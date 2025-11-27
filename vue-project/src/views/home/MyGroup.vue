<template>
  <div class="root">
    <!-- 头部信息与控制器 -->
    <div class="header">
      <div class="current-date">
        <span v-if="currentView === 'year'">{{ currentYear }} 年</span>
        <span v-else-if="currentView === 'month'">{{ currentYear }} 年 {{ currentMonth + 1 }} 月</span>
        <span v-else>{{ currentYear }} 年 {{ currentMonth + 1 }} 月 {{ currentDay }} 日</span>
      </div>

      <div class="controls">
        <button class="nav-btn prev" @click="handlePrev">
          <i class="arrow left"></i>
        </button>

        <div class="view-tabs">
          <button
              class="tab"
              :class="{ active: currentView === 'year' }"
              @click="switchToYearView"
          >
            年
          </button>
          <button
              class="tab"
              :class="{ active: currentView === 'month' }"
              @click="switchToMonthView"
          >
            月
          </button>
          <button
              class="tab"
              :class="{ active: currentView === 'week' }"
              @click="switchToWeekView"
          >
            周
          </button>
        </div>

        <button class="nav-btn next" @click="handleNext">
          <i class="arrow right"></i>
        </button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content" style="background-color: #42b983">
      <!-- 星期标题 (月视图和周视图显示) -->
      <div class="weekdays" v-if="currentView !== 'year'">
        <div class="weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
      </div>
      <!-- 年视图 -->
      <div class="year-view" v-if="currentView === 'year'">
        <div class="month-item"
             v-for="(month, index) in 12"
             :key="index"
             :class="{
               current: isCurrentMonth(index),
               selected: isSelectedMonth(index)
             }"
             @click="selectMonth(index)">
          {{ index + 1 }}月
        </div>
      </div>

      <!-- 月视图 -->
      <div class="month-view" v-if="currentView === 'month'" >
        <div class="day-item"
             v-for="day in days"
             :key="day.timestamp"
             :class="{
               'other-month': day.isOtherMonth,
               current: day.isCurrentDay,
               selected: day.isSelected
             }"
             @click="selectDay(day)" style="background-color: #3b82f6">
          <span v-if="!day.isOtherMonth">{{ day.date.getDate() }}</span>
        </div>
      </div>

      <!-- 周视图 -->
      <div class="week-view" v-if="currentView === 'week'">
        <div class="date-navigation">
          <div
              class="date-box"
              v-for="dateInfo in weekDates"
              :key="dateInfo.timestamp"
              :class="{
              'selected': isSelectedDate(dateInfo.date),
              'current-day': isCurrentDay(dateInfo.date),
              'other-month': dateInfo.isOtherMonth
            }"
              @click="selectDate(dateInfo.date)"
          >
            <div class="date-number">{{ dateInfo.date.getDate() }}</div>
          </div>
        </div>

        <!-- 消费记录区域 -->
        <div class="records-area">
          <div class="records-container">
            <div class="record-item" v-for="(record, index) in filteredRecords" :key="index">
              <div class="record-time">{{ record.time }}</div>
              <div class="record-title">{{ record.title }}</div>
              <div class="record-amount" :class="{ income: record.type === 'income' }">
                {{ record.type === 'income' ? '+' : '-' }}{{ record.amount }}元
              </div>
            </div>

            <div class="no-records" v-if="filteredRecords.length === 0">
              当天暂无记录
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// 接收父组件传入的消费记录
const props = defineProps({
  records: {
    type: Array,
    default: () => []
  }
});

// 状态管理
const currentDate = ref(new Date()); // 用于导航和显示的当前日期
const selectedDate = ref(new Date()); // 用户选中的日期
const currentView = ref('week'); // 默认显示周视图

// 星期数据
const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
const weekdaysShort = ['日', '一', '二', '三', '四', '五', '六'];

// 计算属性
const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => currentDate.value.getMonth());
const currentDay = computed(() => currentDate.value.getDate());

// --- 年视图和月视图数据 ---
const days = computed(() => {
  if (currentView.value !== 'month') return [];

  const daysArray = [];
  const firstDay = new Date(currentYear.value, currentMonth.value, 1);
  const lastDay = new Date(currentYear.value, currentMonth.value + 1, 0);

  const firstDayOfWeek = firstDay.getDay();

  // 添加上月的空白格子
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    const date = new Date(firstDay);
    date.setDate(firstDay.getDate() - (i + 1));
    daysArray.push(createDayObject(date, true));
  }

  // 添加当月的日期格子
  for (let i = 1; i <= lastDay.getDate(); i++) {
    const date = new Date(currentYear.value, currentMonth.value, i);
    daysArray.push(createDayObject(date, false));
  }

  // 添加下月的空白格子，确保总共有6行 (42格)
  const remaining = 42 - daysArray.length;
  for (let i = 1; i <= remaining; i++) {
    const date = new Date(lastDay);
    date.setDate(lastDay.getDate() + i);
    daysArray.push(createDayObject(date, true));
  }

  return daysArray;
});

// --- 周视图数据 ---
const weekDates = computed(() => {
  if (currentView.value !== 'week') return [];

  const dates = [];
  const startOfWeek = new Date(selectedDate.value);
  // 计算本周一的日期 (getDay() 返回 0-6, 0 是周日)
  startOfWeek.setDate(selectedDate.value.getDate() - (selectedDate.value.getDay() || 7) + 1);

  for (let i = 0; i < 7; i++) {
    const date = new Date(startOfWeek);
    date.setDate(startOfWeek.getDate() + i);
    dates.push({
      date: date,
      timestamp: date.getTime(),
      isOtherMonth: date.getMonth() !== currentMonth.value
    });
  }
  return dates;
});

// --- 消费记录过滤 ---
const filteredRecords = computed(() => {
  const dateStr = formatDate(selectedDate.value);
  return props.records.filter(record => record.date === dateStr);
});

// --- 辅助函数 ---
function createDayObject(date, isOtherMonth) {
  const isCurrentDay =
      date.getFullYear() === new Date().getFullYear() &&
      date.getMonth() === new Date().getMonth() &&
      date.getDate() === new Date().getDate();

  const isSelected =
      date.getFullYear() === selectedDate.value.getFullYear() &&
      date.getMonth() === selectedDate.value.getMonth() &&
      date.getDate() === selectedDate.value.getDate();

  return {
    date,
    timestamp: date.getTime(),
    isOtherMonth,
    isCurrentDay,
    isSelected
  };
}

function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

// --- 视图切换函数 ---
function switchToYearView() {
  currentView.value = 'year';
  // 切换到年视图时，更新 currentDate 到选中日期的年初
  currentDate.value = new Date(selectedDate.value.getFullYear(), 0, 1);
}

function switchToMonthView() {
  currentView.value = 'month';
  // 切换到月视图时，更新 currentDate 到选中日期的月初
  currentDate.value = new Date(selectedDate.value.getFullYear(), selectedDate.value.getMonth(), 1);
}

function switchToWeekView() {
  currentView.value = 'week';
  // 切换到周视图时，currentDate 保持为选中日期
  currentDate.value = new Date(selectedDate.value);
}

// --- 导航和选择函数 ---
const handlePrev = () => {
  const newDate = new Date(currentDate.value);
  if (currentView.value === 'year') {
    // 年视图：切换到上一年
    newDate.setFullYear(newDate.getFullYear() - 1);
  } else if (currentView.value === 'month') {
    // 月视图：切换到上一个月
    newDate.setMonth(newDate.getMonth() - 1);
  } else { // week view, navigate by day
    // 周视图：切换到上一天
    newDate.setDate(newDate.getDate() - 1);
    selectedDate.value = newDate;
  }
  currentDate.value = newDate;
};

const handleNext = () => {
  const newDate = new Date(currentDate.value);
  if (currentView.value === 'year') {
    // 年视图：切换到下一年
    newDate.setFullYear(newDate.getFullYear() + 1);
  } else if (currentView.value === 'month') {
    // 月视图：切换到下一个月
    newDate.setMonth(newDate.getMonth() + 1);
  } else { // week view, navigate by day
    // 周视图：切换到下一天
    newDate.setDate(newDate.getDate() + 1);
    selectedDate.value = newDate;
  }
  currentDate.value = newDate;
};

const selectMonth = (monthIndex) => {
  const newDate = new Date(currentYear.value, monthIndex, 1);
  selectedDate.value = newDate;
  switchToMonthView();
};

const selectDay = (day) => {
  if (day.isOtherMonth) return;
  selectedDate.value = new Date(day.date);
  switchToWeekView();
};

const selectDate = (date) => {
  selectedDate.value = new Date(date);
  currentDate.value = new Date(date);
};

// --- 辅助判断 ---
const isCurrentMonth = (monthIndex) => {
  const today = new Date();
  return today.getFullYear() === currentYear.value && today.getMonth() === monthIndex;
};

const isSelectedMonth = (monthIndex) => {
  return selectedDate.value.getFullYear() === currentYear.value &&
      selectedDate.value.getMonth() === monthIndex;
};

const isCurrentDay = (date) => {
  const today = new Date();
  return (
      date.getFullYear() === today.getFullYear() &&
      date.getMonth() === today.getMonth() &&
      date.getDate() === today.getDate()
  );
};

const isSelectedDate = (date) => {
  return (
      date.getFullYear() === selectedDate.value.getFullYear() &&
      date.getMonth() === selectedDate.value.getMonth() &&
      date.getDate() === selectedDate.value.getDate()
  );
};

// 初始化
onMounted(() => {
  const now = new Date();
  currentDate.value = now;
  selectedDate.value = now;
});
</script>

<style scoped>
.root {
  width: 100%;
  height: 100%;
  padding: 10px;
}



.header {
  padding: 14px 20px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-date {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #ffffff;
  border: 1px solid #ddd;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.nav-btn:hover {
  background-color: #f5f5f5;
}

.arrow {
  width: 0;
  height: 0;
  border-style: solid;
}

.arrow.left {
  border-width: 5px 7px 5px 0;
  border-color: transparent #666 transparent transparent;
}

.arrow.right {
  border-width: 5px 0 5px 7px;
  border-color: transparent transparent transparent #666;
}

.view-tabs {
  display: flex;
  border-radius: 4px;
  background-color: #ffffff;
  border: 1px solid #eee;
  overflow: hidden;
}

.tab {
  padding: 6px 14px;
  background: none;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab.active {
  background-color: #e6f7e9;
  color: #008000;
  font-weight: 500;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  padding: 10px 20px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.weekday {
  text-align: center;
  font-size: 13px;
  color: #666;
  padding: 4px 0;
}

.content {
  height: 100%;
  flex-grow: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* --- 年视图样式 --- */
.year-view {
  height: 100%;
  background-color: #3b82f6;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding: 20px;
  overflow: auto;
}

.month-item {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  border-radius: 6px;
  height: 60px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #eee;
}

.month-item:hover {
  border-color: #ccc;
  background-color: #f9f9f9;
}

.month-item.current {
  border-color: #42b983;
  background-color: #f0fdf4;
  color: #10b981;
}

.month-item.selected {
  border-color: #3b82f6;
  background-color: #eff6ff;
  color: #3b82f6;
  font-weight: 500;
}

/* --- 月视图样式 --- */
/* 月视图容器样式 */
.content {
  /* 让容器占满父元素剩余空间 */
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止内容超出 */
}

/* 日历网格容器 */
.month-view {
  display: grid;
  grid-template-columns: repeat(7, 1fr); /* 7列等宽 */
  grid-template-rows: repeat(6, 1fr); /* 6行等高 */
  gap: 2px; /* 缩小格子间距，避免挤压 */
  padding: 5px;
  flex-grow: 1; /* 占满可用空间 */
  box-sizing: border-box; /* 确保padding不增加总尺寸 */
}

/* 日期格子样式 */
.day-item {
  background-color: #4285f4;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  min-height: 24px; /* 最小高度，确保内容可见 */
  box-sizing: border-box; /* 边框计入尺寸 */
  overflow: hidden; /* 防止内容溢出 */
}

/* 当前选中日期样式 */
.day-item.selected {
  background-color: #0f9d58;
  border: 2px solid #0f9d58;
}

/* 其他月份日期样式 */
.day-item.other-month {
  background-color: #b3d1ff;
  opacity: 0.7;
}

/* --- 周视图样式 --- */
.week-view {
  background-color: #ef4444;

  flex-grow: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.date-navigation {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.date-box {
  background-color: #fff;
  border-radius: 8px;
  padding: 10px 5px;
  text-align: center;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  aspect-ratio: 0.8 / 0.2 ;
}

.date-box:hover:not(.other-month) {
  border-color: #ccc;
  background-color: #f9f9f9;
}

.date-box.selected {
  border-color: #10b981;
  background-color: #f0fdf4;
  color: #10b981;
}

.date-box.current-day {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.date-box.other-month {
  background-color: #f9f9f9;
  border-color: #f1f1f1;
  color: #aaa;
  cursor: default;
}

.date-number {
  font-size: 18px;
  font-weight: 500;
  line-height: 1;
}

.weekday-short {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.records-area {
  flex-grow: 1;
  padding: 20px;
  overflow-y: auto;
}

.selected-date-info {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.records-container {
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f1f1f1;
}

.record-time {
  font-size: 13px;
  color: #666;
  min-width: 60px;
}

.record-title {
  font-size: 15px;
  color: #333;
  flex-grow: 1;
  padding: 0 20px;
}

.record-amount {
  font-size: 14px;
  font-weight: 500;
  color: #ef4444;
  min-width: 80px;
  text-align: right;
}

.record-amount.income {
  color: #10b981;
}

.no-records {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 14px;
  flex-direction: column;
  padding: 40px 0;
}
</style>