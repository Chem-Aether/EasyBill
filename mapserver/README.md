# EastBill 离线地理信息服务

独立的 Python/FastAPI 服务，统一提供 PMTiles、机场、车站、POI、行政区边界和地理编码。业务前后端只依赖 HTTP 接口，不直接读取地理数据文件。

## 工程结构

```text
mapserver/
├─ app/
│  ├─ main.py                 FastAPI 应用和中间件
│  ├─ config.py               环境变量及数据路径
│  ├─ database.py             SQLite 只读连接
│  ├─ geometry.py             Polygon/MultiPolygon 点面算法
│  ├─ routers/                HTTP 路由
│  └─ services/               查询与地理编码逻辑
├─ scripts/
│  ├─ import_osm_poi.py       OSM PBF → POI/FTS/RTree
│  └─ import_region_boundaries.py  GeoJSON → 行政区/RTree
├─ data/
│  ├─ geo.sqlite              运行数据库
│  ├─ world.pmtiles
│  ├─ china.pmtiles
│  └─ source/                 可归档的原始 PBF/GeoJSON
├─ requirements.txt
└─ run.ps1
```

`data/source` 只在重新导入时需要，服务运行不读取它。

## 安装

需要 Python 3.11+：

```powershell
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
```

## 启动

```powershell
.\run.ps1
```

默认地址为 `http://127.0.0.1:8765`，交互式接口文档为 `http://127.0.0.1:8765/docs`。

可选环境变量：

```powershell
$env:MAP_HOST = '127.0.0.1'
$env:MAP_PORT = '8765'
$env:MAP_DATA_DIR = 'D:\geo-data'
$env:MAP_DATABASE = 'D:\geo-data\geo.sqlite'
```

## 数据更新

更新行政区边界：

```powershell
.\.venv\Scripts\python.exe scripts\import_region_boundaries.py
```

默认读取 `data/source/boundaries` 下的 `china.json`、`中国_市.json` 和 `中国_县.json`。

更新全国 POI：

```powershell
.\.venv\Scripts\python.exe scripts\import_osm_poi.py
```

默认读取 `data/source/osm` 下文件名最大的 `china-*.osm.pbf`。两个导入器都先在临时数据库中构建和校验，成功后才替换 `data/geo.sqlite`。

## 接口

完整定义以 `/docs` 和 `/openapi.json` 为准。主要接口：

- `GET /world.pmtiles`
- `GET /china.pmtiles`
- `GET /api/geocode/forward`
- `GET /api/geocode/reverse`
- `POST /api/geocode/reverse/batch`
- `GET /api/regions/search`
- `GET /api/regions/{code}`
- `GET /api/regions/{code}/boundary`
- `POST /api/regions/by-codes`
- `POST /api/regions/boundaries/by-codes`
- `GET /api/pois/search`
- `GET /api/pois/nearby`
- `GET /api/pois/stats`
- `GET /api/pois/{id}`
- `GET /api/airports/search`
- `POST /api/airports/by-codes`
- `GET /api/stations/search`
- `POST /api/stations/by-names`

边界接口直接返回标准 GeoJSON；其余接口保持 `{ "msg": "操作成功", "data": ... }` 响应格式。

POI 和底图数据来自 OpenStreetMap contributors，遵循 ODbL 1.0；展示或分发时必须保留署名。
