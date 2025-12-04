<template>
  <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
    <el-tab-pane label="手动记账" name="first">
      <div class="first_title">
        <div style="margin-left: 10px">今日时间：{{ Today }}</div>
        <div style="width: 60%;"></div>
        <div class="buttons">
          <el-button type="primary">提交</el-button>
          <el-button type="success">暂存</el-button>
          <el-button type="danger">清空</el-button>
        </div>
      </div>
      <el-table
          :data="tableData"
          stripe
          style="width: 100%;"
          :empty-text="tableData.length === 0 ? '暂无数据，点击下方「新增行」添加' : '暂无数据'"
          :scrollbar-always-on="false"
      >
        <!-- 交易日期列（可编辑） -->
        <el-table-column
            prop="date"
            label="交易日期"
            width="180"
        >
          <template #default="scope">
            <div v-if="!scope.row.isEditing">
              {{ scope.row.date || '-' }}
            </div>
            <el-input
                v-else
                v-model="scope.row.date"
                placeholder="请输入交易日期（格式：2025/09/30）"
                size="small"
                style="width: 100%;"
            />
          </template>
        </el-table-column>

        <!-- 交易金额列（可编辑） -->
        <el-table-column
            prop="amount"
            label="交易金额"
            width="120"
        >
          <template #default="scope">
            <div v-if="!scope.row.isEditing">
              {{ scope.row.amount || '-' }}
            </div>
            <el-input
                v-else
                v-model="scope.row.amount"
                placeholder="请输入金额"
                size="small"
                style="width: 100%;"
                type="number"
                step="0.01"
            />
          </template>
        </el-table-column>

        <!-- 收支类型列（下拉选择编辑） -->
        <el-table-column
            prop="type"
            label="收支类型"
            width="120"
        >
          <template #default="scope">
            <div v-if="!scope.row.isEditing">
              <el-tag :type="getTagType(scope.row.type)">{{ scope.row.type || '-' }}</el-tag>
            </div>
            <el-select
                v-else
                v-model="scope.row.type"
                placeholder="请选择类型"
                size="small"
                style="width: 100%;"
            >
              <el-option label="收入" value="收入" />
              <el-option label="支出" value="支出" />
              <el-option label="互转" value="互转" />
            </el-select>
          </template>
        </el-table-column>

        <!-- 商品名称列（可编辑） -->
        <el-table-column
            prop="goods"
            label="商品名称"
        >
          <template #default="scope">
            <div v-if="!scope.row.isEditing">
              {{ scope.row.goods || '-' }}
            </div>
            <el-input
                v-else
                v-model="scope.row.goods"
                placeholder="请输入商品名称"
                size="small"
                style="width: 100%;"
            />
          </template>
        </el-table-column>

        <!-- 操作列（编辑/保存/取消/删除） -->
        <el-table-column
            label="操作"
            width="200"
        >
          <template #default="scope">
            <el-button
                v-if="!scope.row.isEditing"
                type="text"
                icon="Edit"
                @click="editRow(scope.row)"
                size="small"
            >
              编辑
            </el-button>
            <el-button
                v-else
                type="text"
                icon="Check"
                @click="saveRow(scope.row)"
                size="small"
                style="color: #67c23a;"
            >
              保存
            </el-button>
            <el-button
                v-else
                type="text"
                icon="Close"
                @click="cancelEdit(scope.row)"
                size="small"
                style="color: #909399;"
            >
              取消
            </el-button>
            <el-button
                type="text"
                icon="Delete"
                @click="deleteRow(scope.$index)"
                size="small"
                style="color: #f56c6c;"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 表格底部操作区：新增行按钮 + 数据预览 -->
      <div class="table-footer-actions" style="margin-top: 15px; display: flex; justify-content: space-between; align-items: center;">
        <!-- 新增行按钮（表格底部左侧） -->
        <el-button
            type="primary"
            icon="Plus"
            @click="addNewRow"
        >
          新增行
        </el-button>

        <!-- 数据预览按钮（表格底部右侧） -->
        <el-button type="text" @click="consoleTableData">控制台打印数据</el-button>
      </div>
    </el-tab-pane>
    <el-tab-pane label="上传文件" name="second">上传文件</el-tab-pane>
  </el-tabs>
</template>

<script lang="ts" setup>

import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { TabsPaneContext } from 'element-plus'

const getToday = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth()+1;
  const day = today.getDay();

  return `${year}年${month}月${day}日`
}

const Today = ref(getToday());

const activeName = ref('first')

const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab, event)
}


// 1. 初始化空表格数据源（ref 响应式）
const tableData = ref([])
// 2. 缓存编辑前的行数据（用于取消编辑回滚）
const editCache = reactive({})

// 3. 新增空行
const addNewRow = () => {
  const newRow = {
    date: '',
    amount: '',
    type: '',
    goods: '',
    isEditing: false // 编辑状态标识
  }
  tableData.value.push(newRow)
}

// 4. 编辑行：进入编辑状态，缓存原始数据
const editRow = (row) => {
  // 缓存编辑前的数据（利用 row 的 _uid 作为唯一标识）
  editCache[row._uid] = { ...row }
  row.isEditing = true
}

// 5. 保存编辑：退出编辑状态，清空缓存
const saveRow = (row) => {
  // 简单校验：必填字段不能为空
  if (!row.date || !row.amount || !row.type || !row.goods) {
    ElMessage.warning('交易日期、金额、收支类型、商品名称不能为空！')
    return
  }
  row.isEditing = false
  delete editCache[row._uid]
  ElMessage.success('保存成功！')
}

// 6. 取消编辑：回滚到原始数据，退出编辑状态
const cancelEdit = (row) => {
  Object.assign(row, editCache[row._uid])
  row.isEditing = false
  delete editCache[row._uid]
}

// 7. 删除行
const deleteRow = (index) => {
  tableData.value.splice(index, 1)
  ElMessage.info('行已删除')
}

// 8. 收支类型标签颜色映射
const getTagType = (type) => {
  const colorMap = {
    '收入': 'success',
    '支出': 'danger',
    '互转': 'info'
  }
  return colorMap[type] || 'default'
}

// 9. 控制台打印当前表格数据（方便调试）
const consoleTableData = () => {
  console.log('当前表格数据：', tableData.value)
}
</script>

<style scoped>
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

.first_title {
  display: flex;
  flex-direction: row;
  align-items: center;

  width: 100%;
  height: 40px;
  margin-bottom: 10px;
}

</style>