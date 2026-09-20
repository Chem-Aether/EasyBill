from fastapi import APIRouter, HTTPException, Query

from ..utils.models import PointsRequest
from ..utils.responses import payload
from ..services import catalog, regions


router = APIRouter(prefix="/api/geocode", tags=["geocoding"])


def valid(longitude, latitude):
    return -180 <= longitude <= 180 and -90 <= latitude <= 90


@router.get("/reverse")
def reverse(longitude: float | None = None, latitude: float | None = None, lng: float | None = None, lat: float | None = None):
    longitude = longitude if longitude is not None else lng
    latitude = latitude if latitude is not None else lat
    if longitude is None or latitude is None or not valid(longitude, latitude):
        raise HTTPException(400, "经纬度参数无效")
    return payload(regions.reverse(longitude, latitude))


@router.post("/reverse/batch")
def reverse_batch(body: PointsRequest):
    results = []
    for item in body.points:
        try:
            longitude = float(item.get("longitude", item.get("lng")))
            latitude = float(item.get("latitude", item.get("lat")))
            if not valid(longitude, latitude):
                raise ValueError
            results.append({"id": item.get("id"), **regions.reverse(longitude, latitude)})
        except (TypeError, ValueError):
            results.append({"id": item.get("id"), "error": "经纬度参数无效"})
    return payload(results)


@router.get("/forward")
def forward(
    q: str | None = None,
    keyword: str | None = None,
    type: str = Query("all", pattern="^(all|region|poi)$"),
    level: int = Query(0, ge=0, le=3),
    limit: int = Query(10, ge=1, le=50),
):
    text = (q or keyword or "").strip()[:80]
    if not text:
        return payload([])
    found = []
    if type != "poi":
        found.extend(regions.search_boundaries(text, level, limit))
    if type != "region":
        found.extend(catalog.forward_pois(text, limit))
    return payload(found[:limit])
