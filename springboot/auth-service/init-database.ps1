param(
  [string]$Psql = 'C:\Database\PostgreSQL\17\bin\psql.exe',
  [string]$User = 'postgres',
  [string]$Password = '123456'
)
$ErrorActionPreference = 'Stop'
$env:PGPASSWORD = $Password
try {
  & $Psql -U $User -d postgres -v ON_ERROR_STOP=1 -f "$PSScriptRoot\database\init.sql"
  if ($LASTEXITCODE -ne 0) { throw 'Failed to create eastbill_identity' }
  & $Psql -U $User -d eastbill_identity -v ON_ERROR_STOP=1 -f "$PSScriptRoot\src\main\resources\schema.sql"
  if ($LASTEXITCODE -ne 0) { throw 'Failed to create auth schema' }
} finally {
  Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}
Write-Host 'eastbill_identity initialized and ready for auth-service.'
