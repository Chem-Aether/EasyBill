<template>
  <div class="train-manage-page">
    <div class="page-title">铁路出行记录管理</div>

    <!-- 🔍 查询条件栏 -->
    <el-card class="query-card" shadow="hover">
      <el-form :model="queryForm" inline size="default">
        <el-form-item label="车次">
          <el-input v-model="queryForm.trainNo" placeholder="G123" clearable />
        </el-form-item>
        <el-form-item label="发站">
          <el-autocomplete
              v-model="queryForm.startStation"
              :fetch-suggestions="queryStation"
              placeholder="北京南"
              @select="handleSelectStation"
              @blur="handleBlurStation"
          />
        </el-form-item>
        <el-form-item label="到站">
          <el-autocomplete
              v-model="queryForm.endStation"
              :fetch-suggestions="queryStation"
              placeholder="上海虹桥"
              @select="handleSelectStation"
              @blur="handleBlurStation"
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
      <el-button type="primary" @click="addTrain">+ 追加新铁路</el-button>
      <el-button @click="refresh">刷新</el-button>
    </div>

    <!-- 卡片列表 -->
    <div class="card-list">
      <div v-for="(item, idx) in pageData" :key="idx" class="train-card">
        <el-card shadow="hover">
          <div class="card-header">
            <span>第 {{ idxStart + idx + 1 }} 条</span>
            <div>
              <el-button type="primary" link size="small" @click="setEdit(item, idx)">修改</el-button>
              <el-button type="danger" link size="small" @click="delTrain(idx)">删除</el-button>
            </div>
          </div>

          <el-form
              :model="editIndex === idx ? editForm : item"
              label-width="110px"
              size="default"
              style="margin-top:10px"
          >
            <div class="grid-form">
              <el-form-item label="车次">
                <el-input
                    v-model="(editIndex === idx ? editForm : item).trainNo"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="列车类型">
                <el-input
                    v-model="(editIndex === idx ? editForm : item).trainType"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="车型">
                <el-input
                    v-model="(editIndex === idx ? editForm : item).trainModel"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>

              <el-form-item label="发站">
                <el-autocomplete
                    v-model="(editIndex === idx ? editForm : item).startStation"
                    :fetch-suggestions="queryStation"
                    placeholder="发站"
                    :disabled="editIndex !== idx"
                    @select="(val) => { if(editIndex === idx) editForm.startStation = val.value }"
                />
              </el-form-item>
              <el-form-item label="到站">
                <el-autocomplete
                    v-model="(editIndex === idx ? editForm : item).endStation"
                    :fetch-suggestions="queryStation"
                    placeholder="到站"
                    :disabled="editIndex !== idx"
                    @select="(val) => { if(editIndex === idx) editForm.endStation = val.value }"
                />
              </el-form-item>
              <el-form-item label="始发站">
                <el-autocomplete
                    v-model="(editIndex === idx ? editForm : item).originStation"
                    :fetch-suggestions="queryStation"
                    placeholder="始发站"
                    :disabled="editIndex !== idx"
                    @select="(val) => { if(editIndex === idx) editForm.originStation = val.value }"
                />
              </el-form-item>
              <el-form-item label="终到站">
                <el-autocomplete
                    v-model="(editIndex === idx ? editForm : item).terminalStation"
                    :fetch-suggestions="queryStation"
                    placeholder="终到站"
                    :disabled="editIndex !== idx"
                    @select="(val) => { if(editIndex === idx) editForm.terminalStation = val.value }"
                />
              </el-form-item>

              <el-form-item label="发车时间">
                <el-date-picker
                    v-model="(editIndex === idx ? editForm : item).departureDatetime"
                    type="datetime"
                    style="width:100%"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="到达时间">
                <el-date-picker
                    v-model="(editIndex === idx ? editForm : item).arrivalDatetime"
                    type="datetime"
                    style="width:100%"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="座位号">
                <el-input
                    v-model="(editIndex === idx ? editForm : item).seatNo"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="座位等级">
                <el-select
                    v-model="(editIndex === idx ? editForm : item).seatClass"
                    style="width:100%"
                    :disabled="editIndex !== idx"
                >
                  <el-option label="二等座" value="二等座" />
                  <el-option label="一等座" value="一等座" />
                  <el-option label="商务座" value="商务座" />
                  <el-option label="硬卧" value="硬卧" />
                  <el-option label="软卧" value="软卧" />
                </el-select>
              </el-form-item>
              <el-form-item label="里程(km)">
                <el-input
                    v-model.number="(editIndex === idx ? editForm : item).mileageKm"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
            </div>

            <!-- 途经站 -->
            <div class="station-section" style="margin-top:12px;border-top:1px solid #eee;padding-top:8px">
              <div class="station-header" @click="toggleStation(idx)" style="cursor:pointer;display:flex;justify-content:space-between">
                <span>途经站 {{ item.stationList?.length || 0 }} 个</span>
                <span>{{ expandIdx === idx ? '收起' : '展开' }}</span>
              </div>

              <el-timeline v-if="expandIdx === idx" style="margin-top:10px">
                <el-timeline-item
                    v-for="(st, sidx) in (editIndex === idx ? (editForm.stationList || []) : (item.stationList || []))"
                    :key="sidx"
                >
                  {{ st.stationOrder }}. {{ st.stationName }}
                </el-timeline-item>
              </el-timeline>
            </div>

            <div v-if="editIndex === idx" style="text-align:center; margin-top:10px">
              <el-button size="small" @click="cancelEdit">取消</el-button>
              <el-button type="primary" size="small" @click="saveEdit(idx)">保存</el-button>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredList.length"
        layout="total,prev,pager,next,jumper"
        @current-change="handlePageChange"
        style="text-align:center;margin-top:20px"
    />

    <!-- 新增卡片 -->
    <div v-if="showAdd" class="train-card add-card">
      <el-card shadow="hover">
        <div class="card-header"><span>新增铁路行程</span></div>
        <el-form :model="addForm" label-width="110px" size="default" style="margin-top:10px">
          <div class="grid-form">
            <el-form-item label="车次"><el-input v-model="addForm.trainNo" /></el-form-item>
            <el-form-item label="列车类型"><el-input v-model="addForm.trainType" /></el-form-item>
            <el-form-item label="车型"><el-input v-model="addForm.trainModel" /></el-form-item>

            <el-form-item label="发站">
              <el-autocomplete
                  v-model="addForm.startStation"
                  :fetch-suggestions="queryStation"
                  placeholder="发站"
                  @select="(item)=>addForm.startStation=item.value"
                  @blur="handleBlurAdd"
              />
            </el-form-item>
            <el-form-item label="到站">
              <el-autocomplete
                  v-model="addForm.endStation"
                  :fetch-suggestions="queryStation"
                  placeholder="到站"
                  @select="(item)=>addForm.endStation=item.value"
                  @blur="handleBlurAdd"
              />
            </el-form-item>
            <el-form-item label="始发站">
              <el-autocomplete
                  v-model="addForm.originStation"
                  :fetch-suggestions="queryStation"
                  placeholder="始发站"
                  @select="(item)=>addForm.originStation=item.value"
                  @blur="handleBlurAdd"
              />
            </el-form-item>
            <el-form-item label="终到站">
              <el-autocomplete
                  v-model="addForm.terminalStation"
                  :fetch-suggestions="queryStation"
                  placeholder="终到站"
                  @select="(item)=>addForm.terminalStation=item.value"
                  @blur="handleBlurAdd"
              />
            </el-form-item>

            <el-form-item label="发车时间"><el-date-picker v-model="addForm.departureDatetime" type="datetime" style="width:100%"/></el-form-item>
            <el-form-item label="到达时间"><el-date-picker v-model="addForm.arrivalDatetime" type="datetime" style="width:100%"/></el-form-item>
            <el-form-item label="座位号"><el-input v-model="addForm.seatNo" /></el-form-item>
            <el-form-item label="座位等级">
              <el-select v-model="addForm.seatClass" style="width:100%">
                <el-option label="二等座" value="二等座"/><el-option label="一等座" value="一等座"/><el-option label="商务座" value="商务座"/>
              </el-select>
            </el-form-item>
            <el-form-item label="里程"><el-input v-model.number="addForm.mileageKm" /></el-form-item>
          </div>

          <!-- 途经站添加 -->
          <div style="margin-top:10px">
            <div class="station-header">
              <span>途经站</span>
              <el-button size="small" type="primary" @click="addStation">+ 加站</el-button>
            </div>

            <el-autocomplete
                v-if="addingStation"
                v-model="tempStation"
                :fetch-suggestions="queryStation"
                placeholder="搜索站点"
                style="width:200px;margin-right:10px"
                @select="handleSelectTemp"
                @blur="handleBlurTemp"
            />

            <el-button v-if="addingStation" size="small" @click="confirmStation">确认</el-button>

            <el-timeline style="margin-top:10px">
              <el-timeline-item v-for="(st, sidx) in addForm.stations" :key="sidx" :timestamp="st.time">
                {{ st.name }}
                <el-button type="text" danger size="small" @click="delStation(sidx)">删</el-button>
              </el-timeline-item>
            </el-timeline>
          </div>

          <div style="text-align:center;margin-top:15px">
            <el-button size="small" @click="cancelAdd">取消</el-button>
            <el-button type="primary" size="small" @click="saveAdd">确认添加</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getTrainList } from '@/modules/travel/apis/trainTickets.js'

// 站点列表
const stationList = [
  "北京南","天津南","济南西","徐州东","南京南","上海虹桥",
  "杭州东","合肥南","福州南","厦门北","深圳北","广州南",
  "武汉","长沙南","郑州东","石家庄","太原南","西安北"
]

// 搜索方法
const queryStation = (queryString, cb) => {
  if (!queryString?.trim()) return cb([])
  const res = stationList
      .filter(s => s.includes(queryString.trim()))
      .map(s => ({ value: s }))
  cb(res)
}

// 选择站点
const handleSelectStation = (item) => {}

// 离开输入框校验（只能选预设）
const handleBlurStation = (e) => {
  const val = e.target.value?.trim()
  if (!val) return
  if (!stationList.includes(val)) {
    e.target.value = ''
    ElMessage.warning('只能选择预设站点')
  }
}

// 新增表单失焦校验
const handleBlurAdd = (e) => {
  const val = e.target.value?.trim()
  if (!val) return
  if (!stationList.includes(val)) {
    e.target.value = ''
    ElMessage.warning('只能选择预设站点')
  }
}

// 编辑字段失焦校验
const handleBlurField = (e, form, field) => {
  if (!form) return
  const val = e.target.value?.trim()
  if (!val) return
  if (!stationList.includes(val)) {
    form[field] = ''
    e.target.value = ''
    ElMessage.warning('只能选择预设站点')
  }
}

// 临时途经站选择 & 校验
const tempStation = ref('')
const handleSelectTemp = (item) => {
  tempStation.value = item.value
}
const handleBlurTemp = (e) => {
  const val = e.target.value?.trim()
  if (!val) return
  if (!stationList.includes(val)) {
    tempStation.value = ''
    e.target.value = ''
    ElMessage.warning('只能选择预设站点')
  }
}

// ===================== 以下是原有逻辑 ========================
const trainList = ref([])
getTrainList().then(res => {
  console.log('统计数据', res.data)
  trainList.value = res.data || []
})

// 查询
const queryForm = reactive({ trainNo: '', startStation: '', endStation: '' })
const filteredList = computed(() => {
  return trainList.value.filter(item => {
    const no = !queryForm.trainNo || item.trainNo.includes(queryForm.trainNo)
    const start = !queryForm.startStation || item.startStation.includes(queryForm.startStation)
    const end = !queryForm.endStation || item.endStation.includes(queryForm.endStation)
    return no && start && end
  })
})
const doQuery = () => currentPage.value = 1
const resetQuery = () => {
  queryForm.trainNo = ''
  queryForm.startStation = ''
  queryForm.endStation = ''
}

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})
const idxStart = computed(() => (currentPage.value - 1) * pageSize.value)
const handlePageChange = () => {}

// 途经站展开/收起
const expandIdx = ref(-1)
const toggleStation = (idx) => {
  expandIdx.value = expandIdx.value === idx ? -1 : idx
}

// 编辑
const editIndex = ref(-1)
const editForm = reactive({ stations: [] })
const setEdit = (item, idx) => {
  editIndex.value = idx
  Object.assign(editForm, item)
}
const cancelEdit = () => editIndex.value = -1
const saveEdit = (idx) => {
  const realIdx = idxStart.value + idx
  trainList.value[realIdx] = { ...editForm }
  editIndex.value = -1
  ElMessage.success('修改成功')
}

// 删除
const delTrain = (idx) => {
  ElMessageBox.confirm('确定删除？').then(() => {
    const realIdx = idxStart.value + idx
    trainList.value.splice(realIdx, 1)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 新增
const showAdd = ref(false)
const addForm = reactive({
  trainNo: '', trainType: '', trainModel: '',
  startStation: '', endStation: '', originStation: '', terminalStation: '',
  departureDatetime: '', arrivalDatetime: '', seatNo: '', seatClass: '二等座', mileageKm: 0,
  stations: []
})
const addingStation = ref(false)

const addStation = () => {
  tempStation.value = ''
  addingStation.value = true
}

const confirmStation = () => {
  const val = tempStation.value?.trim()
  if (!val) {
    ElMessage.warning('请选择站点')
    return
  }
  if (!stationList.includes(val)) {
    ElMessage.warning('只能选择预设站点')
    tempStation.value = ''
    return
  }
  addForm.stations.push({
    name: val,
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  })
  tempStation.value = ''
  addingStation.value = false
}

const delStation = (sidx) => addForm.stations.splice(sidx, 1)

const addTrain = () => showAdd.value = true
const cancelAdd = () => showAdd.value = false

const saveAdd = () => {
  const check = [addForm.startStation, addForm.endStation, addForm.originStation, addForm.terminalStation]
  for (const s of check) {
    if (s && !stationList.includes(s)) {
      ElMessage.warning('请选择合法站点')
      return
    }
  }
  trainList.value.unshift({ ...addForm })
  showAdd.value = false
  currentPage.value = 1
  ElMessage.success('新增成功')
}

const refresh = () => ElMessage.success('刷新成功')
</script>

<style scoped>
.train-manage-page {
  width: 96%;
  margin: 20px auto;
}
.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 15px;
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
  gap: 15px;
}
.train-card {
  width: 100%;
}
.add-card {
  margin-top: 20px;
  border: 1px dashed #999;
}
.card-header {
  display: flex;
  justify-content: space-between;
  font-weight: 600;
}
.grid-form {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.station-section {
  margin-top: 10px;
}
.station-header {
  display: flex;
  justify-content: space-between;
  cursor: pointer;
  padding: 5px 0;
  font-weight: 500;
}
</style>