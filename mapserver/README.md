# EastBill 离线地图与地理数据服务

本目录负责两类离线资源：

- `world.pmtiles`、`china.pmtiles`：矢量地图。
- `geo.sqlite`：机场、火车站和三级行政区基础数据。

## 首次迁移

需要 Node.js 22.5+ 和 MySQL 命令行客户端。PowerShell 示例：

```powershell
$env:MYSQL_BIN = 'C:\Database\MySQL\MySQL Server 9.5\bin\mysql.exe'
$env:MYSQL_PASSWORD = '123456'
npm run migrate
```

可选变量：`MYSQL_HOST`、`MYSQL_PORT`、`MYSQL_USER`、`MYSQL_PASSWORD`、`MYSQL_DATABASE`。

## 启动

```powershell
npm start
```

默认监听 `http://127.0.0.1:8765`。除 PMTiles Range 请求外，还提供：

- `GET /api/airports/search?keyword=...`
- `GET /api/stations/search?keyword=...`
- `GET /api/regions/search?keyword=...&level=3`
- `GET /api/regions/{code}`
- `POST /api/airports/by-codes`
- `POST /api/stations/by-names`
- `POST /api/regions/by-codes`
