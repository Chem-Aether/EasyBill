param(
  [string]$MySql = 'C:\Database\MySQL\MySQL Server 9.5\bin\mysql.exe',
  [string]$User = 'root',
  [string]$Password = '123456'
)
$ErrorActionPreference = 'Stop'
Get-Content -Raw "$PSScriptRoot\database\init.sql" |
  & $MySql "-u$User" "-p$Password" --default-character-set=utf8mb4
if ($LASTEXITCODE -ne 0) { throw 'Failed to initialize eastbill_bill' }
Get-Content -Raw "$PSScriptRoot\src\main\resources\schema.sql" |
  & $MySql "-u$User" "-p$Password" --default-character-set=utf8mb4 eastbill_bill
if ($LASTEXITCODE -ne 0) { throw 'Failed to create bill schema' }
Get-Content -Raw "$PSScriptRoot\src\main\resources\data.sql" |
  & $MySql "-u$User" "-p$Password" --default-character-set=utf8mb4 eastbill_bill
if ($LASTEXITCODE -ne 0) { throw 'Failed to seed bill data' }
Write-Host 'eastbill_bill initialized and ready for bill-service.'
