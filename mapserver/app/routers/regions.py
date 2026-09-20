from fastapi import APIRouter, HTTPException, Query

from ..models import ValuesRequest
from ..responses import geojson, payload
from ..services import regions


router = APIRouter(prefix="/api/regions", tags=["regions"])


@router.get("/search")
def search(keyword: str = "", level: int = Query(0, ge=0, le=3)):
    return payload(regions.search(keyword.strip(), level) if keyword.strip() else [])


@router.post("/boundaries/by-codes")
def boundaries(body: ValuesRequest):
    codes = list(dict.fromkeys(body.codes))[:500]
    features = [regions.boundary_feature(regions.boundary_row(code)) for code in codes]
    return geojson({"type": "FeatureCollection", "features": [feature for feature in features if feature]})


@router.post("/by-codes")
def by_codes(body: ValuesRequest):
    rows = []
    for code in list(dict.fromkeys(body.codes))[:5000]:
        row = regions.region_record(code)
        if row:
            row["fullName"] = regions.full_name(code)
            rows.append(row)
    return payload(rows)


@router.get("/{code}/boundary")
def boundary(code: str):
    feature = regions.boundary_feature(regions.boundary_row(code))
    if not feature:
        raise HTTPException(404, "行政区边界不存在")
    return geojson(feature)


@router.get("/{code}")
def by_code(code: str):
    return payload(regions.region_record(code))
