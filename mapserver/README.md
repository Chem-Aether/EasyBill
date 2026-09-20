# EastBill 离线地理信息服务

独立的 Python/FastAPI 服务，统一提供矢量瓦片、机场、车站、POI、行政区边界和地理编码。业务前后端只依赖 HTTP 接口，不直接读取地理数据文件。

## 工程结构

```text
mapserver/
├─ main.py                    服务启动入口
├─ app/
│  ├─ settings.py             JSON 配置加载与环境变量覆盖
│  ├─ routers/                HTTP 路由
│  ├─ services/               查询与地理编码逻辑
│  ├─ tiles/                  PMTiles 选源、读取、overzoom 和缓存
│  └─ utils/                  数据库、几何算法、模型与响应
├─ config/
│  └─ application.json        服务、存储、CORS 和地图数据源配置
├─ scripts/
│  ├─ import_osm_poi.py       OSM PBF → POI/FTS/RTree
│  └─ import_region_boundaries.py  GeoJSON → 行政区/RTree
├─ data/
│  ├─ geo.sqlite              运行数据库
│  ├─ world.pmtiles
│  ├─ china.pmtiles
│  └─ source/                 可归档的原始 PBF/GeoJSON
├─ requirements.txt
└─ requirements-dev.txt
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
.\.venv\Scripts\python.exe main.py
```

默认地址为 `http://127.0.0.1:8765`，交互式接口文档为 `http://127.0.0.1:8765/docs`。

可选环境变量：

```powershell
$env:MAP_HOST = '127.0.0.1'
$env:MAP_PORT = '8765'
$env:MAP_DATA_DIR = 'D:\geo-data'
$env:MAP_DATABASE = 'D:\geo-data\geo.sqlite'
$env:MAP_CONFIG = 'D:\geo-config\application.json'
$env:MAP_TILE_CACHE_SIZE = '512'
```

## 地图瓦片

前端只使用一个 TileJSON 地址：`GET /api/tiles/tilejson.json`。瓦片请求统一进入
`GET /api/tiles/{z}/{x}/{y}.mvt`，服务按经纬度、缩放级别和 `config/application.json`
中的优先级选择 PMTiles。当前 Z0-Z6 使用全球库，中国区域 Z7-Z14 使用中国库；请求超过数据源最高层级时，服务会裁剪并重编码最近的父级矢量瓦片。

增加新的国家或城市地图时，将 PMTiles 放进数据目录，在 `config/application.json` 的 `tiles.sources` 中增加数据源，随后调用
`POST /api/admin/maps/reload` 即可，不需要修改前端。

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

- `GET /api/tiles/tilejson.json`
- `GET /api/tiles/{z}/{x}/{y}.mvt`
- `GET /api/maps/catalog`
- `GET /api/maps/status`
- `POST /api/admin/maps/reload`
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
