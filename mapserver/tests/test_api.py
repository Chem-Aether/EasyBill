from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


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


def test_pmtiles_range_request():
    response = client.get("/world.pmtiles", headers={"Range": "bytes=0-127"})
    assert response.status_code == 206
    assert len(response.content) == 128
