<template>
  <div style="padding: 20px; width: 600px;">
    <!-- 点击空白处的容器：确保点击空白区域能触发失焦 -->
    <div @click="handleBlankClick" style="min-height: 400px;">
      <el-table
          :data="tableData"
          border
          row-key="name"
          style="width: 100%; margin-bottom: 20px;"
      >
        <el-table-column
            prop="name"
            label="收支类型"
            width="150"
        />
        <el-table-column
            label="金额（双击可编辑）"
            width="200"
        >
          <template #default="scope">
            <div
                @dblclick.stop="enterEdit(scope.row)"
            style="cursor: pointer; padding: 2px;"
            >
            <!-- 展示模式 -->
            <span v-if="!scope.row.isEditing">
                {{ formatThousands(scope.row.value) }}
              </span>
            <!-- 编辑模式：el-input -->
            <el-input
                v-else
                :ref="`editInput-${scope.row.name}`"
                :value="formatThousands(scope.row.value)"
                @input="handleInput(scope.row, $event)"
                @blur="exitEdit(scope.row)"
                @keyup.enter="exitEdit(scope.row)"
                @click.stop
            size="small"
            style="width: 120px;"
            />
    </div>
</template>
</el-table-column>
<el-table-column label="操作" width="100">
<template #default="scope">
  <el-button
      size="small"
      type="text"
      @click="resetValue(scope.row)"
  >
    重置
  </el-button>
</template>
</el-table-column>
</el-table>

<!-- 测试空白处：点击这里也能退出编辑 -->
<div style="height: 200px; background: #f5f5f5; margin-top: 20px;">
点击这个空白区域，编辑中的单元格会退出编辑
</div>
</div>
</div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

// 表格数据（含编辑状态、原始值）
const tableData = reactive([
  { name: '收入', value: 1048, isEditing: false, originValue: 1048 },
  { name: '支出', value: 2000, isEditing: false, originValue: 2000 },
  { name: '互转', value: 0, isEditing: false, originValue: 0 }
]);

// 千分位格式化
const formatThousands = (num) => {
  if (!num || isNaN(Number(num))) return "0";
  const [integerPart, decimalPart] = Number(num).toString().split(".");
  const formattedInteger = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  return decimalPart ? `${formattedInteger}.${decimalPart}` : formattedInteger;
};

// 解析千分位为数字
const parseThousands = (str) => {
  if (!str) return 0;
  return Number(str.replace(/,/g, ""));
};

// 双击进入编辑
const enterEdit = (row) => {
  // 关闭所有行的编辑状态
  tableData.forEach(item => item.isEditing = false);
  row.isEditing = true;

  // 延迟聚焦输入框
  setTimeout(() => {
    const inputRef = `editInput-${row.name}`;
    const input = ref(inputRef).value;
    if (input) {
      input.focus();
      input.select();
    }
  }, 0);
};

// 实时输入处理
const handleInput = (row, val) => {
  row.value = parseThousands(val);
};

// 退出编辑（失焦/回车）
const exitEdit = (row) => {
  if (isNaN(row.value)) {
    row.value = row.originValue;
    ElMessage.warning('输入无效，已重置为原始值');
  }
  row.isEditing = false;
};

// 重置值
const resetValue = (row) => {
  row.value = row.originValue;
  row.isEditing = false;
};

// 点击空白处：强制关闭所有编辑状态（兜底逻辑）
const handleBlankClick = () => {
  tableData.forEach(item => item.isEditing = false);
};
</script>