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
          />
        </el-form-item>
        <el-form-item label="到达机场">
          <el-autocomplete
              v-model="queryForm.arrivalAirport"
              :fetch-suggestions="queryAirport"
              placeholder="到达机场"
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
              <!-- 🔥 修复正确的按钮结构 -->
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
              <el-form-item label="机号">
                <el-input v-model="item.aircraftReg" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="机型">
                <el-input v-model="item.aircraftType" :disabled="editIndex !== idx" />
              </el-form-item>

              <el-form-item label="起飞机场">
                <el-autocomplete
                    v-model="item.departureAirport"
                    :fetch-suggestions="queryAirport"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="出发航站楼">
                <el-input v-model="item.departureTerminal" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="出发ICAO">
                <el-input v-model="item.departureIcao" :disabled="editIndex !== idx" />
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
              <el-form-item label="飞行距离(km)">
                <el-input v-model.number="item.flightDistanceKm" :disabled="editIndex !== idx" />
              </el-form-item>

              <el-form-item label="到达机场">
                <el-autocomplete
                    v-model="item.arrivalAirport"
                    :fetch-suggestions="queryAirport"
                    :disabled="editIndex !== idx"
                />
              </el-form-item>
              <el-form-item label="到达航站楼">
                <el-input v-model="item.arrivalTerminal" :disabled="editIndex !== idx" />
              </el-form-item>
              <el-form-item label="到达ICAO">
                <el-input v-model="item.arrivalIcao" :disabled="editIndex !== idx" />
              </el-form-item>

              <el-form-item label="落地时间">
                <el-date-picker
                    v-model="item.landingTime"
                    type="datetime"
                    :disabled="editIndex !== idx"
                    style="width:100%"
                />
              </el-form-item>
              <el-form-item label="经停机场">
                <el-autocomplete
                    v-model="item.stopoverAirport"
                    :fetch-suggestions="queryAirport"
                    :disabled="editIndex !== idx"
                    placeholder="无则不填"
                />
              </el-form-item>
              <el-form-item label="座位号">
                <el-input v-model="item.seatNo" :disabled="editIndex !== idx" />
              </el-form-item>
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
        style="text-align:center; margin-top:20px"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFlightList } from '@/modules/travel/apis/flightTickets.js'

// ===================== 机场列表 =====================
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

// ===================== 数据 =====================
const flightList = ref([])

onMounted(async () => {
  const res = await getFlightList()
  flightList.value = res.data || []
})

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
const pageSize = ref(10)

const pageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

const idxStart = computed(() => (currentPage.value - 1) * pageSize.value)

// ===================== 编辑 / 新增 统一逻辑 =====================
const editIndex = ref(-1)

// 进入编辑
const handleEdit = (item, idx) => {
  editIndex.value = idx
}

// 取消
const cancelEdit = () => {
  editIndex.value = -1
}

// 保存
const saveEdit = (idx) => {
  editIndex.value = -1
  ElMessage.success('保存成功')
}

// 新增 = 推入空数据
const handleAdd = () => {
  const newItem = {
    flightNo: '', aircraftReg: '', aircraftType: '',
    departureAirport: '', departureTerminal: '', departureIcao: '',
    takeoffTime: '', boardingMethod: '廊桥', flightDistanceKm: 0,
    arrivalAirport: '', arrivalTerminal: '', arrivalIcao: '',
    landingTime: '', stopoverAirport: '', seatNo: ''
  }
  flightList.value.unshift(newItem)
  editIndex.value = 0
}

// ===================== 删除 =====================
const handleDelete = (idx) => {
  ElMessageBox.confirm('确定删除该航班？', '提示').then(() => {
    flightList.value.splice(idxStart.value + idx, 1)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// ===================== 刷新 =====================
const refresh = async () => {
  const res = await getFlightList()
  flightList.value = res.data || []
  ElMessage.success('刷新成功')
}
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
.flight-card.editing {
  border: 2px solid #1890ff;
  border-radius: 8px;
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