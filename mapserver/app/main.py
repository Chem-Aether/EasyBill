from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse

from .config import MAP_FILES
from .responses import payload
from .routers import catalog, geocoding, regions


app = FastAPI(
    title="EastBill Geographic Service",
    version="1.0.0",
    description="Offline PMTiles, POI search and administrative geocoding service.",
)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["GET", "POST", "HEAD", "OPTIONS"],
    allow_headers=["*"],
)
app.include_router(geocoding.router)
app.include_router(regions.router)
app.include_router(catalog.router)


@app.get("/health", tags=["system"])
def health():
    return payload({"status": "ok"})


@app.api_route("/{archive}.pmtiles", methods=["GET", "HEAD"], include_in_schema=False)
def map_archive(archive: str):
    path = MAP_FILES.get(f"/{archive}.pmtiles")
    if not path or not path.is_file():
        raise HTTPException(404, "Map archive not found")
    return FileResponse(path, media_type="application/octet-stream", headers={"Cache-Control": "public, max-age=3600"})
