<template>
  <el-autocomplete
      v-model="inputValue"
      :fetch-suggestions="querySearch"
      :placeholder="placeholder"
      :disabled="disabled"
      @select="handleSelect"
      @blur="handleBlur"
      style="width: 100%"
  />
</template>

<script setup>
import { ref, watch } from 'vue'

// 接收外部参数
const props = defineProps({
  // 预设选项列表
  options: {
    type: Array,
    required: true
  },
  // 默认值
  modelValue: String,
  placeholder: String,
  disabled: Boolean
})

const emit = defineEmits(['update:modelValue'])

// 内部输入值
const inputValue = ref('')

// 监听外部值变化
watch(
    () => props.modelValue,
    (val) => {
      inputValue.value = val || ''
    },
    { immediate: true }
)

// 搜索过滤
const querySearch = (queryString, cb) => {
  if (!queryString || !queryString.trim()) {
    cb([])
    return
  }
  const result = props.options
      .filter(item => item.includes(queryString.trim()))
      .map(item => ({ value: item }))
  cb(result)
}

// 选中
const handleSelect = (item) => {
  inputValue.value = item.value
  emit('update:modelValue', item.value)
}

// 失焦校验（只能选预设值）
const handleBlur = () => {
  const val = inputValue.value?.trim()
  if (!val) {
    emit('update:modelValue', '')
    return
  }
  // 不在列表 → 清空
  if (!props.options.includes(val)) {
    inputValue.value = ''
    emit('update:modelValue', '')
    ElMessage.warning('只能选择预设选项')
  }
}
</script>