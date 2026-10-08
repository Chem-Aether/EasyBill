# EastBill

个人使用的旅行、账单和日记系统，采用单仓库、多独立服务结构。

- `vue-project/`：Vue 3 前端
- `springboot/auth-service/`：身份认证，8081
- `springboot/bill-service/`：账单服务，8083
- `springboot/travel-service/`：旅行服务，8084
- `springboot/diary-service/`：日记服务，8085
- `mapserver/`：离线地图和地理 API，8765
- `spyderserver/`：外部列车信息查询，8082

每个后端服务可单独启动和初始化。服务数据库初始化说明、IDEA 入口及地图数据说明见 [开发文档](docs/开发文档.md) 和 [后端服务说明](springboot/README.md)。
