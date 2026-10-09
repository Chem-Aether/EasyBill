param(
  [string]$Psql = 'psql',
  [string]$User = 'postgres',
  [string]$Password = '123456',
  [string]$HostName = 'localhost',
  [int]$Port = 5432
)
$ErrorActionPreference = 'Stop'
$env:PGPASSWORD = $Password
& $Psql -h $HostName -p $Port -U $User -d postgres -f "$PSScriptRoot\database\init.sql"
if ($LASTEXITCODE -ne 0) { throw 'Failed to initialize eastbill_bill' }
& $Psql -h $HostName -p $Port -U $User -d eastbill_bill -f "$PSScriptRoot\src\main\resources\schema.sql"
if ($LASTEXITCODE -ne 0) { throw 'Failed to create bill schema' }
Remove-Item Env:PGPASSWORD
Write-Host 'PostgreSQL database eastbill_bill is initialized.'
