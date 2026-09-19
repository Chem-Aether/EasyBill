import { existsSync, renameSync, rmSync } from 'node:fs'
import { spawnSync } from 'node:child_process'
import { join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { DatabaseSync } from 'node:sqlite'

const root = fileURLToPath(new URL('.', import.meta.url))
const target = join(root, 'geo.sqlite')
const temporary = `${target}.tmp`
const mysql = process.env.MYSQL_BIN || 'mysql'
const databaseName = process.env.MYSQL_DATABASE || 'springdatabase'
const mysqlArgs = [
  `--host=${process.env.MYSQL_HOST || '127.0.0.1'}`,
  `--port=${process.env.MYSQL_PORT || '3306'}`,
  `--user=${process.env.MYSQL_USER || 'root'}`,
  `--database=${databaseName}`,
  '--default-character-set=utf8mb4', '--batch', '--raw', '--skip-column-names',
]

function mysqlRows(query) {
  const result = spawnSync(mysql, [...mysqlArgs, '--execute', query], {
    encoding: 'utf8',
    env: { ...process.env, MYSQL_PWD: process.env.MYSQL_PASSWORD || '123456' },
    maxBuffer: 64 * 1024 * 1024,
  })
  if (result.status !== 0) throw new Error(result.stderr || `MySQL 导出失败，退出码 ${result.status}`)
  return result.stdout.split(/\r?\n/).filter(Boolean).map(line => JSON.parse(line))
}

const airports = mysqlRows(`SELECT JSON_OBJECT('icao',icao,'iata',iata,'name',name,'attr',attr,'longitude',longitude,'latitude',latitude,'level',level,'city',city,'type',type) FROM airport`)
const stations = mysqlRows(`SELECT JSON_OBJECT('id',id,'name',name,'code',code,'city',city,'region',region,'province',province,'latitude',latitude,'longitude',longitude) FROM train_stations`)
const regions = mysqlRows(`SELECT JSON_OBJECT('code',code,'name',name,'level',level,'type',type,'parentCode',parent_code) FROM sys_area`)

if (existsSync(temporary)) rmSync(temporary)
const database = new DatabaseSync(temporary)
database.exec(`
  PRAGMA journal_mode = DELETE;
  CREATE TABLE airport (
    icao TEXT PRIMARY KEY, iata TEXT UNIQUE, name TEXT NOT NULL, attr TEXT,
    longitude REAL NOT NULL, latitude REAL NOT NULL, level TEXT, city TEXT, type TEXT
  );
  CREATE INDEX idx_airport_city ON airport(city);
  CREATE TABLE train_stations (
    id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE, code TEXT, city TEXT, region TEXT,
    province TEXT, latitude REAL, longitude REAL
  );
  CREATE INDEX idx_station_code ON train_stations(code);
  CREATE INDEX idx_station_city ON train_stations(city);
  CREATE TABLE sys_area (
    code TEXT PRIMARY KEY, name TEXT NOT NULL, level INTEGER NOT NULL, type TEXT, parent_code TEXT
  );
  CREATE INDEX idx_area_parent ON sys_area(parent_code);
  CREATE INDEX idx_area_level ON sys_area(level);
`)

const insertAirport = database.prepare('INSERT INTO airport VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)')
const insertStation = database.prepare('INSERT INTO train_stations VALUES (?, ?, ?, ?, ?, ?, ?, ?)')
const insertRegion = database.prepare('INSERT INTO sys_area VALUES (?, ?, ?, ?, ?)')
database.exec('BEGIN')
try {
  for (const row of airports) insertAirport.run(row.icao, row.iata, row.name, row.attr, row.longitude, row.latitude, row.level, row.city, row.type)
  for (const row of stations) insertStation.run(row.id, row.name, row.code, row.city, row.region, row.province, row.latitude, row.longitude)
  for (const row of regions) insertRegion.run(row.code, row.name, row.level, row.type, row.parentCode)
  database.exec('COMMIT')
} catch (error) {
  database.exec('ROLLBACK')
  throw error
} finally {
  database.close()
}

if (existsSync(target)) rmSync(target)
renameSync(temporary, target)
console.log(`迁移完成: airport=${airports.length}, train_stations=${stations.length}, sys_area=${regions.length}`)
