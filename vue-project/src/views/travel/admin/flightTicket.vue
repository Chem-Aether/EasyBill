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
              v-model="queryForm.departureAirport"
              :fetch-suggestions="queryAirport"
              placeholder="起飞机场"
              @select="handleSelectAirport"
              @blur="handleBlurAirport"
          />
        </el-form-item>
        <el-form-item label="到达机场">
          <el-autocomplete
              v-model="queryForm.arrivalAirport"
              :fetch-suggestions="queryAirport"
              placeholder="到达机场"
              @select="handleSelectAirport"
              @blur="handleBlurAirport"
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
      <el-button type="primary" @click="addFlight">+ 追加新机票</el-button>
      <el-button @click="refresh">刷新列表</el-button>
    </div>

    <!-- 卡片列表 -->
    <div class="card-list">
      <div
          v-for="(item, idx) in pageData"
          :key="idx"
          class="flight-card"
      >
        <el-card shadow="hover">
          <div class="card-header">
            <span>第 {{ idxStart + idx + 1 }} 条</span>
            <div>
              <el-button type="primary" link size="small" @click="setEdit(item, idx)">
                修改
              </el-button>
              <el-button type="danger" link size="small" @click="delFlight(item, idx)">
                删除
              </el-button>
            </div>
          </div>

          <el-form
              :model="editItem && editIndex === idx ? editForm : item"
              label-width="120px"
              size="default"
              style="margin-top:10px"
          >
            <div class="grid-form">
              <el-form-item label="航班号">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).flightNo"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="机号">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).aircraftReg"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="机型">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).aircraftType"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>

              <el-form-item label="起飞机场">
                <el-autocomplete
                    v-model="(editItem && editIndex === idx ? editForm : item).departureAirport"
                    :fetch-suggestions="queryAirport"
                    :disabled="!editItem || editIndex !== idx"
                    @select="(val)=>{if(editIndex===idx)editForm.departureAirport=val.value}"
                    @blur="(e)=>handleBlurEdit(e, 'departureAirport')"
                />
              </el-form-item>
              <el-form-item label="出发航站楼">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).departureTerminal"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="出发ICAO">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).departureIcao"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>

              <el-form-item label="起飞时间">
                <el-date-picker
                    v-model="(editItem && editIndex === idx ? editForm : item).takeoffTime"
                    type="datetime"
                    :disabled="!editItem || editIndex !== idx"
                    style="width:100%"
                />
              </el-form-item>
              <el-form-item label="登机方式">
                <el-select
                    v-model="(editItem && editIndex === idx ? editForm : item).boardingMethod"
                    :disabled="!editItem || editIndex !== idx"
                    style="width:100%"
                >
                  <el-option label="廊桥" value="廊桥"/>
                  <el-option label="摆渡车" value="摆渡车"/>
                </el-select>
              </el-form-item>
              <el-form-item label="飞行距离(km)">
                <el-input
                    v-model.number="(editItem && editIndex === idx ? editForm : item).flightDistanceKm"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>

              <el-form-item label="到达机场">
                <el-autocomplete
                    v-model="(editItem && editIndex === idx ? editForm : item).arrivalAirport"
                    :fetch-suggestions="queryAirport"
                    :disabled="!editItem || editIndex !== idx"
                    @select="(val)=>{if(editIndex===idx)editForm.arrivalAirport=val.value}"
                    @blur="(e)=>handleBlurEdit(e, 'arrivalAirport')"
                />
              </el-form-item>
              <el-form-item label="到达航站楼">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).arrivalTerminal"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="到达ICAO">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).arrivalIcao"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>

              <el-form-item label="落地时间">
                <el-date-picker
                    v-model="(editItem && editIndex === idx ? editForm : item).landingTime"
                    type="datetime"
                    :disabled="!editItem || editIndex !== idx"
                    style="width:100%"
                />
              </el-form-item>
              <el-form-item label="经停机场">
                <el-autocomplete
                    v-model="(editItem && editIndex === idx ? editForm : item).stopoverAirport"
                    :fetch-suggestions="queryAirport"
                    :disabled="!editItem || editIndex !== idx"
                    placeholder="无则不填"
                    @select="(val)=>{if(editIndex===idx)editForm.stopoverAirport=val.value}"
                    @blur="(e)=>handleBlurEdit(e, 'stopoverAirport')"
                />
              </el-form-item>
              <el-form-item label="座位号">
                <el-input
                    v-model="(editItem && editIndex === idx ? editForm : item).seatNo"
                    :disabled="!editItem || editIndex !== idx"
                />
              </el-form-item>
            </div>

            <div v-if="editItem && editIndex === idx" style="text-align:center; margin-top:10px">
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
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
        style="text-align:center; margin-top:20px"
    />

    <!-- 新增卡片 -->
    <div v-if="showAdd" class="flight-card add-card">
      <el-card shadow="hover">
        <div class="card-header"><span>新增航班</span></div>
        <el-form :model="addForm" label-width="120px" size="default" style="margin-top:10px">
          <div class="grid-form">
            <el-form-item label="航班号"><el-input v-model="addForm.flightNo" /></el-form-item>
            <el-form-item label="机号"><el-input v-model="addForm.aircraftReg" /></el-form-item>
            <el-form-item label="机型"><el-input v-model="addForm.aircraftType" /></el-form-item>

            <el-form-item label="起飞机场">
              <el-autocomplete
                  v-model="addForm.departureAirport"
                  :fetch-suggestions="queryAirport"
                  placeholder="起飞机场"
                  @select="(val)=>addForm.departureAirport=val.value"
                  @blur="handleBlurAdd"
              />
            </el-form-item>
            <el-form-item label="出发航站楼"><el-input v-model="addForm.departureTerminal" /></el-form-item>
            <el-form-item label="出发ICAO"><el-input v-model="addForm.departureIcao" /></el-form-item>

            <el-form-item label="起飞时间"><el-date-picker v-model="addForm.takeoffTime" type="datetime" style="width:100%"/></el-form-item>
            <el-form-item label="登机方式">
              <el-select v-model="addForm.boardingMethod" style="width:100%">
                <el-option label="廊桥" value="廊桥"/>
                <el-option label="摆渡车" value="摆渡车"/>
              </el-select>
            </el-form-item>
            <el-form-item label="飞行距离"><el-input v-model.number="addForm.flightDistanceKm" /></el-form-item>

            <el-form-item label="到达机场">
              <el-autocomplete
                  v-model="addForm.arrivalAirport"
                  :fetch-suggestions="queryAirport"
                  placeholder="到达机场"
                  @select="(val)=>addForm.arrivalAirport=val.value"
                  @blur="handleBlurAdd"
              />
            </el-form-item>
            <el-form-item label="到达航站楼"><el-input v-model="addForm.arrivalTerminal" /></el-form-item>
            <el-form-item label="到达ICAO"><el-input v-model="addForm.arrivalIcao" /></el-form-item>

            <el-form-item label="落地时间"><el-date-picker v-model="addForm.landingTime" type="datetime" style="width:100%"/></el-form-item>
            <el-form-item label="经停机场">
              <el-autocomplete
                  v-model="addForm.stopoverAirport"
                  :fetch-suggestions="queryAirport"
                  placeholder="经停机场"
                  @select="(val)=>addForm.stopoverAirport=val.value"
                  @blur="handleBlurAdd"
              />
            </el-form-item>
            <el-form-item label="座位号"><el-input v-model="addForm.seatNo" /></el-form-item>
          </div>
          <div style="text-align:center; margin-top:10px">
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

// ===================== 机场列表（可扩展）=====================
const airportList = [
  "北京首都", "北京大兴", "上海浦东", "上海虹桥",
  "广州白云", "深圳宝安", "成都天府", "成都双流",
  "杭州萧山", "重庆江北", "西安咸阳", "武汉天河",
  "南京禄口", "长沙黄花", "青岛流亭", "青岛胶东"
]

// 搜索机场
const queryAirport = (queryString, cb) => {
  if (!queryString?.trim()) return cb([])
  const res = airportList
      .filter(s => s.includes(queryString.trim()))
      .map(s => ({ value: s }))
  cb(res)
}

// 选择
const handleSelectAirport = () => {}

// 查询框失焦校验
const handleBlurAirport = (e) => {
  const val = e.target.value?.trim()
  if (!val) return
  if (!airportList.includes(val)) {
    e.target.value = ''
    ElMessage.warning('只能选择预设机场')
  }
}

// 新增失焦校验
const handleBlurAdd = (e) => {
  const val = e.target.value?.trim()
  if (!val) return
  if (!airportList.includes(val)) {
    e.target.value = ''
    ElMessage.warning('只能选择预设机场')
  }
}

// 修改失焦校验
const handleBlurEdit = (e, field) => {
  const val = e.target.value?.trim()
  if (!val) return
  if (!airportList.includes(val)) {
    editForm[field] = ''
    e.target.value = ''
    ElMessage.warning('只能选择预设机场')
  }
}

// ===================== 原始数据 =====================
const flightList = ref([
  {
    flightNo: 'MU5106', aircraftReg: 'B-301D', aircraftType: 'A330',
    departureAirport: '北京首都', departureTerminal: 'T2', departureIcao: 'ZBAA',
    takeoffTime: '2026-04-10 08:05:00', boardingMethod: '廊桥',
    arrivalAirport: '上海浦东', arrivalTerminal: 'T1', arrivalIcao: 'ZSPD',
    landingTime: '2026-04-10 10:15:00', stopoverAirport: '', flightDistanceKm: 1318, seatNo: '32A'
  },
  {
    flightNo: 'CA1831', aircraftReg: 'B-8586', aircraftType: 'B737',
    departureAirport: '广州白云', departureTerminal: 'T2', departureIcao: 'ZGGG',
    takeoffTime: '2026-04-11 14:20:00', boardingMethod: '摆渡车',
    arrivalAirport: '成都天府', arrivalTerminal: 'T2', arrivalIcao: 'ZUTF',
    landingTime: '2026-04-11 16:40:00', stopoverAirport: '', flightDistanceKm: 1270, seatNo: '15F'
  }
])

// ===================== 查询 =====================
const queryForm = reactive({
  flightNo: '',
  departureAirport: '',
  arrivalAirport: ''
})

const filteredList = computed(() => {
  return flightList.value.filter(item => {
    const matchNo = !queryForm.flightNo || item.flightNo.includes(queryForm.flightNo)
    const matchDep = !queryForm.departureAirport || item.departureAirport.includes(queryForm.departureAirport)
    const matchArr = !queryForm.arrivalAirport || item.arrivalAirport.includes(queryForm.arrivalAirport)
    return matchNo && matchDep && matchArr
  })
})

const doQuery = () => currentPage.value = 1
const resetQuery = () => {
  queryForm.flightNo = ''
  queryForm.departureAirport = ''
  queryForm.arrivalAirport = ''
}

// ===================== 分页 =====================
const currentPage = ref(1)
const pageSize = ref(2)

const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredList.value.slice(start, end)
})

const idxStart = computed(() => (currentPage.value - 1) * pageSize.value)
const handlePageChange = () => {}

// ===================== 修改 =====================
const editItem = ref(null)
const editIndex = ref(-1)
const editForm = reactive({})

const setEdit = (item, idx) => {
  editItem.value = item
  editIndex.value = idx
  Object.assign(editForm, item)
}

const cancelEdit = () => {
  editItem.value = null
  editIndex.value = -1
}

const saveEdit = (idx) => {
  const realIdx = idxStart.value + idx
  flightList.value[realIdx] = { ...editForm }
  cancelEdit()
  ElMessage.success('修改成功')
}

// ===================== 删除 =====================
const delFlight = (item, idx) => {
  ElMessageBox.confirm('确认删除？', '提示').then(() => {
    const realIdx = idxStart.value + idx
    flightList.value.splice(realIdx, 1)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// ===================== 新增 =====================
const showAdd = ref(false)
const addForm = reactive({
  flightNo: '', aircraftReg: '', aircraftType: '',
  departureAirport: '', departureTerminal: '', departureIcao: '',
  takeoffTime: '', boardingMethod: '廊桥',
  arrivalAirport: '', arrivalTerminal: '', arrivalIcao: '',
  landingTime: '', stopoverAirport: '', flightDistanceKm: 0, seatNo: ''
})

const addFlight = () => showAdd.value = true
const cancelAdd = () => showAdd.value = false

const saveAdd = () => {
  const check = [addForm.departureAirport, addForm.arrivalAirport, addForm.stopoverAirport]
  for (const s of check) {
    if (s && !airportList.includes(s)) {
      ElMessage.warning('请选择系统预设机场')
      return
    }
  }
  flightList.value.unshift({ ...addForm })
  showAdd.value = false
  currentPage.value = 1
  ElMessage.success('新增成功')
}

const refresh = () => ElMessage.success('刷新成功')
</script>

<style scoped>
.flight-manage-page {
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
.flight-card {
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
</style>