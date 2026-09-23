from fastapi import APIRouter, HTTPException, Query

from ..utils.database import all_rows, one
from ..utils.models import NamesRequest, ValuesRequest
from ..utils.responses import payload
from ..services import catalog


router = APIRouter(prefix="/api", tags=["catalog"])


@router.get("/airports/search", summary="搜索机场")
def airports(keyword: str = "", limit: int = Query(20, ge=1, le=50)):
    return payload(catalog.search_airports(keyword.strip(), limit))


@router.post("/airports/by-codes", summary="根据 ICAO/IATA 代码查询机场(可批量)")
def airports_by_codes(body: ValuesRequest):
    return payload(catalog.by_values("airport", "icao", body.codes, "icao, iata, name, city, attr, longitude, latitude", 1000))


@router.get("/stations/search", summary="搜索火车站")
def stations(keyword: str = "", limit: int = Query(20, ge=1, le=50)):
    return payload(catalog.search_stations(keyword.strip(), limit))


@router.post("/stations/by-names", summary="根据火车站名称查询火车站(可批量)")
def stations_by_names(body: NamesRequest):
    return payload(catalog.by_values("train_stations", "name", body.names, "name, code, city, region, province, longitude, latitude", 5000))


@router.get("/pois/search", summary="搜索兴趣点")
def pois(q: str | None = None, keyword: str | None = None, category: str | None = None, limit: int = Query(20, ge=1, le=100)):
    return payload(catalog.search_pois((q or keyword or "").strip(), category, limit))


@router.get("/pois/nearby", summary="查询附近兴趣点")
def nearby(
    longitude: float | None = None, latitude: float | None = None,
    lng: float | None = None, lat: float | None = None,
    radius: float = Query(5000, ge=50, le=100000), category: str | None = None,
    limit: int = Query(20, ge=1, le=100),
):
    longitude = longitude if longitude is not None else lng
    latitude = latitude if latitude is not None else lat
    if longitude is None or latitude is None or not (-180 <= longitude <= 180 and -90 <= latitude <= 90):
        raise HTTPException(400, "经纬度参数无效")
    return payload(catalog.nearby_pois(longitude, latitude, radius, category, limit))


@router.get("/pois/stats", summary="兴趣点统计信息")
def poi_stats():
    total = one("SELECT COUNT(*) AS count FROM poi")["count"]
    categories = all_rows("SELECT category, COUNT(*) AS count FROM poi GROUP BY category ORDER BY count DESC")
    metadata = {row["key"]: row["value"] for row in all_rows("SELECT key, value FROM geo_metadata WHERE key LIKE 'poi.%'")}
    return payload({"total": total, "categories": categories, "metadata": metadata})


@router.get("/pois/{identifier}", summary="根据 ID 查询兴趣点")
def poi(identifier: int):
    result = catalog.poi_by_id(identifier)
    if not result:
        raise HTTPException(404, "POI 不存在")
    return payload(result)
