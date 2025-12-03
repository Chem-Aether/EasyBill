<template>
  <div class="custom-style">
    <el-segmented v-model="value" :options="options" />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const value = ref('全部')

const options = ['全部', '支出', '收入', '互转']
</script>

<style scoped>
.custom-style .el-segmented {
  --el-segmented-item-selected-color: var(--el-text-color-primary);
  --el-segmented-item-selected-bg-color: #ffd100;
  --el-border-radius-base: 16px;
}
</style>