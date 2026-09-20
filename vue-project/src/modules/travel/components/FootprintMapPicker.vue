<template>
  <div class="picker-shell">
    <div class="poi-search">
      <el-select
        v-model="selectedPoi"
        filterable
        remote
        clearable
        reserve-keyword
        :remote-method="searchPois"
        :loading="poiLoading"
        placeholder="搜索全国景点或打卡点"
        @change="selectPoi"
      >
        <el-option
          v-for="item in poiOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        >
          <div class="poi-option">
            <span class="poi-main">
              <span class="poi-name">{{ item.name }}</span>
              <span v-if="item.kind" class="poi-kind">{{ item.kind }}</span>
            </span>
            <span class="poi-region" :title="item.fullName">{{ item.fullName || '行政区未知' }}</span>
          </div>
        </el-option>
      </el-select>
    </div>
    <div ref="mapContainer" class="picker-map"></div>
    <div class="picker-status">
      <span v-if="longitude != null && latitude != null">
        {{ Number(longitude).toFixed(6) }}, {{ Number(latitude).toFixed(6) }}
      </span>
      <span v-else>在地图上单击地点</span>
      <el-button v-if="longitude != null" link type="danger" @click="clearPoint">清除选点</el-button>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as maplibregl from 'maplibre-gl'
import { Protocol } from 'pmtiles'
import 'maplibre-gl/dist/maplibre-gl.css'
import { forwardGeocode, reverseGeocode } from '@/modules/travel/apis/sysResource.js'

const props = defineProps({
  longitude: { type: Number, default: null },
  latitude: { type: Number, default: null }
})
const emit = defineEmits(['pick', 'clear'])
const MAP_SERVER = import.meta.env.VITE_MAP_SERVER || 'http://127.0.0.1:8765'
const mapContainer = ref(null)
const selectedPoi = ref('')
const poiOptions = ref([])
const poiLoading = ref(false)
let map
let marker
let searchRequestId = 0

function vectorLayers(source, prefix, minzoom = 0) {
  const textName = ['coalesce', ['get', 'name:zh-Hans'], ['get', 'name']]
  return [
    { id: `${prefix}-earth`, type: 'fill', source, 'source-layer': 'earth', minzoom, paint: { 'fill-color': '#e8edf0' } },
    { id: `${prefix}-landuse`, type: 'fill', source, 'source-layer': 'landuse', minzoom: Math.max(3, minzoom), paint: { 'fill-color': ['match', ['get', 'kind'], ['park', 'forest', 'nature_reserve'], '#cfe5d4', '#e2e8e5'], 'fill-opacity': 0.8 } },
    { id: `${prefix}-water`, type: 'fill', source, 'source-layer': 'water', minzoom, paint: { 'fill-color': '#b7dceb' } },
    { id: `${prefix}-boundaries`, type: 'line', source, 'source-layer': 'boundaries', minzoom, paint: { 'line-color': '#82939d', 'line-width': 0.7 } },
    { id: `${prefix}-roads`, type: 'line', source, 'source-layer': 'roads', minzoom: Math.max(5, minzoom), paint: { 'line-color': ['match', ['get', 'kind'], ['highway', 'major_road'], '#d3a85f', '#bbc3c5'], 'line-width': ['interpolate', ['linear'], ['zoom'], 5, 0.4, 14, 3.5] } },
    { id: `${prefix}-poi-labels`, type: 'symbol', source, 'source-layer': 'pois', minzoom: Math.max(5, minzoom), layout: { 'text-field': textName, 'text-size': 11, 'text-offset': [0, 0.8], 'text-anchor': 'top', 'text-optional': true }, paint: { 'text-color': '#3f5158', 'text-halo-color': '#f4f7f7', 'text-halo-width': 1.2 } },
    { id: `${prefix}-road-labels`, type: 'symbol', source, 'source-layer': 'roads', minzoom: Math.max(11, minzoom), layout: { 'symbol-placement': 'line', 'text-field': textName, 'text-size': 11 }, paint: { 'text-color': '#46565c', 'text-halo-color': '#f4f7f7', 'text-halo-width': 1.3 } },
    { id: `${prefix}-place-labels`, type: 'symbol', source, 'source-layer': 'places', minzoom: Math.max(2, minzoom), layout: { 'text-field': textName, 'text-size': ['interpolate', ['linear'], ['zoom'], 2, 10, 10, 14] }, paint: { 'text-color': '#263a42', 'text-halo-color': '#f4f7f7', 'text-halo-width': 1.4 } }
  ]
}

function createStyle() {
  return {
    version: 8,
    sources: {
      world: { type: 'vector', url: `pmtiles://${MAP_SERVER}/world.pmtiles` },
      china: { type: 'vector', url: `pmtiles://${MAP_SERVER}/china.pmtiles` }
    },
    layers: [
      { id: 'picker-background', type: 'background', paint: { 'background-color': '#e8edf0' } },
      ...vectorLayers('world', 'picker-world'),
      ...vectorLayers('china', 'picker-china', 6.5)
    ]
  }
}

function setMarker(longitude, latitude, moveMap = false) {
  if (!map || longitude == null || latitude == null) return
  const coordinates = [Number(longitude), Number(latitude)]
  if (!marker) marker = new maplibregl.Marker({ color: '#d94343' })
  marker.setLngLat(coordinates).addTo(map)
  if (moveMap) map.flyTo({ center: coordinates, zoom: Math.max(map.getZoom(), 11) })
}

async function locationAt(longitude, latitude) {
  const response = await reverseGeocode(longitude, latitude)
  const location = response.data
  return {
    adcode: location?.district?.code || '',
    districtName: location?.district?.name || '',
    fullName: location?.formattedRegion || ''
  }
}

async function handleMapClick(event) {
  const longitude = Number(event.lngLat.lng.toFixed(7))
  const latitude = Number(event.lngLat.lat.toFixed(7))
  setMarker(longitude, latitude)
  try {
    emit('pick', { longitude, latitude, ...await locationAt(longitude, latitude) })
  } catch {
    emit('pick', { longitude, latitude, adcode: '', districtName: '', fullName: '' })
  }
}

async function searchPois(keyword) {
  const text = keyword?.trim()
  if (!text) {
    poiOptions.value = []
    return
  }
  const requestId = ++searchRequestId
  poiLoading.value = true
  try {
    const response = await forwardGeocode(text, 'poi', 20)
    if (requestId !== searchRequestId) return
    poiOptions.value = (response.data || []).map(item => ({
      id: item.id,
      name: item.name,
      kind: item.subcategory || item.category || '',
      coordinates: [item.longitude, item.latitude],
      adcode: item.region?.district?.code || '',
      districtName: item.region?.district?.name || '',
      fullName: item.region?.formattedRegion || ''
    }))
  } catch {
    if (requestId === searchRequestId) poiOptions.value = []
  } finally {
    if (requestId === searchRequestId) poiLoading.value = false
  }
}

function selectPoi(id) {
  const poi = poiOptions.value.find(item => item.id === id)
  if (!poi) return
  const [longitude, latitude] = poi.coordinates.map(Number)
  map.flyTo({ center: [longitude, latitude], zoom: Math.max(map.getZoom(), 13) })
  setMarker(longitude, latitude)
  emit('pick', {
    longitude: Number(longitude.toFixed(7)),
    latitude: Number(latitude.toFixed(7)),
    adcode: poi.adcode,
    districtName: poi.districtName,
    fullName: poi.fullName,
    suggestedName: poi.name
  })
}

function clearPoint() {
  marker?.remove()
  marker = null
  emit('clear')
}

onMounted(async () => {
  await nextTick()
  const protocol = new Protocol()
  maplibregl.addProtocol('pmtiles', protocol.tile)
  const hasPoint = props.longitude != null && props.latitude != null
  map = new maplibregl.Map({
    container: mapContainer.value,
    style: createStyle(),
    center: hasPoint ? [props.longitude, props.latitude] : [104, 35],
    zoom: hasPoint ? 11 : 5.2,
    minZoom: 1,
    maxZoom: 16,
    attributionControl: false,
    localIdeographFontFamily: 'Microsoft YaHei, sans-serif'
  })
  map.addControl(new maplibregl.NavigationControl({ showCompass: false }), 'top-right')
  map.on('load', () => {
    if (hasPoint) setMarker(props.longitude, props.latitude)
  })
  map.on('click', handleMapClick)
})

watch(() => [props.longitude, props.latitude], ([longitude, latitude]) => {
  if (longitude == null || latitude == null) {
    marker?.remove()
    marker = null
    return
  }
  setMarker(longitude, latitude)
})

onBeforeUnmount(() => {
  marker?.remove()
  map?.remove()
  maplibregl.removeProtocol('pmtiles')
})
</script>

<style scoped>
.picker-shell { border: 1px solid #d7dde5; background: #f6f8fa; }
.poi-search { padding: 10px; border-bottom: 1px solid #d7dde5; background: #fff; }
.poi-search :deep(.el-select) { width: 100%; }
.poi-option { width: 100%; display: flex; align-items: center; gap: 16px; }
.poi-main { min-width: 0; display: flex; align-items: baseline; gap: 8px; }
.poi-name { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.poi-kind { flex: none; color: #98a2b3; font-size: 12px; }
.poi-region { min-width: 130px; margin-left: auto; overflow: hidden; color: #667085; font-size: 12px; text-align: right; text-overflow: ellipsis; white-space: nowrap; }
.picker-map { width: 100%; height: 330px; }
.picker-status { min-height: 38px; padding: 0 12px; display: flex; align-items: center; justify-content: space-between; color: #52606d; font-size: 13px; font-variant-numeric: tabular-nums; }
:deep(.maplibregl-ctrl-group) { border-radius: 4px; }
</style>
