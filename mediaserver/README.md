# Media Service

Standalone Python/FastAPI image service at the repository root, independent from `mapserver` and `spyderserver`.

## Setup and start

```powershell
cd mediaserver
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
.\.venv\Scripts\python.exe main.py
```

Default address: `http://127.0.0.1:8780`; API docs: `/docs`.

## API

- `POST /api/media` multipart fields `file` and optional `category` (`travel`, `diary`, `bill`, `shared`); returns `media_id`, `url`, category, MIME type and size.
- `GET /media/{media_id}` serves the image URL.
- `DELETE /api/media/{media_id}` removes the file and metadata.
- `GET /health` checks service availability.

Example upload from Windows PowerShell:

```powershell
curl.exe -X POST "http://127.0.0.1:8780/api/media" `
  -F "category=travel" `
  -F "file=@D:\Pictures\trip.jpg"
```

JPEG, PNG, WebP and GIF are accepted, up to 20 MiB by default. SVG and other active-content formats are rejected. If `MEDIA_API_KEY` is configured, upload and deletion require the matching `X-Media-Key` header. Keep the service bound to localhost or behind a trusted reverse proxy; do not expose write endpoints publicly without authentication.

Files are stored under category/year/month folders, for example `data/files/travel/2026/10/<id>.jpg`. Categories are configured independently in `config/application.json`; point each category to its own mounted directory when deploying, or override with `MEDIA_STORAGE_TRAVEL`, `MEDIA_STORAGE_DIARY`, `MEDIA_STORAGE_BILL` and `MEDIA_STORAGE_SHARED`. Relative category paths are resolved under `MEDIA_DATA_DIR`; absolute paths can target separate disks/volumes. SQLite stores category plus a relative path; public URLs remain `/media/{media_id}` and do not expose storage layout. On startup, an existing flat `data/files/<id>.<ext>` installation is migrated into `shared/YYYY/MM/` and its metadata table is rebuilt. The database and all configured category folders are excluded from Git; back them up together. Set `MEDIA_DATA_DIR` to move the metadata database outside the repository. `MEDIA_PUBLIC_BASE_URL`, `MEDIA_HOST`, `MEDIA_PORT`, `MEDIA_MAX_UPLOAD_BYTES`, `MEDIA_CORS_ORIGINS` and `MEDIA_CONFIG` can override settings.

Business services should persist `media_id`, not an absolute disk path. The returned URL is for display; the URL base can be changed without rewriting business records.
