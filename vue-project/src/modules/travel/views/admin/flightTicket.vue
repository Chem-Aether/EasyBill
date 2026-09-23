<template>
  <div class="flight-manage-page">
    <div class="page-title">航空出行记录管理</div>

    <!-- 🔍 查询条件栏 -->
    <el-card class="query-card" shadow="hover">
      <el-form :model="queryForm" inline size="default">
        <el-form-item label="航班号">
          <el-input v-model="queryForm.flightNo" placeholder="请输入航班号" clearable />
        </el-form-item>
  <el-form-item label="起飞机场">
          <el-autocomplete
              v-model="queryForm.departureAirportDisplay"
              :fetch-suggestions="queryAirport"
              placeholder="起飞机场"
              @select="(s) => handleAirportSelect(s, 'queryDeparture')"
          />
        </el-form-item>
        <el-form-item label="到达机场">
          <el-autocomplete
              v-model="queryForm.arrivalAirportDisplay"
              :fetch-suggestions="queryAirport"
              placeholder="到达机场"
              @select="(s) => handleAirportSelect(s, 'queryArrival')"
          />
        </el-form-item>

        <el-form-item label="起飞时间">
          <el-date-picker
              v-model="queryForm.takeoffTimeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <div class="tool-bar">
      <el-button type="primary" @click="handleAdd">+ 追加新机票</el-button>
      <el-button @click="refresh">刷新列表</el-button>
    </div>

    <!-- 卡片列表 -->
    <div class="card-list">
      <div
          v-for="(item, idx) in pageData"
          :key="idx"
          class="flight-card"
          :class="{ editing: editIndex === idx }"
      >
        <el-card shadow="hover">
          <div class="card-header">
            <span>
              {{ editIndex === idx ? '编辑航班' : `第 ${idxStart + idx + 1} 条` }}
            </span>
            <div>
              <template v-if="editIndex !== idx">
                <el-button
                    type="primary"
                    link
                    size="small"
                    @click="handleEdit(item, idx)"
                >修改</el-button>
                <el-button
                    type="danger"
                    link
                    size="small"
                    @click="handleDelete(item, idx)"
                >删除</el-button>
              </template>
              <template v-else>
                <el-button size="small" @click="cancelEdit">取消</el-button>
                <el-button type="primary" size="small" @click="saveEdit(idx)">保存</el-button>
              </template>
            </div>
          </div>

          <el-form
              :model="item"
              label-width="120px"
              size="default"
              style="margin-top:10px"
          >
            <div class="grid-form">
              <el-form-item label="航班号">
                <el-input v-model="item.flightNo" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="航空公司">
                <el-input v-model="item.company" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="座位号">
                <el-input v-model="item.seatNo" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item>

              </el-form-item>
              <el-form-item label="机号">
                <el-input v-model="item.aircraftReg" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="机型">
                <el-input v-model="item.aircraftType" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="飞行距离(km)">
                <el-input v-model.number="item.flightDistanceKm" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="经停机场">
                <el-autocomplete
                    v-model="item.stopoverAirportDisplay"
                    :fetch-suggestions="queryAirport"
                    :disabled="editIndex !== idx"
                    placeholder="无则不填"
                    @select="(s) => handleAirportSelect(s, 'formStopover')"
                />
              </el-form-item>


              <el-form-item label="起飞机场">
                <el-autocomplete
                    v-model="item.departureAirportDisplay"
                    :fetch-suggestions="queryAirport"
                    :disabled="editIndex !== idx"
                    @select="(s) => handleAirportSelect(s, 'formDeparture')"
                />
              </el-form-item>
              <el-form-item label="起飞机场航站楼">
                <el-input v-model="item.departureTerminal" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="起飞时间">
                <el-date-picker
                    v-model="item.takeoffTime"
                    type="datetime"
                    :disabled="editIndex !== idx"
                    style="width:100%"
                />
              </el-form-item>
              <el-form-item label="登机方式">
                <el-select
                    v-model="item.boardingMethod"
                    :disabled="editIndex !== idx"
                    style="width:100%"
                >
                  <el-option label="廊桥" value="廊桥"/>
                  <el-option label="摆渡车" value="摆渡车"/>
                </el-select>
              </el-form-item>

              <el-form-item label="到达机场">
                <el-autocomplete
                    v-model="item.arrivalAirportDisplay"
                    :fetch-suggestions="queryAirport"
                    :disabled="editIndex !== idx"
                    @select="(s) => handleAirportSelect(s, 'formArrival')"
                />
              </el-form-item>
              <el-form-item label="到达机场航站楼">
                <el-input v-model="item.arrivalTerminal" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="达到时间">
                <el-date-picker
                    v-model="item.landingTime"
                    type="datetime"
                    :disabled="editIndex !== idx"
                    style="width:100%"
                />
              </el-form-item>
              <el-form-item label="下机方式">
                <el-select
                    v-model="item.deplaningMethod"
                    :disabled="editIndex !== idx"
                    style="width:100%"
                >
                  <el-option label="廊桥" value="廊桥"/>
                  <el-option label="摆渡车" value="摆渡车"/>
                </el-select>
              </el-form-item>

<!--              <el-form-item label="起飞机场ICAO">-->
<!--                <el-input v-model="item.departureIcao" :disabled="editIndex !== idx" />-->
<!--              </el-form-item>-->
<!--              <el-form-item label="到达机场ICAO">-->
<!--                <el-input v-model="item.arrivalIcao" :disabled="editIndex !== idx" />-->
<!--              </el-form-item>-->
            </div>
          </el-form>
        </el-card>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
  :total="pageInfo.total || 0"
        layout="total, prev, pager, next, jumper"
        style="text-align:center; margin-top:20px"
  @current-change="loadData"
  @size-change="() => { currentPage.value = 1; loadData() }"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFlightList, deleteFlightTicketById, insertFlightTicket, updateFlightTicketById } from '@/modules/travel/apis/flightTickets.js'
import { useResourceSearch } from '@/modules/travel/composables/useResourceSearch.js'

const { queryAirport } = useResourceSearch()

// 选择后：输入框显示中文(ICAO)，但真实提交字段保存 ICAO
// type: queryDeparture | queryArrival | formDeparture | formArrival | formStopover
const handleAirportSelect = (suggestion, type) => {
  const code = suggestion?.icao
  if (!code) return

  const display = suggestion?.value || `${suggestion?.name || ''} (${code})`
  const name = suggestion?.name || ''

  if (type === 'queryDeparture') {
  queryForm.departureIcao = code
    queryForm.departureAirportDisplay = display
  }
  if (type === 'queryArrival') {
  queryForm.arrivalIcao = code
    queryForm.arrivalAirportDisplay = display
  }

  const globalIndex = idxStart.value + editIndex.value
  const item = flightList.value[globalIndex]
  if (!item) return

  if (type === 'formDeparture') {
  // 提交后端：ICAO
  item.departureIcao = code
  // 展示/入库中文名
  item.departureAirport = name
    item.departureAirportDisplay = display
  }
  if (type === 'formArrival') {
  item.arrivalIcao = code
  item.arrivalAirport = name
    item.arrivalAirportDisplay = display
  }
  if (type === 'formStopover') {
  // 表结构：经停机场只存中文
  item.stopoverAirport = name
    item.stopoverAirportDisplay = display
  }
}

// ===================== 数据 =====================
const flightList = ref([])

// 后端分页信息（total/size/current/records...）
const pageInfo = ref({ total: 0 })

onMounted(async () => {
  await loadData()
})

// 用 ICAO 回填显示文本（列表加载/刷新时）
const fillAirportDisplays = (list = []) => {
  return (list || []).map(item => {
  // 入库字段：airport 是中文名，icao 是码；展示两者组合
  item.departureAirportDisplay = item.departureAirport && item.departureIcao ? `${item.departureAirport} (${item.departureIcao})` : (item.departureAirport || item.departureIcao || '')
  item.arrivalAirportDisplay = item.arrivalAirport && item.arrivalIcao ? `${item.arrivalAirport} (${item.arrivalIcao})` : (item.arrivalAirport || item.arrivalIcao || '')

  // 经停机场表里只有中文，展示直接用中文即可
  item.stopoverAirportDisplay = item.stopoverAirport || ''
    return item
  })
}

const loadData = async () => {
  // 后端 DTO 是 LocalDateTime，避免传 ISO 字符串(带 Z)导致 400
  const toLocalDateTimeParam = (v) => {
    if (!v) return null
    const d = (v instanceof Date) ? v : new Date(v)
    if (Number.isNaN(d.getTime())) return null

    const pad = (n) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  }

  const takeoffTimeStart = Array.isArray(queryForm.takeoffTimeRange)
    ? toLocalDateTimeParam(queryForm.takeoffTimeRange[0])
    : null
  const takeoffTimeEnd = Array.isArray(queryForm.takeoffTimeRange)
    ? toLocalDateTimeParam(queryForm.takeoffTimeRange[1])
    : null

  const res = await getFlightList({
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    flightNo: queryForm.flightNo,
  departureIcao: queryForm.departureIcao,
  arrivalIcao: queryForm.arrivalIcao,
    takeoffTimeStart,
    takeoffTimeEnd
  })
  flightList.value = fillAirportDisplays(res.data || [])
  pageInfo.value = res.page || { total: (res.data || []).length }
}

// ===================== 查询 =====================
const queryForm = reactive({
  flightNo: '',
  // 提交后端用（ICAO）
  departureIcao: '',
  arrivalIcao: '',

  // 输入框展示用（中文 + ICAO）
  departureAirportDisplay: '',
  arrivalAirportDisplay: '',
  // [start, end]
  takeoffTimeRange: null
})

const filteredList = computed(() => {
  // 已经由后端按条件过滤，这里直接返回即可
  return flightList.value
})

const doQuery = async () => {
  currentPage.value = 1
  await loadData()
}
const resetQuery = () => {
  queryForm.flightNo = ''
  queryForm.departureIcao = ''
  queryForm.arrivalIcao = ''
  queryForm.departureAirportDisplay = ''
  queryForm.arrivalAirportDisplay = ''
  queryForm.takeoffTimeRange = null
}

// ===================== 分页 =====================
const currentPage = ref(1)
const pageSize = ref(10)

// 后端分页：当前页数据就是 flightList
const pageData = computed(() => filteredList.value)

// 后端分页：页内索引直接用 idx，不再需要 idxStart 偏移
const idxStart = computed(() => 0)

// ===================== 编辑 / 新增 统一逻辑 =====================
const editIndex = ref(-1)
const editBackup = ref(null)
// 进入编辑
const handleEdit = (item, idx) => {
  editIndex.value = idx
  // 深拷贝一份用于取消/对比（避免引用导致对比失效）
  editBackup.value = JSON.parse(JSON.stringify(item))
}

// 取消
const cancelEdit = () => {
  if (editIndex.value !== -1 && editBackup.value) {
  const globalIndex = idxStart.value + editIndex.value
  flightList.value.splice(globalIndex, 1, editBackup.value)
  }
  editIndex.value = -1
  editBackup.value = null
}

// 保存
const saveEdit = async (idx) => {
  // 拿到当前这一行数据（idx 为当前页索引）
  const globalIndex = idxStart.value + idx
  const item = flightList.value[globalIndex]
  if (!item) return

  // 无变更：直接退出编辑，不请求后端
  if (editBackup.value) {
    const now = JSON.stringify(item)
    const old = JSON.stringify(editBackup.value)
    if (now === old) {
      editIndex.value = -1
      editBackup.value = null
      ElMessage.info('未检测到变更，无需保存')
      return
    }
  }

  // 二次确认
  try {
    await ElMessageBox.confirm('确认提交保存当前修改？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch (e) {
    return
  }

  try {
    // 修改已有数据
    if (item.flightId) {
      await updateFlightTicketById(item)
      ElMessage.success('修改成功')
    }

    // 新增数据
    else {
      const res = await insertFlightTicket(item)
      // 把后端返回的 id 覆盖到前端数据上
      flightList.value[globalIndex] = res.data
      ElMessage.success('新增成功')
    }

    // 退出编辑状态
    editIndex.value = -1
    editBackup.value = null

    // 刷新一次，保证列表和后端一致
    await refresh()
  } catch (err) {
    ElMessage.error('保存失败：' + (err.message || '服务异常'))
  }
}

// 新增 = 推入空数据
const handleAdd = () => {
  const newItem = {
    flightNo: '', aircraftReg: '', aircraftType: '', company: '',
  departureAirport: '', departureAirportDisplay: '', departureTerminal: '', departureIcao: '',
    takeoffTime: '', boardingMethod: '廊桥', flightDistanceKm: 0,
  arrivalAirport: '', arrivalAirportDisplay: '', arrivalTerminal: '', arrivalIcao: '',
    landingTime: '', stopoverAirport: '', seatNo: '', deplaningMethod:'',
  }
  newItem.stopoverAirportDisplay = ''
  flightList.value.unshift(newItem)
  editIndex.value = 0
  editBackup.value = JSON.parse(JSON.stringify(newItem))
}

// ===================== 删除 =====================
const handleDelete = async (item, idx) => {
  try {
    await ElMessageBox.confirm('确定删除？')
    await deleteFlightTicketById(item.flightId)

    flightList.value.splice(idx, 1)
    ElMessage.success('删除成功')

  } catch (err) {
    ElMessage.info('取消或失败')
  }
}

// ===================== 刷新 =====================
const refresh = async () => {
  await loadData()
  ElMessage.success('刷新成功')
}
</script>

<style scoped>
.flight-manage-page {
  width: 100%;
  margin: 0 auto;
}
.page-title {
  font-size: 25px;
  font-weight: 750;
  margin-bottom: 22px;
}
.query-card {
  margin-bottom: 15px;
}
.tool-bar {
  margin-bottom: 15px;
}
.card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.flight-card {
  width: 100%;
}
.flight-card.editing {
  border: 2px solid #1890ff;
  border-radius: 8px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: space-between;
  font-weight: 600;
}
.grid-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px 14px;
}
.flight-card :deep(.el-card__header) { padding: 15px 18px; border-color: #e7eded; background: #fbfcfc; }
.flight-card :deep(.el-card__body) { padding: 18px; }
.grid-form :deep(.el-form-item) { margin-bottom: 10px; }
@media (max-width: 1050px) { .grid-form { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 760px) {
  .page-title { font-size: 21px; margin-bottom: 16px; }
  .tool-bar { display: grid; grid-template-columns: 1fr 1fr; }
  .tool-bar :deep(.el-button) { width: 100%; margin: 0; }
  .grid-form { grid-template-columns: 1fr; }
  .flight-card :deep(.el-card__header), .flight-card :deep(.el-card__body) { padding: 14px; }
  .card-header > span { width: 100%; }
  .card-header > div { display: grid; width: 100%; grid-template-columns: 1fr 1fr; gap: 7px; }
  .card-header > div :deep(.el-button) { width: 100%; margin: 0; }
  .card-header > div :deep(.el-button.is-link) { background: transparent; }
  .flight-manage-page > :deep(.el-pagination) { justify-content: center; flex-wrap: wrap; gap: 5px; margin-top: 18px; }
  .flight-manage-page > :deep(.el-pagination .el-pagination__jump) { display: none; }
}
</style>
