import json
from functools import lru_cache

from ..utils.database import all_rows, one
from ..utils.geometry import contains, representative_point


BOUNDARY_FIELDS = """
code, name, level, parent_code AS parentCode, geometry_json AS geometryJson,
min_lon AS minLon, max_lon AS maxLon, min_lat AS minLat, max_lat AS maxLat,
data_version AS dataVersion
"""
JOINED_BOUNDARY_FIELDS = """
b.code, b.name, b.level, b.parent_code AS parentCode, b.geometry_json AS geometryJson,
b.min_lon AS minLon, b.max_lon AS maxLon, b.min_lat AS minLat, b.max_lat AS maxLat,
b.data_version AS dataVersion
"""


def region_record(code):
    return one("SELECT code, name, level, type, parent_code AS parentCode FROM sys_area WHERE code = ?", (code,)) or one(
        "SELECT code, name, level, NULL AS type, parent_code AS parentCode "
        "FROM region_boundary WHERE code = ? ORDER BY level DESC LIMIT 1", (code,)
    )


def full_name(code):
    names = []
    visited = set()
    while code and code != "0" and code not in visited:
        visited.add(code)
        region = region_record(code)
        if not region:
            break
        names.append(region["name"])
        code = region["parentCode"]
    return "".join(reversed(names))


def boundary_row(code):
    return one(f"SELECT {BOUNDARY_FIELDS} FROM region_boundary WHERE code = ? ORDER BY level DESC LIMIT 1", (code,))


def boundary_feature(row):
    if not row:
        return None
    return {
        "type": "Feature",
        "id": row["code"],
        "bbox": [row["minLon"], row["minLat"], row["maxLon"], row["maxLat"]],
        "properties": {
            "code": row["code"], "name": row["name"], "level": row["level"],
            "parentCode": row["parentCode"], "dataVersion": row["dataVersion"],
        },
        "geometry": json.loads(row["geometryJson"]),
    }


def search(keyword, level=0, limit=20):
    like = f"%{keyword[:80]}%"
    rows = all_rows(
        "SELECT code, name, level, type, parent_code AS parentCode FROM sys_area "
        "WHERE (name LIKE ? OR code LIKE ?) AND (? = 0 OR level = ?) "
        "ORDER BY CASE WHEN name = ? OR code = ? THEN 0 ELSE 1 END, level, code LIMIT ?",
        (like, like, level, level, keyword, keyword, limit),
    )
    for row in rows:
        row["fullName"] = full_name(row["code"])
    return rows


def search_boundaries(keyword, level=0, limit=20):
    like = f"%{keyword[:80]}%"
    rows = all_rows(
        f"SELECT {BOUNDARY_FIELDS} FROM region_boundary "
        "WHERE (name LIKE ? OR code LIKE ?) AND (? = 0 OR level = ?) "
        "ORDER BY CASE WHEN name = ? OR code = ? THEN 0 ELSE 1 END, level DESC, name LIMIT ?",
        (like, like, level, level, keyword, keyword, limit),
    )
    results = []
    for row in rows:
        feature = boundary_feature(row)
        longitude, latitude = representative_point(feature)
        results.append({
            "type": "region", "id": f"region:{row['level']}:{row['code']}", "name": row["name"],
            "displayName": full_name(row["code"]) or row["name"], "longitude": longitude, "latitude": latitude,
            "region": {"code": row["code"], "level": row["level"], "parentCode": row["parentCode"]},
        })
    return results


@lru_cache(maxsize=5000)
def _reverse_cached(longitude, latitude):
    rows = all_rows(
        f"SELECT {JOINED_BOUNDARY_FIELDS} FROM region_boundary_rtree r "
        "JOIN region_boundary b ON b.boundary_id = r.boundary_id "
        "WHERE r.min_lon <= ? AND r.max_lon >= ? AND r.min_lat <= ? AND r.max_lat >= ? "
        "ORDER BY b.level DESC, b.bbox_area ASC",
        (longitude, longitude, latitude, latitude),
    )
    matched = [row for row in rows if contains(json.loads(row["geometryJson"]), [longitude, latitude])]
    levels = {}
    for row in matched:
        levels.setdefault(row["level"], row)
    province, city, district = levels.get(1), levels.get(2), levels.get(3)
    if district and (district["code"] in {province and province["code"], city and city["code"]}
                     or district["name"] in {province and province["name"], city and city["name"]}):
        district = None

    def summary(row):
        return {"code": row["code"], "name": row["name"]} if row else None

    names = []
    for row in (province, city, district):
        if row and (not names or names[-1] != row["name"]):
            names.append(row["name"])
    source = district or city or province
    return {
        "longitude": longitude, "latitude": latitude, "province": summary(province), "city": summary(city),
        "district": summary(district), "formattedRegion": "".join(names),
        "dataVersion": source["dataVersion"] if source else None,
    }


def reverse(longitude, latitude):
    return _reverse_cached(round(longitude, 5), round(latitude, 5))
