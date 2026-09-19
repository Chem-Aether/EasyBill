import { createReadStream, statSync } from 'node:fs'
import { createServer } from 'node:http'
import { extname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { DatabaseSync } from 'node:sqlite'

const root = fileURLToPath(new URL('.', import.meta.url))
const port = Number(process.env.MAP_PORT || 8765)
const database = new DatabaseSync(join(root, 'geo.sqlite'), { readOnly: true })
const allowedFiles = new Set(['/world.pmtiles', '/china.pmtiles'])
const jsonHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'Content-Type',
  'Access-Control-Allow-Methods': 'GET,POST,HEAD,OPTIONS',
  'Cache-Control': 'no-store',
  'Content-Type': 'application/json; charset=utf-8',
}

function sendJson(response, status, data, msg = status < 400 ? '操作成功' : '请求失败') {
  response.writeHead(status, jsonHeaders)
  response.end(JSON.stringify({ msg, data }))
}

function searchValue(value) {
  return `%${String(value || '').trim().slice(0, 80)}%`
}

function fullRegionName(code) {
  const names = []
  const visited = new Set()
  let currentCode = code
  const statement = database.prepare('SELECT code, name, parent_code AS parentCode FROM sys_area WHERE code = ?')
  while (currentCode && currentCode !== '0' && !visited.has(currentCode)) {
    visited.add(currentCode)
    const region = statement.get(currentCode)
    if (!region) break
    names.push(region.name)
    currentCode = region.parentCode
  }
  return names.reverse().join('')
}

async function readBody(request) {
  const chunks = []
  let size = 0
  for await (const chunk of request) {
    size += chunk.length
    if (size > 1024 * 1024) throw new Error('请求体过大')
    chunks.push(chunk)
  }
  return chunks.length ? JSON.parse(Buffer.concat(chunks).toString('utf8')) : {}
}

function placeholders(values) {
  return values.map(() => '?').join(',')
}

async function handleApi(request, response, url) {
  const { pathname, searchParams } = url
  if (request.method === 'OPTIONS') {
    response.writeHead(204, jsonHeaders).end()
    return true
  }

  if (request.method === 'GET' && pathname === '/api/airports/search') {
    const keyword = searchParams.get('keyword')?.trim()
    const data = keyword ? database.prepare(`
      SELECT icao, iata, name, city, attr, longitude, latitude FROM airport
      WHERE name LIKE ? OR icao LIKE ? OR iata LIKE ? OR city LIKE ?
      ORDER BY CASE WHEN name = ? OR icao = ? OR iata = ? THEN 0 ELSE 1 END, name LIMIT 20
    `).all(searchValue(keyword), searchValue(keyword.toUpperCase()), searchValue(keyword.toUpperCase()), searchValue(keyword), keyword, keyword.toUpperCase(), keyword.toUpperCase()) : []
    sendJson(response, 200, data)
    return true
  }

  if (request.method === 'GET' && pathname === '/api/stations/search') {
    const keyword = searchParams.get('keyword')?.trim()
    const data = keyword ? database.prepare(`
      SELECT name, code, city, region, province, longitude, latitude FROM train_stations
      WHERE name LIKE ? OR code LIKE ? OR city LIKE ?
      ORDER BY CASE WHEN name = ? OR code = ? THEN 0 ELSE 1 END, name LIMIT 20
    `).all(searchValue(keyword), searchValue(keyword.toUpperCase()), searchValue(keyword), keyword, keyword.toUpperCase()) : []
    sendJson(response, 200, data)
    return true
  }

  if (request.method === 'GET' && pathname === '/api/regions/search') {
    const keyword = searchParams.get('keyword')?.trim()
    const level = Number(searchParams.get('level'))
    const selectedLevel = Number.isInteger(level) ? level : 0
    const rows = keyword ? database.prepare(`
      SELECT code, name, level, type, parent_code AS parentCode FROM sys_area
      WHERE (name LIKE ? OR code LIKE ?) AND (? = 0 OR level = ?)
      ORDER BY CASE WHEN name = ? OR code = ? THEN 0 ELSE 1 END, level, code LIMIT 20
    `).all(searchValue(keyword), searchValue(keyword), selectedLevel, selectedLevel, keyword, keyword) : []
    sendJson(response, 200, rows.map(region => ({ ...region, fullName: fullRegionName(region.code) })))
    return true
  }

  const regionMatch = pathname.match(/^\/api\/regions\/([^/]+)$/)
  if (request.method === 'GET' && regionMatch) {
    const data = database.prepare('SELECT code, name, level, type, parent_code AS parentCode FROM sys_area WHERE code = ?').get(decodeURIComponent(regionMatch[1])) || null
    sendJson(response, 200, data)
    return true
  }

  if (request.method === 'POST' && pathname === '/api/airports/by-codes') {
    const values = [...new Set((await readBody(request)).codes || [])].slice(0, 1000)
    const data = values.length ? database.prepare(`SELECT icao, iata, name, city, attr, longitude, latitude FROM airport WHERE icao IN (${placeholders(values)})`).all(...values) : []
    sendJson(response, 200, data)
    return true
  }

  if (request.method === 'POST' && pathname === '/api/stations/by-names') {
    const values = [...new Set((await readBody(request)).names || [])].slice(0, 5000)
    const data = values.length ? database.prepare(`SELECT name, code, city, region, province, longitude, latitude FROM train_stations WHERE name IN (${placeholders(values)})`).all(...values) : []
    sendJson(response, 200, data)
    return true
  }

  if (request.method === 'POST' && pathname === '/api/regions/by-codes') {
    const values = [...new Set((await readBody(request)).codes || [])].slice(0, 5000)
    const rows = values.length ? database.prepare(`SELECT code, name, level, type, parent_code AS parentCode FROM sys_area WHERE code IN (${placeholders(values)})`).all(...values) : []
    sendJson(response, 200, rows.map(region => ({ ...region, fullName: fullRegionName(region.code) })))
    return true
  }
  return false
}

function serveMap(request, response, pathname) {
  if (!allowedFiles.has(pathname)) return false
  const file = join(root, pathname.slice(1))
  let size
  try {
    size = statSync(file).size
  } catch {
    response.writeHead(404).end('Map archive not found')
    return true
  }
  const headers = {
    'Accept-Ranges': 'bytes', 'Access-Control-Allow-Origin': '*',
    'Cache-Control': 'public, max-age=3600',
    'Content-Type': extname(file) === '.pmtiles' ? 'application/octet-stream' : 'text/plain',
  }
  const match = request.headers.range?.match(/^bytes=(\d+)-(\d*)$/)
  if (match) {
    const start = Number(match[1])
    const end = Math.min(match[2] ? Number(match[2]) : size - 1, size - 1)
    if (start > end || start >= size) {
      response.writeHead(416, { ...headers, 'Content-Range': `bytes */${size}` }).end()
      return true
    }
    response.writeHead(206, { ...headers, 'Content-Length': end - start + 1, 'Content-Range': `bytes ${start}-${end}/${size}` })
    if (request.method === 'HEAD') response.end()
    else createReadStream(file, { start, end }).pipe(response)
    return true
  }
  response.writeHead(200, { ...headers, 'Content-Length': size })
  if (request.method === 'HEAD') response.end()
  else createReadStream(file).pipe(response)
  return true
}

createServer(async (request, response) => {
  try {
    const url = new URL(request.url, `http://${request.headers.host}`)
    if (url.pathname.startsWith('/api/') && await handleApi(request, response, url)) return
    if (serveMap(request, response, url.pathname)) return
    sendJson(response, 404, null, '接口或资源不存在')
  } catch (error) {
    console.error(error)
    sendJson(response, 500, null, error.message || '地图服务异常')
  }
}).listen(port, '127.0.0.1', () => {
  console.log(`Offline map and geo server: http://127.0.0.1:${port}`)
})
