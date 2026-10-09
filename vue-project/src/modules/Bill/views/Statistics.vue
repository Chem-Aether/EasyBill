<template>
  <main class="statistics-page" v-loading="loading">
    <header class="page-header">
      <div><h1>收支统计</h1><p>按年月日查看资金净流动与分类结构</p></div>
      <div class="year-control">
        <span>统计年度</span>
        <el-select v-model="year" style="width:120px">
          <el-option v-for="item in years" :key="item" :label="`${item} 年`" :value="item" />
        </el-select>
      </div>
    </header>

    <section class="summary-grid">
      <article class="summary-tile income-tile"><span>年度收入</span><strong>¥ {{ money(totals.income) }}</strong><small>自外部流入</small></article>
      <article class="summary-tile expense-tile"><span>年度支出</span><strong>¥ {{ money(totals.expense) }}</strong><small>流向外部</small></article>
      <article class="summary-tile net-tile"><span>年度净流水</span><strong :class="totals.net >= 0 ? 'positive' : 'negative'">{{ totals.net >= 0 ? '+' : '' }}¥ {{ money(totals.net) }}</strong><small>收入减支出</small></article>
    </section>

    <section class="panel heatmap-panel">
      <div class="panel-heading">
        <div><h2>每日净流水</h2><p>{{ year }} 年 · 颜色按年度分位归一 · 悬停查看实际金额</p></div>
        <div class="heatmap-legend"><span>支出较多</span><i class="loss"/><i class="neutral"/><i class="profit"/><span>收入较多</span></div>
      </div>
      <div ref="calendarEl" class="calendar-chart" />
    </section>

    <section class="chart-grid">
      <article class="panel trend-panel">
        <div class="panel-heading">
          <div><h2>月度收支趋势</h2><p>柱形为收入/支出，折线为净流水</p></div>
        </div>
        <div ref="trendEl" class="trend-chart" />
      </article>

      <article class="panel category-panel">
        <div class="panel-heading">
          <div><h2>{{ categoryDirection === 'expense' ? '支出分类' : '收入分类' }}</h2><p>{{ categoryDirection === 'expense' ? '按一级分类汇总' : '按具体分类汇总' }}</p></div>
          <el-radio-group v-model="categoryDirection" size="small">
            <el-radio-button value="expense">支出</el-radio-button>
            <el-radio-button value="income">收入</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="categoryEl" class="category-chart" />
      </article>
    </section>

    <section class="panel detail-panel">
      <div class="panel-heading">
        <div><h2>{{ selectedTitle || '账单明细' }}</h2><p>{{ selectedTitle ? `共 ${detailRecords.length} 条记录` : '点击上方日期热图或趋势柱查看明细' }}</p></div>
        <el-button v-if="selectedRange" text type="primary" @click="openFilteredBills">查看账单列表</el-button>
      </div>
      <el-table v-if="selectedRange" :data="detailRecords" stripe empty-text="该时段没有账单" class="detail-table">
        <el-table-column prop="occurredAt" label="日期" width="160"><template #default="{ row }">{{ formatDate(row.occurredAt) }}</template></el-table-column>
        <el-table-column prop="direction" label="方向" width="100"><template #default="{ row }"><el-tag :class="`tag-${row.direction}`" effect="plain">{{ directionName(row.direction) }}</el-tag></template></el-table-column>
        <el-table-column prop="category" label="分类" width="140"><template #default="{ row }">{{ row.category || '未分类' }}</template></el-table-column>
        <el-table-column prop="description" label="摘要" min-width="180" show-overflow-tooltip><template #default="{ row }">{{ row.description || row.counterparty || '-' }}</template></el-table-column>
        <el-table-column prop="amount" label="金额" width="140" align="right"><template #default="{ row }"><strong :class="row.direction">{{ row.direction === 'income' ? '+' : row.direction === 'expense' ? '-' : '' }}¥ {{ money(row.amount) }}</strong></template></el-table-column>
      </el-table>
      <el-empty v-else description="选择日期或月份，查看对应账单" :image-size="68" />
    </section>
  </main>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getBillCategories, getBillRecords, getBillTimeline, getCategoryStatistics } from '@/modules/Bill/apis/bill.js'

const router = useRouter()
const currentYear = new Date().getFullYear()
const years = Array.from({ length: 12 }, (_, index) => currentYear - index)
const year = ref(currentYear)
const categoryDirection = ref('expense')
const loading = ref(false)
const daily = ref([])
const monthly = ref([])
const categoryStats = ref([])
const categories = ref([])
const detailRecords = ref([])
const selectedRange = ref(null)
const selectedTitle = ref('')
const calendarEl = ref(null)
const trendEl = ref(null)
const categoryEl = ref(null)
let calendarChart
let trendChart
let categoryChart

const palette = ['#258b88', '#528bd4', '#e3ad4f', '#8b78c3', '#e27670', '#58a897', '#7c9c64', '#d486a6', '#67849c']
const dailyMap = computed(() => new Map(daily.value.map(item => [item.period, item])))
const totals = computed(() => {
  const result = monthly.value.reduce((acc, item) => {
    acc.income += Number(item.income || 0)
    acc.expense += Number(item.expense || 0)
    return acc
  }, { income: 0, expense: 0 })
  return { ...result, net: result.income - result.expense }
})

function money(value) { return Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }
function directionName(value) { return ({ income: '收入', expense: '支出', transfer: '转账' })[value] || '未知' }
function formatDate(value) { return value ? String(value).replace('T', ' ').slice(0, 16) : '-' }
function localIsoDate(date) {
  const pad = value => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}
function dailyRows() {
  const count = new Date(year.value, 1, 0).getDate() + Array.from({ length: 11 }, (_, index) => new Date(year.value, index + 2, 0).getDate()).reduce((sum, value) => sum + value, 0)
  return Array.from({ length: count }, (_, index) => {
    const date = localIsoDate(new Date(year.value, 0, index + 1))
    const item = dailyMap.value.get(date) || {}
    const income = Number(item.income || 0)
    const expense = Number(item.expense || 0)
    return { date, income, expense, net: income - expense }
  })
}
function monthlyRows() {
  const source = new Map(monthly.value.map(item => [item.period, item]))
  return Array.from({ length: 12 }, (_, index) => {
    const period = `${year.value}-${String(index + 1).padStart(2, '0')}`
    const item = source.get(period) || {}
    return { period, income: Number(item.income || 0), expense: Number(item.expense || 0) }
  })
}
function chartTheme() {
  return { textStyle: { color: '#617581', fontFamily: 'inherit' }, animationDuration: 450, animationDurationUpdate: 300 }
}
function renderCalendar() {
  if (!calendarChart) return
  const rows = dailyRows()
  const magnitudes = rows.map(item => Math.abs(item.net)).filter(value => value > 0).sort((left, right) => left - right)
  const values = rows.map(item => {
    let normalized = 0
    if (item.net !== 0 && magnitudes.length) {
      let low = 0
      let high = magnitudes.length
      while (low < high) {
        const middle = (low + high) >>> 1
        if (magnitudes[middle] <= Math.abs(item.net)) low = middle + 1
        else high = middle
      }
      const percentile = low / magnitudes.length
      normalized = Math.sign(item.net) * (0.25 + 0.75 * percentile)
    }
    return { value: [item.date, normalized], itemStyle: item.date === selectedRange.value?.day ? { borderColor: '#293b49', borderWidth: 2 } : undefined }
  })
  calendarChart.setOption({
    ...chartTheme(),
    tooltip: { formatter: ({ data }) => {
      const date = data.value[0]
      const item = dailyMap.value.get(date) || {}
      const net = Number(item.income || 0) - Number(item.expense || 0)
      return `<strong>${date}</strong><br/>收入：¥ ${money(item.income)}<br/>支出：¥ ${money(item.expense)}<br/>净流水：<b style="color:${net < 0 ? '#d76069' : '#258b88'}">${net < 0 ? '-' : '+'}¥ ${money(Math.abs(net))}</b>`
    } },
    visualMap: { min: -1, max: 1, calculable: false, orient: 'horizontal', left: 'center', bottom: 0, itemWidth: 12, itemHeight: 10, text: ['净流入', '净流出'], textStyle: { color: '#819099', fontSize: 11 }, inRange: { color: ['#cf4f5c', '#edf1f3', '#218579'] } },
    calendar: { top: 34, left: 42, right: 18, bottom: 38, range: String(year.value), cellSize: ['auto', 14], itemStyle: { color: '#f1f4f5', borderWidth: 3, borderColor: '#fff', borderRadius: 3 }, yearLabel: { show: false }, monthLabel: { color: '#62747e', nameMap: 'cn', fontSize: 11 }, dayLabel: { firstDay: 1, color: '#829098', nameMap: 'cn', fontSize: 10 }, splitLine: { show: false } },
    series: [{ type: 'heatmap', coordinateSystem: 'calendar', data: values }],
  }, true)
}
function renderTrend() {
  if (!trendChart) return
  const items = monthlyRows()
  const labels = items.map(item => `${Number(item.period.slice(5))}月`)
  const net = items.map(item => Number(item.income || 0) - Number(item.expense || 0))
  trendChart.setOption({
    ...chartTheme(),
    color: ['#528bd4', '#e27670', '#258b88'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: params => {
      const item = items[params[0]?.dataIndex]
      if (!item) return ''
      const period = item.period || item.date
      const balance = Number(item.income || 0) - Number(item.expense || 0)
      return `<strong>${period}</strong><br/>收入：¥ ${money(item.income)}<br/>支出：¥ ${money(item.expense)}<br/>净流水：${balance < 0 ? '-' : '+'}¥ ${money(Math.abs(balance))}`
    } },
    legend: { top: 0, right: 4, textStyle: { color: '#72838c', fontSize: 11 }, data: ['收入', '支出', '净流水'] },
    grid: { top: 42, left: 58, right: 44, bottom: 34 },
    xAxis: { type: 'category', data: labels, boundaryGap: true, axisLine: { lineStyle: { color: '#dce3e6' } }, axisTick: { show: false }, axisLabel: { color: '#87959c', fontSize: 10 } },
    yAxis: [{ type: 'value', axisLabel: { color: '#87959c', fontSize: 10, formatter: value => compactMoney(value) }, splitLine: { lineStyle: { color: '#edf1f2', type: 'dashed' } } }, { type: 'value', show: false }],
    series: [
      { name: '收入', type: 'bar', data: items.map(item => Number(item.income || 0)), barMaxWidth: 18, itemStyle: { color: '#528bd4', borderRadius: [3, 3, 0, 0] } },
      { name: '支出', type: 'bar', data: items.map(item => Number(item.expense || 0)), barMaxWidth: 18, itemStyle: { color: '#e27670', borderRadius: [3, 3, 0, 0] } },
      { name: '净流水', type: 'line', yAxisIndex: 1, data: net, symbol: 'circle', symbolSize: 6, lineStyle: { width: 2, color: '#258b88' }, itemStyle: { color: '#258b88' } },
    ],
  }, true)
}
function renderCategories() {
  if (!categoryChart) return
  const data = categoryStats.value.map(item => ({ name: item.category, value: Number(item.total_amount || 0) }))
  categoryChart.setOption({
    ...chartTheme(),
    color: palette,
    tooltip: {
      trigger: 'item',
      confine: true,
      position: (point, params, dom, rect, size) => [
        Math.max(8, Math.min(point[0] + 12, size.viewSize[0] - size.contentSize[0] - 8)),
        Math.max(8, Math.min(point[1] + 12, size.viewSize[1] - size.contentSize[1] - 8)),
      ],
      formatter: params => `${params.marker}${params.name}<br/>金额：¥ ${money(params.value)}<br/>占比：${params.percent}%`,
    },
    legend: { type: 'scroll', orient: 'vertical', right: 0, top: 'middle', width: '38%', textStyle: { color: '#72838c', fontSize: 11 } },
    series: [{ type: 'pie', radius: ['48%', '72%'], center: ['32%', '52%'], avoidLabelOverlap: true, itemStyle: { borderColor: '#fff', borderWidth: 3, borderRadius: 5 }, label: { show: false }, labelLine: { show: false }, emphasis: { scale: true, scaleSize: 8, label: { show: false }, labelLine: { show: false } }, data }],
    graphic: data.length ? [{ type: 'text', left: '22%', top: '45%', style: { text: categoryDirection.value === 'expense' ? '支出' : '收入', fill: '#89969d', fontSize: 12, textAlign: 'center' } }] : [{ type: 'text', left: 'center', top: 'middle', style: { text: '暂无分类数据', fill: '#a4afb4', fontSize: 13, textAlign: 'center' } }],
  }, true)
}
function compactMoney(value) {
  const abs = Math.abs(Number(value || 0))
  if (abs >= 10000) return `${(value / 10000).toFixed(1)}万`
  return Number(value).toLocaleString('zh-CN')
}
function initCharts() {
  calendarChart = echarts.init(calendarEl.value)
  trendChart = echarts.init(trendEl.value)
  categoryChart = echarts.init(categoryEl.value)
  calendarChart.on('click', params => {
    const date = Array.isArray(params.data?.value) ? params.data.value[0] : null
    if (date) selectDay(date)
  })
  trendChart.on('click', params => {
    const item = monthlyRows()[params.dataIndex]
    if (item) selectMonth(item.period)
  })
  renderCalendar(); renderTrend(); renderCategories()
}
function getYearRange() { return { startTime: `${year.value}-01-01T00:00:00`, endTime: `${year.value}-12-31T23:59:59` } }
async function loadStatistics() {
  loading.value = true
  try {
    const [timeline, stats, categoryList] = await Promise.all([
      getBillTimeline(year.value),
      getCategoryStatistics({ ...getYearRange(), direction: categoryDirection.value, detailed: categoryDirection.value === 'income' }),
      categories.value.length ? Promise.resolve(categories.value) : getBillCategories(),
    ])
    daily.value = timeline.daily || []
    monthly.value = timeline.monthly || []
    categoryStats.value = stats || []
    categories.value = categoryList || []
    await nextTick()
    renderCalendar(); renderTrend(); renderCategories()
  } catch (error) { ElMessage.error(error?.response?.data?.msg || '统计数据加载失败') }
  finally { loading.value = false }
}
async function loadCategoryStats() {
  try {
    categoryStats.value = await getCategoryStatistics({ ...getYearRange(), direction: categoryDirection.value, detailed: categoryDirection.value === 'income' })
    renderCategories()
  } catch (error) { ElMessage.error(error?.response?.data?.msg || '分类统计加载失败') }
}
async function selectDay(date) {
  selectedRange.value = { day: date }
  selectedTitle.value = `${date} 账单`
  await loadDetails(`${date}T00:00:00`, `${date}T23:59:59`)
  renderCalendar()
}
async function selectMonth(period) {
  const [targetYear, targetMonth] = period.split('-').map(Number)
  const lastDay = new Date(targetYear, targetMonth, 0).getDate()
  selectedRange.value = { month: period }
  selectedTitle.value = `${targetYear} 年 ${targetMonth} 月账单`
  await loadDetails(`${period}-01T00:00:00`, `${period}-${String(lastDay).padStart(2, '0')}T23:59:59`)
}
async function loadDetails(startTime, endTime) {
  try {
    const [income, expense] = await Promise.all([
      getBillRecords({ startTime, endTime, direction: 'income' }),
      getBillRecords({ startTime, endTime, direction: 'expense' }),
    ])
    detailRecords.value = [...income, ...expense].sort((left, right) => String(right.occurredAt).localeCompare(String(left.occurredAt)))
  }
  catch (error) { ElMessage.error(error?.response?.data?.msg || '明细加载失败') }
}
function openFilteredBills() {
  if (!selectedRange.value) return
  const { day, month } = selectedRange.value
  const start = day ? `${day}T00:00:00` : `${month}-01T00:00:00`
  let end
  if (day) end = `${day}T23:59:59`
  else {
    const [targetYear, targetMonth] = month.split('-').map(Number)
    end = `${month}-${String(new Date(targetYear, targetMonth, 0).getDate()).padStart(2, '0')}T23:59:59`
  }
  router.push({ path: '/bill/query', query: { startTime: start, endTime: end } })
}
function resizeCharts() { calendarChart?.resize(); trendChart?.resize(); categoryChart?.resize() }
watch(year, async () => { selectedRange.value = null; selectedTitle.value = ''; detailRecords.value = []; await loadStatistics() })
watch(categoryDirection, loadCategoryStats)
onMounted(async () => {
  await nextTick()
  initCharts()
  window.addEventListener('resize', resizeCharts)
  await loadStatistics()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  calendarChart?.dispose(); trendChart?.dispose(); categoryChart?.dispose()
})
</script>

<style scoped>
.statistics-page { display:flex; flex-direction:column; gap:16px; color:#293b49; }
.page-header { display:flex; align-items:center; justify-content:space-between; gap:16px; }
.page-header h1 { margin:0; font-size:24px; }.page-header p { margin:6px 0 0; color:#7c8b95; font-size:13px; }
.year-control { display:flex; align-items:center; gap:10px; color:#75868e; font-size:13px; }
.summary-grid { display:grid; grid-template-columns:repeat(3,minmax(0,1fr)); gap:12px; }
.summary-tile,.panel { background:#fff; border:1px solid #e4e9ee; border-radius:9px; box-shadow:0 2px 8px rgba(38,55,70,.025); }
.summary-tile { display:flex; min-width:0; flex-direction:column; gap:7px; padding:16px 18px; border-top:3px solid #258b88; }
.summary-tile > span { color:#74858e; font-size:12px; }.summary-tile strong { overflow:hidden; color:#344957; font-size:22px; font-variant-numeric:tabular-nums; text-overflow:ellipsis; white-space:nowrap; }.summary-tile small { color:#98a3a8; font-size:11px; }
.income-tile { border-top-color:#528bd4; }.expense-tile { border-top-color:#e27670; }.net-tile { border-top-color:#258b88; }
.positive,.income { color:#258b88!important; }.negative,.expense { color:#d76069!important; }.transfer { color:#7183bf!important; }
.panel { padding:16px 18px; min-width:0; }.panel-heading { display:flex; align-items:center; justify-content:space-between; gap:12px; }.panel-heading h2 { margin:0; font-size:16px; }.panel-heading p { margin:5px 0 0; color:#8a979d; font-size:11px; }
.heatmap-legend { display:flex; align-items:center; gap:5px; color:#87959c; font-size:10px; }.heatmap-legend i { width:12px; height:12px; border-radius:3px; }.heatmap-legend .loss { background:#d96870; }.heatmap-legend .neutral { background:#f2f4f6; border:1px solid #e4e9ee; }.heatmap-legend .profit { background:#54a58d; }
.calendar-chart { height:190px; margin-top:8px; }.chart-grid { display:grid; grid-template-columns:minmax(0,1.55fr) minmax(320px,1fr); gap:16px; }.trend-chart,.category-chart { width:100%; height:310px; margin-top:8px; }.detail-panel { min-height:190px; }.detail-table { margin-top:12px; }.tag-income { color:#288d76; border-color:#b9ded2; background:#eff8f4; }.tag-expense { color:#c95e66; border-color:#efc6c8; background:#fff4f4; }.tag-transfer { color:#537fbd; border-color:#c5d5eb; background:#f2f6fc; }
.statistics-page :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) { background:#258b88; border-color:#258b88; box-shadow:-1px 0 0 0 #258b88; }
@media(max-width:1000px) { .summary-grid { grid-template-columns:repeat(2,minmax(0,1fr)); }.chart-grid { grid-template-columns:1fr; } }
@media(max-width:620px) { .page-header { align-items:flex-start; flex-direction:column; }.summary-grid { gap:8px; }.summary-tile { padding:13px; }.summary-tile strong { font-size:18px; }.panel { padding:14px 12px; }.calendar-chart { height:165px; }.trend-chart,.category-chart { height:280px; }.panel-heading { align-items:flex-start; }.heatmap-legend { align-self:flex-end; }.panel-heading :deep(.el-radio-group) { flex-shrink:0; } }
</style>
