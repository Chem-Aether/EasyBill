# Bill Service

## Database

PostgreSQL database: `eastbill_bill`. Set `BILL_DB_URL`, `BILL_DB_USERNAME`, and `BILL_DB_PASSWORD` as needed. Initialize it on Windows with:

```powershell
.\init-database.ps1 -User postgres -Password your-password
```

The service runs on port `8083` by default and applies the idempotent `src/main/resources/schema.sql` at startup.

## Ledger model

- `bill_account` contains only owned accounts. `opening_balance` is the balance before tracked transactions; current balance is computed from account inflows and outflows.
- `bill_transaction` stores a positive amount and nullable `from_account_id` / `to_account_id`. Null from-account means income; null to-account means expense; both set means an internal transfer.
- `category` is a transaction label, not a flow type. Category choices are maintained in the browser and are not normalized into a backend category table.
- External parties are kept as `counterparty` text and never become owned accounts automatically.

## API

- `GET/POST /bill/accounts`, `PUT/DELETE /bill/accounts/{id}`
- `GET/POST /bill/records`, `PUT/DELETE /bill/records/{id}`
- `GET /bill/statistics/summary`, `GET /bill/statistics/categories`

The historical workbook is not imported automatically. Account identity and balance semantics should be checked before importing its historical transactions.
