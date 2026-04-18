<template>
  <div class="train-manage-page">
    <div class="page-title">铁路出行记录管理</div>

    <!-- 查询 -->
    <el-card class="query-card" shadow="hover">
      <el-form :model="queryForm" inline size="default">
        <el-form-item label="车次">
          <el-input v-model="queryForm.trainNo" placeholder="G123" clearable />
        </el-form-item>
        <el-form-item label="发站">
          <el-autocomplete
              v-model="queryForm.startStation"
              :fetch-suggestions="queryStation"
              placeholder="发站"
          />
        </el-form-item>
        <el-form-item label="到站">
          <el-autocomplete
              v-model="queryForm.endStation"
              :fetch-suggestions="queryStation"
              placeholder="到站"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="tool-bar">
      <el-button type="primary" @click="handleAdd">+ 新增行程</el-button>
      <el-button @click="handleRefresh">刷新</el-button>
    </div>

    <el-skeleton v-if="loading" rows="8" />

    <div v-else-if="filteredList.length === 0" class="empty-tip">
      <el-empty description="暂无记录" />
    </div>

    <div v-else class="card-list">
      <div
          v-for="(item, idx) in pageData"
          :key="idx"
          class="train-card"
          :class="{ editing: editIndex === idx }"
      >
        <el-card shadow="hover">
          <div class="card-header">
            <span>
              {{ editIndex === idx ? '编辑行程' : `第 ${idxStart + idx + 1} 条` }}
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
                    @click="handleDelete(idx)"
                >删除</el-button>
              </template>
              <template v-else>
                <el-button size="small" @click="cancelEdit">取消</el-button>
                <el-button type="primary" size="small" @click="saveEdit(idx)">保存</el-button>
              </template>
            </div>
          </div>

          <el-form :model="item" label-width="110px" style="margin-top:10px">
            <div class="grid-form">
              <el-form-item label="车次">
                <el-input v-model="item.trainNo" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="列车类型">
                <el-input v-model="item.trainType" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="车型">
                <el-input v-model="item.trainModel" :disabled="editIndex !== idx" />
              </el-form-item>

              <el-form-item label="发站">
                <el-autocomplete
                    v-model="item.startStation"
                    :fetch-suggestions="queryStation"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="发车时间">
                <el-date-picker
                    v-model="item.departureDatetime"
                    type="datetime"
                    style="width:100%"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="始发站">
                <el-autocomplete
                    v-model="item.originStation"
                    :fetch-suggestions="queryStation"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>

              <el-form-item label="到站">
                <el-autocomplete
                    v-model="item.endStation"
                    :fetch-suggestions="queryStation"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="到达时间">
                <el-date-picker
                    v-model="item.arrivalDatetime"
                    type="datetime"
                    style="width:100%"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="终到站">
                <el-autocomplete
                    v-model="item.terminalStation"
                    :fetch-suggestions="queryStation"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>

              <el-form-item label="座位号">
                <el-input v-model="item.seatNo" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="座位等级">
                <el-select
                    v-model="item.seatClass"
                    style="width:100%"
                    :disabled="editIndex !== idx"
                >
                  <el-option label="二等座" value="二等座" />
                  <el-option label="一等座" value="一等座" />
                  <el-option label="商务座" value="商务座" />
                </el-select>
              </el-form-item>

              <el-form-item label="里程">
                <el-input v-model.number="item.mileageKm" :disabled="editIndex !== idx" />
              </el-form-item>
            </div>

            <!-- 途经站 -->
            <div class="station-section">
              <div class="station-header" @click="toggleStation(idx)">
                <span>途经站：{{ (item.stationList || []).length }} 个</span>
                <span>{{ expandIdx === idx ? '收起' : '展开' }}</span>
              </div>

              <div v-if="expandIdx === idx" style="margin-top:10px;">
                <el-timeline>
                  <!-- 编辑模式：可拖拽 -->
                  <draggable
                      v-if="editIndex === idx"
                      v-model="item.stationList"
                      @end="renumber(item.stationList)"
                      ghost-class="drag-ghost"
                      :options="{
                      handle: '.drag-handle',
                      filter: 'input,button',
                      preventOnFilter: true
                    }"
                  >
                    <template #item="{ element, index }">
                      <el-timeline-item :color="getColorByIndex(index).dot">
                        <div class="station-item" :style="{ background: getColorByIndex(index).bg }">
                          <span class="drag-handle" style="cursor:move; margin-right:6px;">☰</span>
                          <el-autocomplete
                              v-model="element.stationName"
                              :fetch-suggestions="queryStation"
                              placeholder="站点"
                              style="flex:1"
                          />
                          <span style="margin-left:8px;">#{{ element.stationOrder }}</span>
                          <el-button
                              type="text"
                              size="small"
                              danger
                              @click="delStation(item.stationList, element)"
                          >删</el-button>
                        </div>
                      </el-timeline-item>
                    </template>
                  </draggable>

                  <!-- 查看模式 -->
                  <template v-else>
                    <el-timeline-item
                        v-for="(st, sidx) in item.stationList"
                        :key="sidx"
                        :color="getColorByIndex(sidx).dot"
                    >
                      <div
                          class="station-item"
                          :style="{ background: getColorByIndex(sidx).bg }"
                      >
                        {{ st.stationOrder }}. {{ st.stationName }}
                      </div>
                    </el-timeline-item>
                  </template>
                </el-timeline>

                <!-- 添加 -->
                <div v-if="editIndex === idx" style="margin-top:8px; display:flex; gap:8px; align-items:center">
                  <el-autocomplete
                      v-model="tempStationName"
                      :fetch-suggestions="queryStation"
                      placeholder="搜索站点"
                      style="width:220px"
                  />
                  <el-button type="primary" size="small" @click="confirmAddStation(item.stationList)">
                    添加站点
                  </el-button>
                </div>
              </div>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>

    <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredList.length"
        layout="total,prev,pager,next,jumper"
        style="margin-top:20px;text-align:center"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable'
import { getTrainList } from '@/modules/travel/apis/trainTickets.js'
import { useTrainStationStore } from '@/modules/travel/stores/allTrainStationsList.js'

const stationStore = useTrainStationStore()
const loading = ref(true)
const trainList = ref([])

const currentPage = ref(1)
const pageSize = ref(10)
const expandIdx = ref(-1)
const editIndex = ref(-1)
const tempStationName = ref('')

const queryForm = reactive({ trainNo: '', startStation: '', endStation: '' })

onMounted(async () => {
  await stationStore.initTrainStations()
  await loadData()
})

const loadData = async () => {
  loading.value = true
  const res = await getTrainList()
  trainList.value = (res.data || []).map(i => {
    i.stationList = i.stationList || []
    return i
  })
  loading.value = false
}

const queryStation = (q, cb) => {
  const data = stationStore.stationList
      .filter(i => i.name.includes(q || ''))
      .map(i => ({ value: i.name }))
  cb(data)
}

const filteredList = computed(() => trainList.value)
const pageData = computed(() => {
  const s = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(s, s + pageSize.value)
})
const idxStart = computed(() => (currentPage.value - 1) * pageSize.value)

const toggleStation = (idx) => {
  expandIdx.value = expandIdx.value === idx ? -1 : idx
}

const handleEdit = (item, idx) => {
  editIndex.value = idx
}
const cancelEdit = () => {
  editIndex.value = -1
}
const saveEdit = (idx) => {
  editIndex.value = -1
  ElMessage.success('保存成功')
}

const handleAdd = () => {
  const newItem = {
    trainNo: '',
    trainType: '',
    trainModel: '',
    startStation: '',
    endStation: '',
    originStation: '',
    terminalStation: '',
    departureDatetime: '',
    arrivalDatetime: '',
    seatNo: '',
    seatClass: '二等座',
    mileageKm: 0,
    stationList: []
  }
  trainList.value.unshift(newItem)
  editIndex.value = 0
}

const renumber = (list) => {
  list.forEach((s, i) => s.stationOrder = i + 1)
}
const confirmAddStation = (list) => {
  const name = tempStationName.value?.trim()
  if (!name) {
    ElMessage.warning('请选择站点')
    return
  }
  list.push({
    stationName: name,
    stationOrder: list.length + 1
  })
  tempStationName.value = ''
}
const delStation = (list, el) => {
  list.splice(list.indexOf(el), 1)
  renumber(list)
}

const handleDelete = (idx) => {
  ElMessageBox.confirm('确定删除？').then(() => {
    trainList.value.splice(idxStart.value + idx, 1)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const handleRefresh = async () => {
  await loadData()
  ElMessage.success('刷新成功')
}
const doQuery = () => {}
const resetQuery = () => Object.assign(queryForm, { trainNo: '', startStation: '', endStation: '' })

// 10色循环
const loopColors = [
  '#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1',
  '#eb2f96', '#13c2c2', '#fa8c16', '#a0d911', '#531dab'
]
const bgLoopColors = [
  '#e6f7ff', '#f0fdf4', '#fffbe6', '#fff1f0', '#f9f0ff',
  '#fff0f6', '#e6fffb', '#fff7e6', '#f9ffe6', '#f0e6ff'
]

const getColorByIndex = (index) => {
  const i = index % 10
  return { dot: loopColors[i], bg: bgLoopColors[i] }
}
</script>

<style scoped>
.train-manage-page { width: 96%; margin: 20px auto; }
.page-title { font-size: 22px; font-weight: bold; margin-bottom: 15px; }
.query-card { margin-bottom: 15px; }
.tool-bar { margin-bottom: 15px; }
.card-list { display: flex; flex-direction: column; gap: 15px; }
.train-card { width: 100%; }
.train-card.editing { border: 2px solid #1890ff; border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; font-weight: 600; }
.grid-form { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.station-section { margin-top: 10px; }
.station-header { display: flex; justify-content: space-between; cursor: pointer; padding: 5px 0; font-weight: 500; }
.station-item { display: flex; align-items: center; padding: 6px 10px; border-radius: 4px; margin: 4px 0; }
.empty-tip { padding: 40px 0; text-align: center; }
.drag-ghost { opacity: 0.4; background: #eee; }
</style>