<template>
  <div class="footprint-page">
    <div class="page-header">
      <div>
        <h1>足迹与点亮地区</h1>
        <p>每条足迹对应一个区县，保存后将在旅行地图中点亮所属城市。</p>
      </div>
      <el-button type="primary" @click="openCreate">新增足迹</el-button>
    </div>

    <div class="toolbar">
      <el-input v-model="filterText" clearable placeholder="搜索地区或地点" style="width: 280px" />
      <span class="count">共 {{ filteredRows.length }} 条</span>
    </div>

    <el-table
      v-loading="loading"
      :data="pageRows"
      :default-sort="{ prop: 'visitTime', order: 'descending' }"
      stripe
      empty-text="暂无足迹记录"
      @sort-change="handleSortChange"
    >
      <el-table-column prop="regionName" label="点亮地区" min-width="220" sortable="custom" />
      <el-table-column prop="spotName" label="地点名称" min-width="180" sortable="custom" />
      <el-table-column prop="spotType" label="类型" width="120" sortable="custom" />
      <el-table-column prop="visitTime" label="到访日期" width="140" sortable="custom" />
      <el-table-column label="坐标" min-width="190">
        <template #default="scope">
          <span v-if="scope.row.longitude != null">
            {{ Number(scope.row.longitude).toFixed(5) }}, {{ Number(scope.row.latitude).toFixed(5) }}
          </span>
          <span v-else class="muted">未选点</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="sortedRows.length"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handlePageSizeChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="form.spotId ? '编辑足迹' : '新增足迹'"
      width="min(920px, 94vw)"
      destroy-on-close
    >
      <el-form label-width="90px">
        <el-form-item label="地图选点" required>
          <FootprintMapPicker
            :longitude="form.longitude"
            :latitude="form.latitude"
            @pick="handleMapPick"
            @clear="clearMapPoint"
          />
        </el-form-item>
        <el-form-item v-if="form.adcode && !manualRegionVisible" label="所属地区">
          <div class="region-result">
            <span>{{ selectedRegionName }}</span>
            <el-button link type="primary" @click="manualRegionVisible = true">更正</el-button>
          </div>
        </el-form-item>
        <el-form-item v-else label="所属地区" required>
          <el-select
            v-model="form.adcode"
            filterable
            remote
            clearable
            reserve-keyword
            :remote-method="queryRegions"
            :loading="regionLoading"
            placeholder="输入区县名称，如 西湖"
            style="width: 100%"
            @change="handleRegionChange"
          >
            <el-option
              v-for="item in regionOptions"
              :key="item.code"
              :label="item.fullName"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <div class="field-grid">
          <el-form-item label="地点名称" required>
            <el-input v-model="form.spotName" placeholder="选择 POI 后自动填写，也可手动输入" maxlength="100" />
          </el-form-item>
          <el-form-item label="地点类型" required>
            <el-select v-model="form.spotType" style="width: 100%">
              <el-option v-for="type in spotTypes" :key="type" :label="type" :value="type" />
            </el-select>
          </el-form-item>
          <el-form-item label="到访日期">
            <el-date-picker
              v-model="form.visitTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-collapse class="more-fields">
          <el-collapse-item title="更多信息" name="more">
            <el-form-item label="地址备注">
              <el-input v-model="form.address" placeholder="街道、门牌号或位置说明" maxlength="255" />
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存并点亮</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addFootprint, deleteFootprint, getFootprints, updateFootprint } from '@/modules/travel/apis/travel.js'
import { searchRegions } from '@/modules/travel/apis/sysResource.js'
import FootprintMapPicker from '@/modules/travel/components/FootprintMapPicker.vue'

const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const regionLoading = ref(false)
const filterText = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const sortState = reactive({ prop: 'visitTime', order: 'descending' })
const dialogVisible = ref(false)
const manualRegionVisible = ref(false)
const regionOptions = ref([])
const spotTypes = ['景点', '商业街', '交通枢纽', '校园', '机场', '公园', '街道', '区域', '其他']
const form = reactive({
  spotId: null,
  adcode: '',
  spotName: '',
  spotType: '景点',
  visitTime: null,
  longitude: null,
  latitude: null,
  address: ''
})

const filteredRows = computed(() => {
  const keyword = filterText.value.trim().toLowerCase()
  if (!keyword) return rows.value
  return rows.value.filter(item =>
    `${item.regionName || ''} ${item.spotName || ''} ${item.spotType || ''}`.toLowerCase().includes(keyword)
  )
})

const selectedRegionName = computed(() => {
  return regionOptions.value.find(item => item.code === form.adcode)?.fullName || form.adcode
})

const sortedRows = computed(() => {
  if (!sortState.prop || !sortState.order) return filteredRows.value
  const direction = sortState.order === 'ascending' ? 1 : -1
  return [...filteredRows.value].sort((left, right) => {
    const a = left[sortState.prop]
    const b = right[sortState.prop]
    if (a == null || a === '') return b == null || b === '' ? 0 : 1
    if (b == null || b === '') return -1
    return String(a).localeCompare(String(b), 'zh-CN', { numeric: true }) * direction
  })
})

const pageRows = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return sortedRows.value.slice(start, start + pageSize.value)
})

watch(filterText, () => {
  currentPage.value = 1
})

watch(() => sortedRows.value.length, total => {
  const lastPage = Math.max(1, Math.ceil(total / pageSize.value))
  if (currentPage.value > lastPage) currentPage.value = lastPage
})

onMounted(loadRows)

async function loadRows() {
  loading.value = true
  try {
    const res = await getFootprints()
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

function handleSortChange({ prop, order }) {
  sortState.prop = prop || ''
  sortState.order = order || ''
  currentPage.value = 1
}

function handlePageSizeChange() {
  currentPage.value = 1
}

function handleRegionChange(code) {
  if (code) manualRegionVisible.value = false
}

function resetForm() {
  Object.assign(form, {
    spotId: null,
    adcode: '',
    spotName: '',
    spotType: '景点',
    visitTime: null,
    longitude: null,
    latitude: null,
    address: ''
  })
  regionOptions.value = []
  manualRegionVisible.value = false
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, {
    spotId: row.spotId,
    adcode: row.adcode,
    spotName: row.spotName,
    spotType: row.spotType,
    visitTime: row.visitTime || null,
    longitude: row.longitude == null ? null : Number(row.longitude),
    latitude: row.latitude == null ? null : Number(row.latitude),
    address: row.address || ''
  })
  regionOptions.value = [{ code: row.adcode, fullName: row.regionName }]
  manualRegionVisible.value = !row.adcode
  dialogVisible.value = true
}

async function queryRegions(keyword) {
  if (!keyword?.trim()) {
    regionOptions.value = []
    return
  }
  regionLoading.value = true
  try {
    const res = await searchRegions(keyword.trim(), 3)
    regionOptions.value = res.data || []
  } catch {
    regionOptions.value = []
  } finally {
    regionLoading.value = false
  }
}

async function handleMapPick(point) {
  form.longitude = point.longitude
  form.latitude = point.latitude
  if (point.suggestedName && !form.spotName.trim()) {
    form.spotName = point.suggestedName
  }
  if (!point.adcode) {
    manualRegionVisible.value = true
    ElMessage.info('已记录坐标；该位置未匹配到中国区县，请手动选择行政区')
    return
  }

  form.adcode = point.adcode
  manualRegionVisible.value = false
  try {
    const res = await searchRegions(point.districtName, 3)
    const matched = (res.data || []).find(item => item.code === point.adcode)
    regionOptions.value = matched
      ? [matched]
      : [{ code: point.adcode, fullName: point.districtName || point.adcode }]
  } catch {
    regionOptions.value = [{ code: point.adcode, fullName: point.districtName || point.adcode }]
  }
  ElMessage.success(`已自动识别：${regionOptions.value[0].fullName}`)
}

function clearMapPoint() {
  form.longitude = null
  form.latitude = null
}

async function save() {
  if (!form.adcode) return ElMessage.warning('请从搜索结果中选择区县')
  if (form.longitude == null || form.latitude == null) return ElMessage.warning('请在地图上选择地点')
  if (!form.spotName.trim()) return ElMessage.warning('请输入地点名称')
  if (!form.spotType) return ElMessage.warning('请选择地点类型')

  saving.value = true
  const payload = {
    adcode: form.adcode,
    spotName: form.spotName.trim(),
    spotType: form.spotType,
    visitTime: form.visitTime || null,
    longitude: form.longitude,
    latitude: form.latitude,
    address: form.address.trim() || null
  }
  try {
    if (form.spotId) await updateFootprint(form.spotId, payload)
    else await addFootprint(payload)
    dialogVisible.value = false
    ElMessage.success('保存成功，地图点亮数据已更新')
    await loadRows()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存失败，请检查填写内容')
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除“${row.spotName}”吗？`, '删除足迹', { type: 'warning' })
    await deleteFootprint(row.spotId)
    ElMessage.success('删除成功')
    await loadRows()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.footprint-page { width: min(1180px, 100%); margin: 0 auto; }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; gap: 24px; margin-bottom: 22px; }
h1 { margin: 0 0 6px; font-size: 24px; color: #1f2937; letter-spacing: 0; }
p { margin: 0; color: #667085; }
.toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.count { color: #667085; font-size: 14px; }
.muted { color: #98a2b3; }
.footprint-page :deep(.picker-shell) { width: 100%; }
.region-result { width: 100%; min-height: 32px; padding: 0 10px; display: flex; align-items: center; justify-content: space-between; background: #f5f7fa; border: 1px solid #e4e7ed; color: #344054; }
.field-grid { display: grid; grid-template-columns: 1fr 1fr; column-gap: 18px; }
.field-grid :deep(.el-form-item:first-child) { grid-column: 1 / -1; }
.more-fields { margin: 2px 0 0 90px; border-top: 0; }
.more-fields :deep(.el-collapse-item__header) { height: 36px; color: #667085; border-bottom: 0; }
.more-fields :deep(.el-collapse-item__wrap) { border-bottom: 0; }
.more-fields :deep(.el-form-item) { margin-bottom: 8px; }
.region-option { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; padding-bottom: 12px; }
@media (max-width: 700px) {
  .page-header { align-items: stretch; flex-direction: column; }
  .toolbar { align-items: stretch; flex-direction: column; gap: 10px; }
  .pagination-bar { justify-content: flex-start; overflow-x: auto; }
  .field-grid { grid-template-columns: 1fr; }
  .field-grid :deep(.el-form-item:first-child) { grid-column: auto; }
  .more-fields { margin-left: 0; }
}
</style>
