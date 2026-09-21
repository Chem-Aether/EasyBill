<template>
  <main class="travel-board">
    <Map
      class="board-map"
      @select-route="showRouteDetail"
      @select-place="showPlaceDetail"
      @blank-click="closeDrawers"
      @footprint-summary="footprintSummary = $event"
      @footprint-timeline="setTimelineYears"
      :selected-year="selectedYear"
    />
    <header class="board-toolbar glass-surface">
      <div class="board-brand"><strong>旅行足迹</strong><span>个人出行地图</span></div>
      <el-radio-group v-model="activeMode" class="mode-switch" @change="changeMode">
        <el-radio-button value="foot">足迹</el-radio-button>
        <el-radio-button value="flight">航线</el-radio-button>
        <el-radio-button value="train">铁路</el-radio-button>
      </el-radio-group>
    </header>
    <el-button class="admin-entry glass-surface" @click="goToAdmin">管理数据</el-button>
    <div v-if="activeMode === 'foot'" class="foot-summary glass-surface">
      <div><strong>{{ footprintSummary.cities }}</strong><span>去过城市</span></div>
      <div><strong>{{ footprintSummary.places }}</strong><span>去过地点</span></div>
    </div>
    <div v-if="timelineYears.length" class="timeline glass-surface">
      <button
        class="timeline-all"
        :class="{ active: selectedYear === 'all' }"
        :aria-pressed="selectedYear === 'all'"
        @click="selectedYear = 'all'"
      >全部</button>
      <div ref="yearTrack" class="year-track" @wheel="scrollYearTrack">
        <button
          v-for="year in timelineYears"
          :key="year"
          class="year-option"
          :class="{ active: selectedYear === year }"
          :aria-pressed="selectedYear === year"
          @click="selectedYear = year"
        >{{ year }}</button>
      </div>
    </div>
    <el-drawer
      v-model="drawerOpen"
      direction="ltr"
      :modal="true"
      :close-on-click-modal="true"
      :lock-scroll="false"
      modal-class="travel-drawer-overlay"
      :show-close="false"
      :with-header="false"
      size="min(430px, 92vw)"
      class="travel-data-drawer"
    >
      <TravelPanel :mode="activeMode" :route-selection="routeSelection" :selected-year="selectedYear" @close="drawerOpen = false" />
    </el-drawer>
    <el-drawer
      v-model="placeDrawerOpen"
      direction="rtl"
      :modal="true"
      :close-on-click-modal="true"
      :lock-scroll="false"
      modal-class="travel-drawer-overlay"
      :show-close="false"
      :with-header="false"
      size="min(380px, 92vw)"
      class="place-detail-drawer"
    >
      <PlaceDetailPanel :place="selectedPlace" @close="placeDrawerOpen = false" />
    </el-drawer>
  </main>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import { useRouter } from 'vue-router'
import Map from './Map.vue'
import TravelPanel from './TravelPanel.vue'
import PlaceDetailPanel from './PlaceDetailPanel.vue'
import { useTravleStore } from '@/modules/travel/stores/TravelStore.js'

const router = useRouter()
const store = useTravleStore()
const activeMode = ref(store.mapType || 'foot')
const drawerOpen = ref(activeMode.value !== 'foot')
const routeSelection = ref(null)
const selectedPlace = ref(null)
const placeDrawerOpen = ref(false)
const footprintSummary = ref({ cities: 0, places: 0 })
const timelineYears = ref([])
const selectedYear = ref('all')
const yearTrack = ref(null)

function changeMode(mode) {
  store.setMapType(mode)
  store.mapName = mode === 'flight' ? '航空' : mode === 'train' ? '铁路' : '足迹'
  routeSelection.value = null
  drawerOpen.value = mode !== 'foot'
  placeDrawerOpen.value = false
}

function showPlaceDetail(place) {
  selectedPlace.value = place
  placeDrawerOpen.value = true
}

function closeDrawers() {
  drawerOpen.value = false
  placeDrawerOpen.value = false
  routeSelection.value = null
  selectedPlace.value = null
}

function setTimelineYears(years) {
  const currentYear = new Date().getFullYear()
  const oldestRecordedYear = years.length ? Math.min(...years) : currentYear
  const firstYear = Math.min(oldestRecordedYear, currentYear - 9)
  timelineYears.value = Array.from(
    { length: currentYear - firstYear + 1 },
    (_, index) => firstYear + index
  )
  if (selectedYear.value !== 'all' && !timelineYears.value.includes(selectedYear.value)) {
    selectedYear.value = 'all'
  }
  nextTick(() => {
    if (yearTrack.value) yearTrack.value.scrollLeft = yearTrack.value.scrollWidth
  })
}

function scrollYearTrack(event) {
  if (!yearTrack.value || Math.abs(event.deltaY) <= Math.abs(event.deltaX)) return
  event.preventDefault()
  yearTrack.value.scrollLeft += event.deltaY
}

function showRouteDetail(selection) {
  if (!selection?.type || selection.type === 'foot') return
  activeMode.value = selection.type
  store.setMapType(selection.type)
  store.mapName = selection.type === 'flight' ? '航空' : '铁路'
  routeSelection.value = { ...selection, nonce: Date.now() }
  drawerOpen.value = true
}

function goToAdmin() {
  router.push('/travel/admin')
}
</script>

<style scoped>
.travel-board { position: fixed; inset: 0; overflow: hidden; background: #07101d; }
.board-map { position: absolute; inset: 0; }
.glass-surface { border: 1px solid rgba(157, 222, 231, .2); background: rgba(7, 18, 29, .7); box-shadow: 0 12px 32px rgba(0, 0, 0, .22); backdrop-filter: blur(18px) saturate(125%); }
.board-toolbar { position: absolute; z-index: 5; top: 18px; left: 50%; display: flex; align-items: center; gap: 28px; min-height: 54px; padding: 7px 9px 7px 18px; transform: translateX(-50%); border-radius: 6px; }
.board-brand { display: flex; flex-direction: column; color: #ecf8fa; }
.board-brand strong { font-size: 17px; letter-spacing: 0; }
.board-brand span { margin-top: 2px; color: #8cabb3; font-size: 11px; }
.mode-switch :deep(.el-radio-button__inner) { min-width: 66px; border-color: rgba(120, 194, 207, .2); background: rgba(14, 36, 50, .68); color: #afc8ce; box-shadow: none; }
.mode-switch :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) { border-color: #43c9de; background: #188ca4; color: #fff; box-shadow: -1px 0 0 0 #43c9de; }
.admin-entry { position: absolute; z-index: 5; top: 20px; right: 18px; height: 42px; border-radius: 5px; color: #d8edf1; }
.foot-summary { position: absolute; z-index: 5; bottom: 18px; left: 18px; display: grid; grid-template-columns: repeat(2, minmax(86px, 1fr)); padding: 10px 12px; border-radius: 6px; }
.foot-summary > div { min-width: 86px; padding: 3px 12px; border-right: 1px solid rgba(141, 205, 216, .16); }
.foot-summary > div:last-child { border-right: 0; }
.foot-summary strong, .foot-summary span { display: block; }
.foot-summary strong { color: #eefafb; font-size: 18px; }
.foot-summary span { margin-top: 2px; color: #819da5; font-size: 11px; }
.timeline { position: absolute; z-index: 5; bottom: 18px; left: 50%; display: grid; grid-template-columns: 62px minmax(0, 580px); align-items: center; width: min(680px, calc(100vw - 420px)); min-height: 52px; padding: 0 12px; transform: translateX(-50%); border-radius: 6px; }
.timeline button { height: 32px; border: 0; border-radius: 4px; background: transparent; color: #8eabb2; font: inherit; cursor: pointer; transition: background-color .18s, color .18s; }
.timeline button:hover { background: rgba(53, 176, 181, .12); color: #d7eff1; }
.timeline button.active { background: #168ba0; color: #fff; box-shadow: inset 0 0 0 1px rgba(111, 224, 229, .4); }
.timeline-all { width: 52px; font-size: 12px; }
.year-track { display: grid; grid-auto-flow: column; grid-auto-columns: 58px; overflow-x: auto; overscroll-behavior-inline: contain; scroll-behavior: smooth; scrollbar-width: thin; scrollbar-color: rgba(88, 179, 187, .45) transparent; }
.year-track::-webkit-scrollbar { height: 3px; }
.year-track::-webkit-scrollbar-thumb { border-radius: 2px; background: rgba(88, 179, 187, .45); }
.year-option { width: 58px; font-size: 12px; }
:global(.travel-data-drawer.el-drawer) { top: 90px; bottom: 18px; height: auto; margin-left: 18px; overflow: hidden; border: 1px solid rgba(131, 215, 226, .22); border-radius: 6px; background: rgba(5, 17, 28, .72); box-shadow: 18px 0 48px rgba(0, 0, 0, .3); backdrop-filter: blur(22px) saturate(130%); }
:global(.travel-data-drawer .el-drawer__body) { padding: 0; overflow: hidden; }
:global(.place-detail-drawer.el-drawer) { top: 90px; right: 18px; bottom: 18px; height: auto; overflow: hidden; border: 1px solid rgba(131, 215, 226, .22); border-radius: 6px; background: rgba(5, 17, 28, .76); box-shadow: -18px 0 48px rgba(0, 0, 0, .3); backdrop-filter: blur(22px) saturate(130%); }
:global(.place-detail-drawer .el-drawer__body) { padding: 0; overflow: hidden; }
:global(.travel-drawer-overlay) { background: transparent !important; }
@media (max-width: 720px) {
  .board-toolbar { left: 12px; right: 12px; justify-content: space-between; gap: 8px; transform: none; }
  .board-brand span { display: none; }
  .admin-entry { top: 82px; right: 12px; }
  .foot-summary { bottom: 68px; left: 10px; grid-template-columns: repeat(2, 1fr); }
  .foot-summary > div { min-width: 0; padding: 3px 7px; }
  .timeline { right: 10px; bottom: 10px; left: 10px; grid-template-columns: 58px minmax(0, 1fr); width: auto; transform: none; }
  :global(.travel-data-drawer.el-drawer) { top: 140px; bottom: 10px; margin-left: 10px; }
  :global(.place-detail-drawer.el-drawer) { top: 140px; right: 10px; bottom: 10px; }
}
</style>
