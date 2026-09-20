<template>
  <div class="map-shell">
    <div ref="mapContainer" class="map-canvas"></div>
    <div class="map-legend">
      <span><i class="legend-swatch explored"></i>已探索城市</span>
      <span v-if="mapType === 'flight'"><i class="legend-swatch flight"></i>航线记录</span>
      <span v-if="mapType === 'train'"><i class="legend-swatch train"></i>铁路记录</span>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import * as maplibregl from 'maplibre-gl'
import 'maplibre-gl/dist/maplibre-gl.css'
import { getFootprints, getTicketData, getVisitedCityCodes } from '@/modules/travel/apis/travel.js'
import { getRegionBoundaries } from '@/modules/travel/apis/sysResource.js'
import { useTravleStore } from '@/modules/travel/stores/TravelStore.js'

const MAP_SERVER = import.meta.env.VITE_MAP_SERVER || 'http://127.0.0.1:8765'
const EMPTY_COLLECTION = { type: 'FeatureCollection', features: [] }
const store = useTravleStore()
const { mapType } = storeToRefs(store)
const mapContainer = ref(null)
let map
let popup
let exploredRegions = EMPTY_COLLECTION
let flightFeatures = EMPTY_COLLECTION
let trainFeatures = EMPTY_COLLECTION
let footprintFeatures = EMPTY_COLLECTION
let planeAnimationFrame = 0
let lastPlaneFrame = 0
let trainAnimationFrame = 0
let lastTrainFrame = 0

function baseLayers(source, prefix) {
  const textName = ['coalesce', ['get', 'name:zh-Hans'], ['get', 'name']]
  return [
    { id: `${prefix}-earth`, type: 'fill', source, 'source-layer': 'earth', paint: { 'fill-color': '#081522' } },
    { id: `${prefix}-landuse`, type: 'fill', source, 'source-layer': 'landuse', minzoom: 2, paint: { 'fill-color': ['match', ['get', 'kind'], ['park', 'forest', 'nature_reserve'], '#123a35', '#101f2b'], 'fill-opacity': 0.8 } },
    { id: `${prefix}-water`, type: 'fill', source, 'source-layer': 'water', paint: { 'fill-color': '#07101d' } },
    { id: `${prefix}-boundaries`, type: 'line', source, 'source-layer': 'boundaries', paint: { 'line-color': '#39708a', 'line-width': ['interpolate', ['linear'], ['zoom'], 2, 0.35, 10, 1.1], 'line-opacity': 0.7 } },
    { id: `${prefix}-roads-casing`, type: 'line', source, 'source-layer': 'roads', minzoom: 4, filter: ['!=', ['get', 'kind'], 'rail'], paint: { 'line-color': '#050c13', 'line-width': ['interpolate', ['linear'], ['zoom'], 5, 1.1, 14, 7] } },
    { id: `${prefix}-roads`, type: 'line', source, 'source-layer': 'roads', minzoom: 4, filter: ['!=', ['get', 'kind'], 'rail'], paint: { 'line-color': ['match', ['get', 'kind'], ['highway', 'major_road'], '#75633f', '#3f5662'], 'line-width': ['interpolate', ['linear'], ['zoom'], 5, 0.45, 14, 4.2], 'line-opacity': 0.3 } },
    { id: `${prefix}-buildings`, type: 'fill', source, 'source-layer': 'buildings', minzoom: 12, paint: { 'fill-color': '#263c49', 'fill-outline-color': '#3f5965' } },
    { id: `${prefix}-road-labels`, type: 'symbol', source, 'source-layer': 'roads', minzoom: 11, layout: { 'symbol-placement': 'line', 'text-field': textName, 'text-size': 11 }, paint: { 'text-color': '#c6d3d7', 'text-halo-color': '#071019', 'text-halo-width': 1.4 } },
    { id: `${prefix}-place-labels`, type: 'symbol', source, 'source-layer': 'places', minzoom: 2, layout: { 'text-field': textName, 'text-size': ['interpolate', ['linear'], ['zoom'], 2, 10, 9, 15] }, paint: { 'text-color': '#dce9ec', 'text-halo-color': '#071019', 'text-halo-width': 1.6 } },
    {
      id: `${prefix}-railways`,
      type: 'line',
      source,
      'source-layer': 'roads',
      minzoom: 1,
      filter: ['==', ['get', 'kind'], 'rail'], paint: {
        'line-color': '#e05a5a',
        'line-width': ['interpolate', ['linear'], ['zoom'], 5, 0.6, 10, 1.5, 14, 4],
        'line-opacity': 0.85,
        'line-dasharray': [4, 2]
      }
    },
  ]
}

function createStyle() {
  return {
    version: 8,
    sources: {
      basemap: { type: 'vector', url: `${MAP_SERVER}/api/tiles/tilejson.json`, attribution: '© OpenStreetMap contributors' },
    },
    layers: [
      { id: 'background', type: 'background', paint: { 'background-color': '#07101d' } },
      ...baseLayers('basemap', 'basemap'),
    ],
  }
}

function lineCollection(records, kind) {
  const features = []
  const points = new Map()
  const routeTotals = new Map()
  const routeIndexes = new Map()
  if (kind === 'flight') {
    for (const record of records) {
      const from = [Number(record.fromLongitude), Number(record.fromLatitude)]
      const to = [Number(record.toLongitude), Number(record.toLatitude)]
      if (![...from, ...to].every(Number.isFinite)) continue
      const key = routeKey(from, to)
      routeTotals.set(key, (routeTotals.get(key) || 0) + 1)
    }
  }
  for (const record of records) {
    const from = [Number(record.fromLongitude), Number(record.fromLatitude)]
    const to = [Number(record.toLongitude), Number(record.toLatitude)]
    if (![...from, ...to].every(Number.isFinite)) continue
    if (kind === 'train') {
      const routeStations = Array.isArray(record.routeStations)
        ? record.routeStations
            .map(station => ({ name: station.name, coordinates: [Number(station.longitude), Number(station.latitude)] }))
            .filter(station => station.coordinates.every(Number.isFinite))
        : []
      const stations = routeStations.length >= 2
        ? routeStations
        : [{ name: record.From, coordinates: from }, { name: record.To, coordinates: to }]
      for (let index = 0; index < stations.length - 1; index++) {
        const start = stations[index]
        const end = stations[index + 1]
        features.push({
          type: 'Feature',
          properties: {
            kind: 'train',
            title: `${start.name} → ${end.name}`,
            number: record.Number || '',
            trainId: record.trainId,
            segmentIndex: index
          },
          geometry: { type: 'LineString', coordinates: [start.coordinates, end.coordinates] }
        })
      }
      stations.forEach((station, index) => {
        const endpoint = index === 0 || index === stations.length - 1
        const existing = points.get(station.name)
        const pointKind = endpoint || existing?.pointKind === 'train-point' ? 'train-point' : 'train-waypoint'
        points.set(station.name, { coordinates: station.coordinates, pointKind })
      })
      continue
    }
    let lane = 0
    if (kind === 'flight') {
      const key = routeKey(from, to)
      const index = routeIndexes.get(key) || 0
      lane = index - ((routeTotals.get(key) || 1) - 1) / 2
      routeIndexes.set(key, index + 1)
    }
    const coordinates = kind === 'flight' ? flightArc(from, to, lane) : [from, to]
    const title = `${record.From} → ${record.To}`
    features.push({ type: 'Feature', properties: { kind, title, number: record.Number || '', lane }, geometry: { type: 'LineString', coordinates } })
    points.set(record.From, { coordinates: from, pointKind: `${kind}-point` })
    points.set(record.To, { coordinates: to, pointKind: `${kind}-point` })
  }
  for (const [name, point] of points) {
    features.push({ type: 'Feature', properties: { kind: point.pointKind, title: name }, geometry: { type: 'Point', coordinates: point.coordinates } })
  }
  return { type: 'FeatureCollection', features }
}

function routeKey(from, to) {
  return `${from[0].toFixed(5)},${from[1].toFixed(5)}>${to[0].toFixed(5)},${to[1].toFixed(5)}`
}

function flightArc(from, to, lane = 0, segments = 96) {
  const start = projectMercator(from)
  const end = projectMercator(to)
  let dx = end[0] - start[0]
  if (dx > 0.5) dx -= 1
  if (dx < -0.5) dx += 1
  const dy = end[1] - start[1]
  const distance = Math.hypot(dx, dy)
  if (distance < 1e-8) return [from, to]

  const curveFactor = Math.max(0.16, Math.min(0.8, 0.42 + lane * 0.11))
  const curve = distance * curveFactor
  const control = [
    start[0] + dx / 2 - (dy / distance) * curve,
    start[1] + dy / 2 + (dx / distance) * curve
  ]
  const coordinates = []
  for (let index = 0; index <= segments; index++) {
    const t = index / segments
    const inverse = 1 - t
    const x = inverse * inverse * start[0] + 2 * inverse * t * control[0] + t * t * (start[0] + dx)
    const y = inverse * inverse * start[1] + 2 * inverse * t * control[1] + t * t * end[1]
    coordinates.push(unprojectMercator([x, y]))
  }
  return coordinates
}

function projectMercator([longitude, latitude]) {
  const limitedLatitude = Math.max(-85, Math.min(85, latitude))
  const sin = Math.sin(limitedLatitude * Math.PI / 180)
  return [(longitude + 180) / 360, 0.5 - Math.log((1 + sin) / (1 - sin)) / (4 * Math.PI)]
}

function unprojectMercator([x, y]) {
  const longitude = x * 360 - 180
  const latitude = Math.atan(Math.sinh(Math.PI * (1 - 2 * y))) * 180 / Math.PI
  return [longitude, latitude]
}

function mercatorBearing(from, to) {
  const start = projectMercator(from)
  const end = projectMercator(to)
  let dx = end[0] - start[0]
  if (dx > 0.5) dx -= 1
  if (dx < -0.5) dx += 1
  const dy = end[1] - start[1]
  return Math.atan2(dx, -dy) * 180 / Math.PI
}

function addPlaneIcon() {
  if (map.hasImage('flight-plane-icon')) return
  const canvas = document.createElement('canvas')
  canvas.width = 64
  canvas.height = 64
  const context = canvas.getContext('2d')
  context.beginPath()
  context.moveTo(32, 3)
  context.lineTo(38, 28)
  context.lineTo(59, 37)
  context.lineTo(59, 44)
  context.lineTo(38, 39)
  context.lineTo(39, 52)
  context.lineTo(47, 58)
  context.lineTo(47, 62)
  context.lineTo(32, 57)
  context.lineTo(17, 62)
  context.lineTo(17, 58)
  context.lineTo(25, 52)
  context.lineTo(26, 39)
  context.lineTo(5, 44)
  context.lineTo(5, 37)
  context.lineTo(26, 28)
  context.closePath()
  context.fillStyle = '#ffd47d'
  context.fill()
  context.lineWidth = 3
  context.strokeStyle = '#06101a'
  context.lineJoin = 'round'
  context.stroke()
  map.addImage('flight-plane-icon', context.getImageData(0, 0, 64, 64), { pixelRatio: 2 })
}

function addTravelLayers() {
  if (map.getSource('explored-cities')) return
  addPlaneIcon()
  map.addSource('explored-cities', { type: 'geojson', data: exploredRegions })
  map.addSource('travel-lines', { type: 'geojson', data: EMPTY_COLLECTION })
  map.addSource('animated-planes', { type: 'geojson', data: EMPTY_COLLECTION })
  map.addSource('animated-train-dots', { type: 'geojson', data: EMPTY_COLLECTION })
  map.addSource('footprint-points', {
    type: 'geojson',
    data: footprintFeatures,
    cluster: true,
    clusterMaxZoom: 12,
    clusterRadius: 42
  })
  map.addLayer({ id: 'explored-fill', type: 'fill', source: 'explored-cities', paint: { 'fill-color': '#37e6a5', 'fill-opacity': 0.38 } })
  map.addLayer({ id: 'explored-outline', type: 'line', source: 'explored-cities', paint: { 'line-color': '#7effcf', 'line-width': 1.5, 'line-opacity': 0.9 } })
  map.addLayer({ id: 'flight-route-glow', type: 'line', source: 'travel-lines', filter: ['==', ['get', 'kind'], 'flight'], paint: { 'line-color': '#ffbd59', 'line-width': 7, 'line-opacity': 0.18, 'line-blur': 4 } })
  map.addLayer({ id: 'travel-routes', type: 'line', source: 'travel-lines', filter: ['==', ['geometry-type'], 'LineString'], paint: { 'line-color': ['match', ['get', 'kind'], 'flight', '#ffbd59', '#59d8ff'], 'line-width': 2.4, 'line-opacity': 0.92, 'line-dasharray': [2, 1.2] } })
  map.addLayer({ id: 'travel-points', type: 'circle', source: 'travel-lines', filter: ['in', ['get', 'kind'], ['literal', ['flight-point', 'train-point', 'train-waypoint']]], paint: { 'circle-radius': ['match', ['get', 'kind'], 'train-waypoint', 2.5, 5], 'circle-color': ['match', ['get', 'kind'], 'flight-point', '#ffbd59', '#59d8ff'], 'circle-stroke-color': '#06101a', 'circle-stroke-width': ['match', ['get', 'kind'], 'train-waypoint', 1, 2] } })
  map.addLayer({ id: 'flight-plane-glow', type: 'circle', source: 'animated-planes', paint: { 'circle-radius': 11, 'circle-color': '#ffbd59', 'circle-opacity': 0.22, 'circle-blur': 0.75 } })
  map.addLayer({ id: 'flight-planes', type: 'symbol', source: 'animated-planes', layout: { 'icon-image': 'flight-plane-icon', 'icon-size': 0.72, 'icon-rotate': ['get', 'bearing'], 'icon-rotation-alignment': 'map', 'icon-pitch-alignment': 'map', 'icon-allow-overlap': true, 'icon-ignore-placement': true } })
  map.addLayer({ id: 'train-dot-glow', type: 'circle', source: 'animated-train-dots', paint: { 'circle-radius': 8, 'circle-color': '#59d8ff', 'circle-opacity': 0.2, 'circle-blur': 0.8 } })
  map.addLayer({ id: 'train-moving-dots', type: 'circle', source: 'animated-train-dots', paint: { 'circle-radius': 3.2, 'circle-color': '#d8fbff', 'circle-stroke-color': '#188ca4', 'circle-stroke-width': 1.2 } })
  map.addLayer({ id: 'footprint-clusters', type: 'circle', source: 'footprint-points', filter: ['has', 'point_count'], paint: { 'circle-color': '#37e6a5', 'circle-radius': ['step', ['get', 'point_count'], 14, 10, 18, 30, 23], 'circle-stroke-color': '#08231c', 'circle-stroke-width': 2 } })
  map.addLayer({ id: 'footprint-cluster-count', type: 'symbol', source: 'footprint-points', filter: ['has', 'point_count'], layout: { 'text-field': ['get', 'point_count_abbreviated'], 'text-size': 11 }, paint: { 'text-color': '#062019' } })
  map.addLayer({ id: 'footprint-markers', type: 'circle', source: 'footprint-points', filter: ['!', ['has', 'point_count']], paint: { 'circle-radius': 6, 'circle-color': '#37e6a5', 'circle-stroke-color': '#06101a', 'circle-stroke-width': 2 } })
  updateMode()
}

function updateMode() {
  if (!map?.getSource('travel-lines')) return
  const footMode = mapType.value === 'foot'
  map.setLayoutProperty('explored-fill', 'visibility', footMode ? 'visible' : 'none')
  map.setLayoutProperty('explored-outline', 'visibility', footMode ? 'visible' : 'none')
  map.setLayoutProperty('footprint-clusters', 'visibility', footMode ? 'visible' : 'none')
  map.setLayoutProperty('footprint-cluster-count', 'visibility', footMode ? 'visible' : 'none')
  map.setLayoutProperty('footprint-markers', 'visibility', footMode ? 'visible' : 'none')
  map.getSource('travel-lines').setData(mapType.value === 'flight' ? flightFeatures : mapType.value === 'train' ? trainFeatures : EMPTY_COLLECTION)
  if (mapType.value === 'flight') startPlaneAnimation()
  else stopPlaneAnimation()
  if (mapType.value === 'train') startTrainAnimation()
  else stopTrainAnimation()
}

function planeCollection(timestamp) {
  const routes = flightFeatures.features.filter(feature => feature.geometry?.type === 'LineString' && feature.properties?.kind === 'flight')
  const reducedMotion = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches
  return {
    type: 'FeatureCollection',
    features: routes.map((route, index) => {
      const coordinates = route.geometry.coordinates
      const progress = reducedMotion ? 0.5 : ((timestamp / 12000) + index / Math.max(routes.length, 1)) % 1
      const position = progress * (coordinates.length - 1)
      const point = interpolateRoute(coordinates, position)
      const before = interpolateRoute(coordinates, Math.max(0, position - 0.75))
      const after = interpolateRoute(coordinates, Math.min(coordinates.length - 1, position + 0.75))
      return {
        type: 'Feature',
        properties: { ...route.properties, kind: 'flight-plane', bearing: mercatorBearing(before, after) },
        geometry: { type: 'Point', coordinates: point }
      }
    })
  }
}

function interpolateRoute(coordinates, position) {
  const segment = Math.min(Math.floor(position), coordinates.length - 2)
  const fraction = position - segment
  const from = coordinates[segment]
  const to = coordinates[segment + 1]
  return [from[0] + (to[0] - from[0]) * fraction, from[1] + (to[1] - from[1]) * fraction]
}

function animatePlanes(timestamp) {
  planeAnimationFrame = 0
  if (!map || mapType.value !== 'flight' || !map.getSource('animated-planes')) return
  if (timestamp - lastPlaneFrame >= 40) {
    map.getSource('animated-planes').setData(planeCollection(timestamp))
    lastPlaneFrame = timestamp
  }
  if (!window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) {
    planeAnimationFrame = requestAnimationFrame(animatePlanes)
  }
}

function startPlaneAnimation() {
  if (planeAnimationFrame || !map?.getSource('animated-planes')) return
  lastPlaneFrame = 0
  planeAnimationFrame = requestAnimationFrame(animatePlanes)
}

function stopPlaneAnimation() {
  if (planeAnimationFrame) cancelAnimationFrame(planeAnimationFrame)
  planeAnimationFrame = 0
  map?.getSource('animated-planes')?.setData(EMPTY_COLLECTION)
}

function trainDotCollection(timestamp) {
  const segments = trainFeatures.features.filter(feature => feature.geometry?.type === 'LineString' && feature.properties?.kind === 'train')
  const reducedMotion = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches
  const routes = new Map()
  for (const segment of segments) {
    const routeId = String(segment.properties?.trainId ?? segment.properties?.number ?? '')
    if (!routes.has(routeId)) routes.set(routeId, [])
    routes.get(routeId).push(segment)
  }
  return {
    type: 'FeatureCollection',
    features: Array.from(routes.values()).map((routeSegments, routeIndex) => {
      routeSegments.sort((left, right) => Number(left.properties.segmentIndex) - Number(right.properties.segmentIndex))
      const progress = reducedMotion ? 0.5 : ((timestamp / 7200) + routeIndex / Math.max(routes.size, 1)) % 1
      const routePosition = progress * routeSegments.length
      const segmentIndex = Math.min(Math.floor(routePosition), routeSegments.length - 1)
      const segment = routeSegments[segmentIndex]
      const coordinates = segment.geometry.coordinates
      const segmentProgress = routePosition - segmentIndex
      return {
        type: 'Feature',
        properties: { ...segment.properties, kind: 'train-moving-dot' },
        geometry: { type: 'Point', coordinates: interpolateRoute(coordinates, segmentProgress * (coordinates.length - 1)) }
      }
    })
  }
}

function animateTrainDots(timestamp) {
  trainAnimationFrame = 0
  if (!map || mapType.value !== 'train' || !map.getSource('animated-train-dots')) return
  if (timestamp - lastTrainFrame >= 40) {
    map.getSource('animated-train-dots').setData(trainDotCollection(timestamp))
    lastTrainFrame = timestamp
  }
  if (!window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) {
    trainAnimationFrame = requestAnimationFrame(animateTrainDots)
  }
}

function startTrainAnimation() {
  if (trainAnimationFrame || !map?.getSource('animated-train-dots')) return
  lastTrainFrame = 0
  trainAnimationFrame = requestAnimationFrame(animateTrainDots)
}

function stopTrainAnimation() {
  if (trainAnimationFrame) cancelAnimationFrame(trainAnimationFrame)
  trainAnimationFrame = 0
  map?.getSource('animated-train-dots')?.setData(EMPTY_COLLECTION)
}

async function loadTravelData() {
  const results = await Promise.allSettled([getVisitedCityCodes(), getTicketData('flight'), getTicketData('train'), getFootprints()])
  const value = index => results[index].status === 'fulfilled' ? results[index].value?.data || [] : []
  const cityCodes = value(0)
  try {
    exploredRegions = cityCodes.length ? await getRegionBoundaries(cityCodes) : EMPTY_COLLECTION
  } catch {
    exploredRegions = EMPTY_COLLECTION
  }
  flightFeatures = lineCollection(value(1), 'flight')
  trainFeatures = lineCollection(value(2), 'train')
  footprintFeatures = {
    type: 'FeatureCollection',
    features: value(3)
      .filter(item => Number.isFinite(Number(item.longitude)) && Number.isFinite(Number(item.latitude)))
      .map(item => ({
        type: 'Feature',
        properties: {
          title: item.spotName,
          detail: [item.regionName, item.spotType, item.visitTime].filter(Boolean).join(' · ')
        },
        geometry: { type: 'Point', coordinates: [Number(item.longitude), Number(item.latitude)] }
      }))
  }
  map?.getSource('footprint-points')?.setData(footprintFeatures)
  map?.getSource('explored-cities')?.setData(exploredRegions)
  updateMode()
}

onMounted(async () => {
  map = new maplibregl.Map({
    container: mapContainer.value,
    style: createStyle(),
    center: [104, 35],
    zoom: 2.2,
    minZoom: 1,
    maxZoom: 16,
    localIdeographFontFamily: 'Microsoft YaHei, sans-serif',
    attributionControl: false,
  })
  map.addControl(new maplibregl.NavigationControl({ showCompass: false }), 'top-right')
  map.on('load', addTravelLayers)
  await loadTravelData()
  if (map.loaded()) addTravelLayers()
  popup = new maplibregl.Popup({ closeButton: false, closeOnClick: true })
  map.on('click', event => {
    const feature = map.queryRenderedFeatures(event.point, { layers: ['travel-routes', 'travel-points', 'flight-planes', 'train-moving-dots', 'footprint-markers', 'explored-fill'] })[0]
    if (!feature) return
    const title = feature.properties.title || feature.properties.name || '已探索地区'
    const detailText = feature.properties.number || feature.properties.detail || ''
    const detail = detailText ? `<br>${detailText}` : ''
    popup.setLngLat(event.lngLat).setHTML(`<strong>${title}</strong>${detail}`).addTo(map)
  })
})

watch(mapType, updateMode)

onBeforeUnmount(() => {
  stopPlaneAnimation()
  stopTrainAnimation()
  popup?.remove()
  map?.remove()
})
</script>

<style scoped>
.map-shell {
  position: relative;
  width: 800px;
  height: 560px;
  overflow: hidden;
  background: #07101d;
  border: 1px solid rgba(52, 210, 221, .3)
}

.map-canvas {
  position: absolute;
  inset: 0
}

.map-legend {
  position: absolute;
  z-index: 2;
  background: rgba(5, 14, 24, .86);
  border: 1px solid rgba(77, 196, 207, .34);
  color: #b9d5dc;
  font-size: 12px;
  backdrop-filter: blur(6px)
}

.map-legend {
  left: 12px;
  bottom: 12px;
  display: flex;
  gap: 14px;
  padding: 8px 10px
}

.map-legend span {
  display: flex;
  align-items: center;
  gap: 6px
}

.legend-swatch {
  display: inline-block;
  width: 18px;
  height: 4px;
  background: #37e6a5
}

.legend-swatch.flight {
  background: #ffbd59
}

.legend-swatch.train {
  background: #59d8ff
}

:deep(.maplibregl-ctrl-group) {
  border-radius: 4px;
  background: #071522
}

:deep(.maplibregl-ctrl-group button) {
  background-color: #071522
}

:deep(.maplibregl-popup-content) {
  padding: 10px 12px;
  background: #081522;
  color: #dce9ec;
  border: 1px solid #2d6976;
  border-radius: 4px
}
</style>
