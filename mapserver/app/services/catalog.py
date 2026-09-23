import math
import re
import sqlite3

from ..utils.database import all_rows, one, placeholders
from .regions import reverse


POI_FIELDS = """
poi_id AS id, name, name_zh AS nameZh, aliases, category, subcategory, longitude, latitude,
address, website, opening_hours AS openingHours, wikidata, importance
"""


def search_airports(keyword, limit=20):
    if not keyword:
        return []
    like, upper = f"%{keyword[:80]}%", keyword.upper()
    return all_rows(
        "SELECT icao, iata, name, city, attr, longitude, latitude FROM airport "
        "WHERE name LIKE ? OR icao LIKE ? OR iata LIKE ? OR city LIKE ? "
        "ORDER BY CASE WHEN name = ? OR icao = ? OR iata = ? THEN 0 ELSE 1 END, name LIMIT ?",
        (like, f"%{upper}%", f"%{upper}%", like, keyword, upper, upper, limit),
    )


def search_stations(keyword, limit=20):
    if not keyword:
        return []
    like, upper = f"%{keyword[:80]}%", keyword.upper()
    return all_rows(
        "SELECT name, code, city, region, province, longitude, latitude FROM train_stations "
        "WHERE name LIKE ? OR code LIKE ? OR city LIKE ? "
        "ORDER BY CASE WHEN name = ? OR code = ? THEN 0 ELSE 1 END, name LIMIT ?",
        (like, f"%{upper}%", like, keyword, upper, limit),
    )


def by_values(table, field, values, columns, maximum):
    values = list(dict.fromkeys(values or []))[:maximum]
    return all_rows(f"SELECT {columns} FROM {table} WHERE {field} IN ({placeholders(values)})", values) if values else []


def search_pois(keyword, category=None, limit=20):
    keyword = " ".join(keyword.split())[:80]
    if not keyword:
        return []
    terms = [term for term in re.split(r"\s+", keyword) if term]
    if all(len(term) >= 3 for term in terms):
        expression = " AND ".join(f'"{term.replace(chr(34), chr(34) * 2)}"' for term in terms)
        try:
            rows = all_rows(
                "SELECT p.poi_id AS id, p.name, p.name_zh AS nameZh, p.aliases, p.category, p.subcategory, "
                "p.longitude, p.latitude, p.address, p.website, p.opening_hours AS openingHours, "
                "p.wikidata, p.importance FROM poi_fts JOIN poi p ON p.poi_id = poi_fts.rowid "
                "WHERE poi_fts MATCH ? AND (? IS NULL OR p.category = ?) "
                "ORDER BY CASE WHEN p.name = ? OR p.name_zh = ? THEN 0 "
                "WHEN p.name LIKE ? OR p.name_zh LIKE ? THEN 1 ELSE 2 END, "
                "bm25(poi_fts), p.importance DESC, p.name LIMIT ?",
                (expression, category, category, keyword, keyword, f"{keyword}%", f"{keyword}%", limit),
            )
            if rows:
                return rows
        except sqlite3.OperationalError:
            pass

    conditions = []
    parameters = []
    for term in terms:
        like = f"%{term}%"
        conditions.append("(name LIKE ? OR name_zh LIKE ? OR aliases LIKE ? OR address LIKE ?)")
        parameters.extend((like, like, like, like))
    prefix = f"{keyword}%"
    return all_rows(
        f"SELECT {POI_FIELDS} FROM poi WHERE {' AND '.join(conditions)} "
        "AND (? IS NULL OR category = ?) "
        "ORDER BY CASE WHEN name = ? OR name_zh = ? THEN 0 "
        "WHEN name LIKE ? OR name_zh LIKE ? THEN 1 "
        "WHEN name LIKE ? OR name_zh LIKE ? THEN 2 "
        "WHEN aliases LIKE ? THEN 3 ELSE 4 END, importance DESC, name LIMIT ?",
        (*parameters, category, category, keyword, keyword, prefix, prefix,
         f"%{keyword}%", f"%{keyword}%", f"%{keyword}%", limit),
    )


def forward_pois(keyword, limit):
    results = []
    for poi in search_pois(keyword, limit=limit):
        location = reverse(poi["longitude"], poi["latitude"])
        results.append({
            "type": "poi", "id": f"poi:{poi['id']}", "name": poi["nameZh"] or poi["name"],
            "displayName": poi["address"] or poi["nameZh"] or poi["name"],
            "longitude": poi["longitude"], "latitude": poi["latitude"], "category": poi["category"],
            "subcategory": poi["subcategory"], "address": poi["address"],
            "region": {key: location[key] for key in ("province", "city", "district", "formattedRegion")},
        })
    return results


def _distance(longitude, latitude, other_longitude, other_latitude):
    radians = math.radians
    latitude_delta = radians(other_latitude - latitude)
    longitude_delta = radians(other_longitude - longitude)
    value = math.sin(latitude_delta / 2) ** 2 + math.cos(radians(latitude)) * math.cos(radians(other_latitude)) * math.sin(longitude_delta / 2) ** 2
    return round(6371008.8 * 2 * math.atan2(math.sqrt(value), math.sqrt(1 - value)))


def nearby_pois(longitude, latitude, radius, category, limit):
    latitude_delta = radius / 111320
    longitude_delta = radius / max(111320 * math.cos(math.radians(latitude)), 1000)
    rows = all_rows(
        f"SELECT {POI_FIELDS.replace('poi_id', 'p.poi_id')} FROM poi_rtree r JOIN poi p ON p.poi_id = r.poi_id "
        "WHERE r.min_lon BETWEEN ? AND ? AND r.min_lat BETWEEN ? AND ? AND (? IS NULL OR p.category = ?) "
        "ORDER BY p.importance DESC LIMIT 1000",
        (longitude - longitude_delta, longitude + longitude_delta, latitude - latitude_delta, latitude + latitude_delta, category, category),
    )
    for row in rows:
        row["distance"] = _distance(longitude, latitude, row["longitude"], row["latitude"])
    return sorted((row for row in rows if row["distance"] <= radius), key=lambda row: (row["distance"], -row["importance"]))[:limit]


def poi_by_id(identifier):
    return one(f"SELECT {POI_FIELDS} FROM poi WHERE poi_id = ?", (identifier,))
