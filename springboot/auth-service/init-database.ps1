param(
  [string]$MySql = 'C:\Database\MySQL\MySQL Server 9.5\bin\mysql.exe',
  [string]$User = 'root',
  [string]$Password = '123456'
)
$ErrorActionPreference = 'Stop'
Get-Content -Raw "$PSScriptRoot\database\init.sql" |
  & $MySql "-u$User" "-p$Password" --default-character-set=utf8mb4
if ($LASTEXITCODE -ne 0) { throw 'Failed to initialize eastbill_identity' }
Get-Content -Raw "$PSScriptRoot\src\main\resources\schema.sql" |
  & $MySql "-u$User" "-p$Password" --default-character-set=utf8mb4 eastbill_identity
if ($LASTEXITCODE -ne 0) { throw 'Failed to create auth schema' }
Write-Host 'eastbill_identity initialized and ready for auth-service.'
