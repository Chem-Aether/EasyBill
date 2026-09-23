from fastapi.testclient import TestClient
import math

import mapbox_vector_tile

from main import app


client = TestClient(app)


def test_catalog_search_is_keyword_driven_and_limited():
    empty_airports = client.get("/api/airports/search")
    assert empty_airports.status_code == 200
    assert empty_airports.json()["data"] == []

    stations = client.get("/api/stations/search", params={"keyword": "南京", "limit": 1})
    assert stations.status_code == 200
    assert len(stations.json()["data"]) <= 1

    invalid_limit = client.get("/api/airports/search", params={"keyword": "北京", "limit": 51})
    assert invalid_limit.status_code == 422


def test_geocoding_and_boundaries():
    reverse = client.get("/api/geocode/reverse", params={"lng": 118.8489, "lat": 32.0416})
    assert reverse.status_code == 200
    assert reverse.json()["data"]["district"]["code"] == "320102"

    forward = client.get("/api/geocode/forward", params={"q": "中山陵", "type": "poi", "limit": 2})
    assert forward.status_code == 200
    assert forward.json()["data"][0]["name"] == "中山陵"
    assert forward.json()["data"][0]["region"]["formattedRegion"]

    partial = client.get("/api/geocode/forward", params={"q": "中山", "type": "poi", "limit": 5})
    assert partial.status_code == 200
    assert any("中山" in item["name"] for item in partial.json()["data"])

    multiple_terms = client.get("/api/geocode/forward", params={"q": "南京 博物院", "type": "poi", "limit": 5})
    assert multiple_terms.status_code == 200
    assert any("博物" in item["name"] for item in multiple_terms.json()["data"])

    boundary = client.post("/api/regions/boundaries/by-codes", json={"codes": ["3201"]})
    assert boundary.status_code == 200
    assert boundary.headers["content-type"].startswith("application/geo+json")
    assert boundary.json()["type"] == "FeatureCollection"


def _tile_at(longitude, latitude, zoom):
    x = int((longitude + 180) / 360 * (1 << zoom))
    y = int((1 - math.asinh(math.tan(math.radians(latitude))) / math.pi) / 2 * (1 << zoom))
    return zoom, x, y


def test_unified_tile_gateway_selects_sources_and_overzooms():
    tilejson = client.get("/api/tiles/tilejson.json")
    assert tilejson.status_code == 200
    assert tilejson.json()["tiles"][0].endswith("/api/tiles/{z}/{x}/{y}.mvt")

    world = client.get("/api/tiles/0/0/0.mvt")
    assert world.status_code == 200
    assert world.headers["x-map-source"] == "world"
    assert world.headers["x-map-overzoom"] == "false"

    z, x, y = _tile_at(118.8, 32.0, 14)
    china = client.get(f"/api/tiles/{z}/{x}/{y}.mvt")
    assert china.status_code == 200
    assert china.headers["x-map-source"] == "china"

    z, x, y = _tile_at(118.8, 32.0, 15)
    overzoom = client.get(f"/api/tiles/{z}/{x}/{y}.mvt")
    assert overzoom.status_code == 200
    assert overzoom.headers["x-map-source"] == "china"
    assert overzoom.headers["x-map-overzoom"] == "true"
    decoded = mapbox_vector_tile.decode(overzoom.content)
    assert isinstance(decoded, dict)
