import { createReadStream, statSync } from 'node:fs'
import { createServer } from 'node:http'
import { extname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const root = fileURLToPath(new URL('.', import.meta.url))
const port = Number(process.env.MAP_PORT || 8765)
const allowedFiles = new Set(['/world.pmtiles', '/china.pmtiles'])

createServer((request, response) => {
  const pathname = new URL(request.url, `http://${request.headers.host}`).pathname
  if (!allowedFiles.has(pathname)) {
    response.writeHead(404).end('Not found')
    return
  }

  const file = join(root, pathname.slice(1))
  let size
  try {
    size = statSync(file).size
  } catch {
    response.writeHead(404).end('Map archive not found')
    return
  }

  const headers = {
    'Accept-Ranges': 'bytes',
    'Access-Control-Allow-Origin': '*',
    'Cache-Control': 'public, max-age=3600',
    'Content-Type': extname(file) === '.pmtiles' ? 'application/octet-stream' : 'text/plain',
  }
  const match = request.headers.range?.match(/^bytes=(\d+)-(\d*)$/)

  if (match) {
    const start = Number(match[1])
    const end = Math.min(match[2] ? Number(match[2]) : size - 1, size - 1)
    if (start > end || start >= size) {
      response.writeHead(416, { ...headers, 'Content-Range': `bytes */${size}` }).end()
      return
    }
    response.writeHead(206, {
      ...headers,
      'Content-Length': end - start + 1,
      'Content-Range': `bytes ${start}-${end}/${size}`,
    })
    if (request.method === 'HEAD') response.end()
    else createReadStream(file, { start, end }).pipe(response)
    return
  }

  response.writeHead(200, { ...headers, 'Content-Length': size })
  if (request.method === 'HEAD') response.end()
  else createReadStream(file).pipe(response)
}).listen(port, '127.0.0.1', () => {
  console.log(`Offline map server: http://127.0.0.1:${port}`)
})
