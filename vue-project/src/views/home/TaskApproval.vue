<template>
<div class="root">
  <div class="block" style="width: 100%;display: flex;">
    <el-button size="large" type="primary" >查询</el-button>
    <div style="width: 200px"></div>
    <el-input size="large" v-model="input" style="width: 400px;" placeholder="输入关键字" />
  </div>
  <el-collapse :expand-icon-position="left">
    <el-collapse-item title="更多筛选" name="1">
      <div class="box-row">
        <div class="titlebox">时间范围</div>
        <div class="box-choice">
          <el-date-picker
              v-model="value1"
              type="daterange"
              range-separator="To"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
          />
        </div>
      </div>
      <div class="box-row">
        <div class="titlebox">动账类型</div>
        <div class="box-choice">
          <el-segmented v-model="value4" :options="type_options" />
        </div>
      </div>
      <div class="box-row">
        <div class="titlebox">类别</div>
        <div class="box-choice">
          <el-cascader
              :options="category_options"
              :props="props"
              :show-all-levels="false"
              collapse-tags
              collapse-tags-tooltip
              :max-collapse-tags="1"
              clearable
          />
        </div>
      </div>
      <div class="box-row">
        <div class="titlebox">金额范围</div>
        <div class="box-choice">
          <el-slider v-model="value" range show-stops :max="10000" />
        </div>
      </div>
    </el-collapse-item>
  </el-collapse>
  <div class="Table">
    <el-table :data="tableData" stripe style="width: 100%;height: 90%;margin-bottom: 10px">
      <el-table-column prop="date" label="Date" width="180" />
      <el-table-column prop="name" label="Name" width="180" />
      <el-table-column prop="address" label="Address" />
    </el-table>
    <el-pagination
        v-model:current-page="currentPage2"
        v-model:page-size="pageSize2"
        :page-sizes="[100, 200, 300, 400]"
        :size="size"
        :disabled="disabled"
        :background="background"
        layout="sizes, prev, pager, next"
        :total="1000"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />
  </div>
</div>
</template>

<script setup>
import { ref } from 'vue'

const input = ref('')
const value1 = ref('')

const value4 = ref('全部')

const type_options = ['全部', '支出', '收入', '互转']

const props = {
  expandTrigger: 'hover',
  multiple: true,
}

const category_options = [
  {
    value: 1,
    label: 'Asia',
    children: [
      {
        value: 2,
        label: 'China',
        children: [
          { value: 3, label: 'Beijing' },
          { value: 4, label: 'Shanghai' },
          { value: 5, label: 'Hangzhou' },
        ],
      },
      {
        value: 6,
        label: 'Japan',
        children: [
          { value: 7, label: 'Tokyo' },
          { value: 8, label: 'Osaka' },
          { value: 9, label: 'Kyoto' },
        ],
      },
      {
        value: 10,
        label: 'Korea',
        children: [
          { value: 11, label: 'Seoul' },
          { value: 12, label: 'Busan' },
          { value: 13, label: 'Taegu' },
        ],
      },
    ],
  },
  {
    value: 14,
    label: 'Europe',
    children: [
      {
        value: 15,
        label: 'France',
        children: [
          { value: 16, label: 'Paris' },
          { value: 17, label: 'Marseille' },
          { value: 18, label: 'Lyon' },
        ],
      },
      {
        value: 19,
        label: 'UK',
        children: [
          { value: 20, label: 'London' },
          { value: 21, label: 'Birmingham' },
          { value: 22, label: 'Manchester' },
        ],
      },
    ],
  },
  {
    value: 23,
    label: 'North America',
    children: [
      {
        value: 24,
        label: 'US',
        children: [
          { value: 25, label: 'New York' },
          { value: 26, label: 'Los Angeles' },
          { value: 27, label: 'Washington' },
        ],
      },
      {
        value: 28,
        label: 'Canada',
        children: [
          { value: 29, label: 'Toronto' },
          { value: 30, label: 'Montreal' },
          { value: 31, label: 'Ottawa' },
        ],
      },
    ],
  },
]

const tableData = [
  {
    date: '2016-05-03',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    date: '2016-05-02',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    date: '2016-05-01',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },  {
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    date: '2016-05-01',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },  {
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    date: '2016-05-01',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },  {
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    date: '2016-05-01',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },  {
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },{
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },{
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles',
  },{
    date: '2016-05-04',
    name: 'Tom',
    address: '最后一行检测',
  },

]

const value3 = ref([4, 8])

const background = ref(false)
const disabled = ref(false)

const handleSizeChange = () => {
  console.log(`${val} items per page`)
}
const handleCurrentChange = () => {
  console.log(`current page: ${val}`)
}
</script>

<style scoped>
.root {
  display: flex;
  flex-direction: column;

  width: 100%;

  overflow: auto;
  box-sizing: border-box;
}

.root > * {
  padding-bottom: 10px;
}

.box-choice .el-segmented {
  --el-segmented-item-selected-color: var(--el-text-color-primary);
  --el-segmented-item-selected-bg-color: #ffd100;
  --el-border-radius-base: 16px;
}

.titlebox{
  width: 60px;
  text-align: justify;
  text-align-last: justify;
  margin-right: 20px;
}

.box-row{
  display: flex;
}

.box-row > * {
  padding-bottom: 10px;
}

.box-choice {
  width: 500px;
}

.Table {
  height: 80%;
}

</style>