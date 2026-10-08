param(
  [string]$Psql = 'C:\Database\PostgreSQL\17\bin\psql.exe',
  [string]$User = 'postgres',
  [string]$Password = '123456'
)
$ErrorActionPreference = 'Stop'
$env:PGPASSWORD = $Password
try {
  & $Psql -U $User -d postgres -v ON_ERROR_STOP=1 -f "$PSScriptRoot\database\init.sql"
  if ($LASTEXITCODE -ne 0) { throw 'Failed to create travel_database' }
  & $Psql -U $User -d travel_database -v ON_ERROR_STOP=1 -f "$PSScriptRoot\src\main\resources\db\travel-schema.sql"
  if ($LASTEXITCODE -ne 0) { throw 'Failed to create travel schema' }
} finally {
  Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}
Write-Host 'travel_database initialized and ready for travel-service.'
