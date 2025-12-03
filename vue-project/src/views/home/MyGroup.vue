<template>
  <div class="root">
    <el-calendar ref="calendar" style="width: 1000px">
      <template #header="{ date }">
        <span>{{ date }}</span>
        <el-button-group>
          <el-button  @click="selectDate('prev-year')">
            Previous Year
          </el-button>
          <el-button  @click="selectDate('prev-month')">
            Previous Month
          </el-button>
          <el-button  @click="selectDate('today')">Today</el-button>
          <el-button @click="selectDate('next-month')">
            Next Month
          </el-button>
          <el-button  @click="selectDate('next-year')">
            Next Year
          </el-button>
        </el-button-group>
      </template>
      <template #date-cell="{ data }">
        <div :class="data.isSelected ? 'is-selected' : ''">
          {{ data.day.split('-').slice(2).join('-') }}
          <div style="margin-top: 5px;text-align: right;color: red">-123</div>
          <div style="margin-top: 5px;text-align: right;color: green">+2,569</div>
        </div>
      </template>
    </el-calendar>
  </div>
</template>



<style scoped>


</style>