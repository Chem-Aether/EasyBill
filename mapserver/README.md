# EastBill 离线地理信息服务

独立的 Python/FastAPI 服务，统一提供矢量瓦片、机场、铁路车站、POI、行政区边界和地理编码。业务前后端只依赖 HTTP 接口，不直接读取地理数据文件。

## 工程结构

```text
mapserver/
├─ main.py                    服务启动入口
├─ app/
│  ├─ settings.py             JSON 配置加载与环境变量覆盖
│  ├─ routers/                HTTP 路由
│  ├─ services/               查询与地理编码逻辑
│  ├─ tiles/                  PMTiles 选源、读取、overzoom 和缓存
│  └─ utils/                  数据库、GeoPackage、模型与响应
├─ config/
│  └─ application.json        服务、存储、CORS 和地图数据源配置
├─ scripts/
│  ├─ import_osm_poi.py       OSM PBF → POI GeoPackage/FTS/RTree
│  └─ import_admin_boundaries.py   GeoJSON → 行政区 GeoPackage/RTree
├─ data/
│  ├─ airports.gpkg           机场点数据
│  ├─ poi.gpkg                POI 点数据、FTS/RTree
│  ├─ china_admin_boundaries.gpkg 行政区目录、边界和空间索引
│  ├─ china_railway_network.gpkg 铁路车站和线路
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
$env:MAP_POI = 'D:\geo-data\poi.gpkg'
$env:MAP_ADMIN_BOUNDARIES = 'D:\geo-data\china_admin_boundaries.gpkg'
$env:MAP_AIRPORTS = 'D:\geo-data\airports.gpkg'
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
.\.venv\Scripts\python.exe scripts\import_admin_boundaries.py
```

默认读取 `data/source/boundaries` 下的 `中国_省.geojson`、`中国_市.geojson` 和 `中国_县.geojson`，独立生成 `data/china_admin_boundaries.gpkg`。源文件路径属于导入工具配置，不放在服务运行配置 `config/application.json` 中；也可通过 `--province`、`--city`、`--district` 指定新文件。文件集中保存行政区编码、名称、层级、类型、父级、几何和 RTree 空间索引；重导入会保留已有编码对应的类型和父级信息。

更新全国 POI：

```powershell
.\.venv\Scripts\python.exe scripts\import_osm_poi.py
```

默认读取 `data/source/osm` 下文件名最大的 `china-*.osm.pbf`。POI 导入器独立构建并校验临时 GeoPackage（包含 FTS5 全文索引和 RTree 空间索引），再替换 `data/poi.gpkg`；行政区导入器独立构建并校验临时 GeoPackage，再替换 `data/china_admin_boundaries.gpkg`。更新 POI 数据时建议先停止地图服务，完成导入后再启动。


机场点位存储在 `data/airports.gpkg` 的 `airports` 点图层（EPSG:4326）中，可直接用 QGIS 编辑；POI 存储在 `data/poi.gpkg` 的 `poi` 点图层（EPSG:4326）中，可在 QGIS 查看与编辑。POI 包内保留 OSM 来源字段、FTS5 全文索引和 GeoPackage RTree 空间索引。空间编辑会通过 GeoPackage 触发器同步空间索引与经纬度字段，属性编辑会维护全文索引。

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
- `GET /api/railway/stations/search`

边界接口直接返回标准 GeoJSON；其余接口保持 `{ "msg": "操作成功", "data": ... }` 响应格式。

POI 和底图数据来自 OpenStreetMap contributors，遵循 ODbL 1.0；展示或分发时必须保留署名。
