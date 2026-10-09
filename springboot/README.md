# EastBill backend services

The backend is a Maven multi-module project. Each service has its own process,
port, datasource and deployment lifecycle.

| Module | Port | Database | Responsibility |
| --- | ---: | --- | --- |
| `auth-service` | 8081 | PostgreSQL `eastbill_identity` | users, login, captcha, JWT |
| `bill-service` | 8083 | PostgreSQL `eastbill_bill` | accounts, categories, bills |
| `travel-service` | 8084 | PostgreSQL `travel_database` | flights, trains, footprints |
| `diary-service` | 8085 | PostgreSQL `diary_database` | reserved diary boundary |

Map data remains in the FastAPI `mapserver` on port 8765. External network
collection remains in `spyderserver` on port 8082. Only `travel-service`
communicates with those services.

## Independent database initialization

Each service owns its database scripts. Initialize only the service you want to
run; no other Java service needs to be running.

```powershell
cd auth-service
.\init-database.ps1

cd ..\bill-service
.\init-database.ps1

cd ..\travel-service
.\init-database.ps1

cd ..\diary-service
.\init-database.ps1
```

The auth initializer creates the PostgreSQL database if needed and idempotently
creates its table; it preserves existing users. Bill initialization is also
idempotent. Travel and diary scripts reset their databases. The auth, bill and
travel services apply or verify their schemas during startup. Mapserver databases
and geographic source files are not touched.

## Start in IDEA

Import `springboot/pom.xml` as a Maven project. Each application is an entirely
independent run configuration; start only the one needed:

- `com.AuthServiceApplication`
- `com.BillServiceApplication`
- `com.TravelServiceApplication`
- `com.DiaryServiceApplication`

The environment variables in each `application.yml` can override ports,
database credentials and downstream service addresses.

Build all modules with:

```powershell
.\mvnw.cmd -s .mvn\settings.xml -DskipTests package
```

The Vue app selects the correct service through `VITE_AUTH_BASE_URL`,
`VITE_BILL_BASE_URL`, `VITE_TRAVEL_BASE_URL`, `VITE_DIARY_BASE_URL` and
`VITE_MAP_BASE_URL`. Business modules never hard-code a host or port.
