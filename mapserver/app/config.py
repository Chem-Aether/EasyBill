import os
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
DATA_DIR = Path(os.getenv("MAP_DATA_DIR", ROOT / "data")).resolve()
DATABASE_PATH = Path(os.getenv("MAP_DATABASE", DATA_DIR / "geo.sqlite")).resolve()
HOST = os.getenv("MAP_HOST", "127.0.0.1")
PORT = int(os.getenv("MAP_PORT", "8765"))
MAP_FILES = {"/world.pmtiles": DATA_DIR / "world.pmtiles", "/china.pmtiles": DATA_DIR / "china.pmtiles"}
MAX_BODY_BYTES = 1024 * 1024
