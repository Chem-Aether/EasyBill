#!/usr/bin/env python3
import argparse
import json
import shutil
import sqlite3
import sys
from datetime import datetime, timezone
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from app.settings import (
    BOUNDARY_SOURCE_DIR,
    CITY_BOUNDARY_FILE,
    DATABASE_PATH,
    DISTRICT_BOUNDARY_FILE,
    PROVINCE_BOUNDARY_FILE,
)


DIRECT_MUNICIPALITIES = {"11", "12", "31", "50"}


def coordinate_bounds(coordinates):
    minimum_longitude = minimum_latitude = float("inf")
    maximum_longitude = maximum_latitude = float("-inf")

    def visit(value):
        nonlocal minimum_longitude, minimum_latitude, maximum_longitude, maximum_latitude
        if len(value) >= 2 and isinstance(value[0], (int, float)) and isinstance(value[1], (int, float)):
            longitude, latitude = value[0], value[1]
            minimum_longitude = min(minimum_longitude, longitude)
            maximum_longitude = max(maximum_longitude, longitude)
            minimum_latitude = min(minimum_latitude, latitude)
            maximum_latitude = max(maximum_latitude, latitude)
            return
        for child in value:
            visit(child)

    visit(coordinates)
    if minimum_longitude == float("inf"):
        raise ValueError("边界没有有效坐标")
    return minimum_longitude, maximum_longitude, minimum_latitude, maximum_latitude


def normalized_code(properties, level):
    if level == 1:
        return str(properties["adcode"]).zfill(6)[:2]
    raw = str(properties["gb"])
    six_digits = raw[-6:].zfill(6)
    if level == 2:
        return six_digits[:2] if six_digits[:2] in DIRECT_MUNICIPALITIES else six_digits[:4]
    return six_digits


def derived_parent(code, level):
    if level == 1:
        return "00"
    if level == 2:
        return code[:2]
    return code[:2] if code[:2] in DIRECT_MUNICIPALITIES else code[:4]


def read_features(path):
    with path.open(encoding="utf-8") as source:
        document = json.load(source)
    if document.get("type") != "FeatureCollection":
        raise ValueError(f"不是 GeoJSON FeatureCollection: {path}")
    return document.get("features", [])


def create_schema(connection):
    connection.executescript("""
        DROP TABLE IF EXISTS region_boundary;
        DROP TABLE IF EXISTS region_boundary_rtree;
        CREATE TABLE region_boundary (
            boundary_id INTEGER PRIMARY KEY AUTOINCREMENT,
            code TEXT NOT NULL,
            name TEXT NOT NULL,
            level INTEGER NOT NULL,
            parent_code TEXT,
            geometry_json TEXT NOT NULL,
            min_lon REAL NOT NULL,
            max_lon REAL NOT NULL,
            min_lat REAL NOT NULL,
            max_lat REAL NOT NULL,
            bbox_area REAL NOT NULL,
            data_version TEXT NOT NULL,
            source_file TEXT NOT NULL,
            UNIQUE(level, code)
        );
        CREATE INDEX idx_boundary_code ON region_boundary(code);
        CREATE INDEX idx_boundary_level ON region_boundary(level);
        CREATE INDEX idx_boundary_parent ON region_boundary(parent_code);
        CREATE VIRTUAL TABLE region_boundary_rtree USING rtree(
            boundary_id, min_lon, max_lon, min_lat, max_lat
        );
    """)


def main():
    parser = argparse.ArgumentParser(description="将省市县 GeoJSON 导入 geo.sqlite 并构建行政区 RTree")
    parser.add_argument("--province", type=Path, default=BOUNDARY_SOURCE_DIR / PROVINCE_BOUNDARY_FILE)
    parser.add_argument("--city", type=Path, default=BOUNDARY_SOURCE_DIR / CITY_BOUNDARY_FILE)
    parser.add_argument("--district", type=Path, default=BOUNDARY_SOURCE_DIR / DISTRICT_BOUNDARY_FILE)
    parser.add_argument("--database", type=Path, default=DATABASE_PATH)
    parser.add_argument("--version", default=datetime.now(timezone.utc).date().isoformat())
    args = parser.parse_args()
    for path in (args.province, args.city, args.district, args.database):
        if not path.is_file():
            parser.error(f"文件不存在: {path}")

    temporary = args.database.with_suffix(".sqlite.boundary.next")
    shutil.copy2(args.database, temporary)
    connection = sqlite3.connect(temporary)
    try:
        create_schema(connection)
        area_rows = connection.execute("SELECT code, level, parent_code FROM sys_area").fetchall()
        area_index = {(str(code), int(level)): parent for code, level, parent in area_rows}
        imported = {1: 0, 2: 0, 3: 0}
        unmatched = {1: 0, 2: 0, 3: 0}
        statement = """
            INSERT OR REPLACE INTO region_boundary (
                code, name, level, parent_code, geometry_json,
                min_lon, max_lon, min_lat, max_lat, bbox_area, data_version, source_file
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        for level, path in ((1, args.province), (2, args.city), (3, args.district)):
            print(f"正在导入 {path.name}...", flush=True)
            for feature in read_features(path):
                geometry = feature.get("geometry")
                properties = feature.get("properties") or {}
                if not geometry or geometry.get("type") not in {"Polygon", "MultiPolygon"}:
                    continue
                code = normalized_code(properties, level)
                name = properties.get("name")
                if not name:
                    continue
                parent = area_index.get((code, level)) or derived_parent(code, level)
                if (code, level) not in area_index:
                    unmatched[level] += 1
                min_lon, max_lon, min_lat, max_lat = coordinate_bounds(geometry["coordinates"])
                connection.execute(statement, (
                    code, name, level, parent,
                    json.dumps(geometry, ensure_ascii=False, separators=(",", ":")),
                    min_lon, max_lon, min_lat, max_lat,
                    (max_lon - min_lon) * (max_lat - min_lat), args.version, path.name,
                ))
                imported[level] += 1

        connection.execute("""
            INSERT INTO region_boundary_rtree
            SELECT boundary_id, min_lon, max_lon, min_lat, max_lat FROM region_boundary
        """)
        total = connection.execute("SELECT COUNT(*) FROM region_boundary").fetchone()[0]
        connection.execute("CREATE TABLE IF NOT EXISTS geo_metadata (key TEXT PRIMARY KEY, value TEXT NOT NULL)")
        metadata = {
            "boundary.version": args.version,
            "boundary.imported_at": datetime.now(timezone.utc).isoformat(),
            "boundary.count": str(total),
            "boundary.crs": "EPSG:4490",
        }
        connection.executemany("INSERT OR REPLACE INTO geo_metadata(key,value) VALUES (?,?)", metadata.items())
        connection.commit()
        integrity = connection.execute("PRAGMA integrity_check").fetchone()[0]
        connection.close()
        if integrity != "ok":
            raise RuntimeError(f"SQLite 完整性检查失败: {integrity}")
        backup = args.database.with_suffix(".sqlite.pre-boundary.bak")
        shutil.copy2(args.database, backup)
        shutil.copyfile(temporary, args.database)
        temporary.unlink()
        print(f"导入完成: province={imported[1]}, city={imported[2]}, district={imported[3]}, total={total}")
        print(f"未在 sys_area 精确匹配: province={unmatched[1]}, city={unmatched[2]}, district={unmatched[3]}")
    except Exception:
        try:
            connection.rollback()
            connection.close()
        except sqlite3.ProgrammingError:
            pass
        print(f"导入失败，临时数据库保留在: {temporary}", file=sys.stderr)
        raise


if __name__ == "__main__":
    main()
