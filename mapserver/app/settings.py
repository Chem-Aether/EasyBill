import os
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CONFIG_PATH = Path(os.getenv("MAP_CONFIG", ROOT / "config" / "application.json")).resolve()


def _load_config() -> dict:
    with CONFIG_PATH.open("r", encoding="utf-8") as stream:
        return json.load(stream)


def _resolve_path(value: str) -> Path:
    path = Path(value)
    return (path if path.is_absolute() else ROOT / path).resolve()


CONFIG = _load_config()
SERVER = CONFIG.get("server", {})
STORAGE = CONFIG.get("storage", {})
TILES = CONFIG.get("tiles", {})
CORS = SERVER.get("cors", {})
IMPORTS = STORAGE.get("imports", {})
BOUNDARY_FILES = IMPORTS.get("boundary_files", {})

HOST = os.getenv("MAP_HOST", str(SERVER.get("host", "127.0.0.1")))
PORT = int(os.getenv("MAP_PORT", SERVER.get("port", 8765)))
DATA_DIR = _resolve_path(os.getenv("MAP_DATA_DIR", str(STORAGE.get("data_dir", "data"))))
DATABASE_PATH = _resolve_path(os.getenv("MAP_DATABASE", str(STORAGE.get("database", "data/geo.sqlite"))))
OSM_SOURCE_DIR = _resolve_path(str(IMPORTS.get("osm_dir", "data/source/osm")))
BOUNDARY_SOURCE_DIR = _resolve_path(str(IMPORTS.get("boundary_dir", "data/source/boundaries")))
PROVINCE_BOUNDARY_FILE = str(BOUNDARY_FILES.get("province", "china.json"))
CITY_BOUNDARY_FILE = str(BOUNDARY_FILES.get("city", "中国_市.json"))
DISTRICT_BOUNDARY_FILE = str(BOUNDARY_FILES.get("district", "中国_县.json"))
TILE_CACHE_SIZE = int(os.getenv("MAP_TILE_CACHE_SIZE", TILES.get("cache_size", 512)))
TILE_MIN_ZOOM = int(TILES.get("min_zoom", 0))
TILE_MAX_ZOOM = int(TILES.get("max_zoom", 18))
CORS_ORIGINS = CORS.get("origins", ["*"])
CORS_METHODS = CORS.get("methods", ["GET", "POST", "HEAD", "OPTIONS"])
CORS_HEADERS = CORS.get("headers", ["*"])


def load_tile_sources() -> list[dict]:
    return _load_config().get("tiles", {}).get("sources", [])
