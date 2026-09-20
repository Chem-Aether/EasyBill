import axios from 'axios'

const geoRequest = axios.create({
  baseURL: import.meta.env.VITE_MAP_SERVER || 'http://127.0.0.1:8765',
  timeout: 10000,
})

geoRequest.interceptors.response.use(response => response.data)

export const searchAirports = keyword => geoRequest.get('/api/airports/search', { params: { keyword } })

export const searchTrainStations = keyword => geoRequest.get('/api/stations/search', { params: { keyword } })

export const forwardGeocode = (keyword, type = 'all', limit = 20) => geoRequest.get('/api/geocode/forward', {
  params: { q: keyword, type, limit },
})

export const reverseGeocode = (longitude, latitude) => geoRequest.get('/api/geocode/reverse', {
  params: { longitude, latitude },
})

export const getRegionBoundaries = codes => geoRequest.post('/api/regions/boundaries/by-codes', { codes })
