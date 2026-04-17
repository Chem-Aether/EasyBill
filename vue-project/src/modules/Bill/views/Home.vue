<template>
  <div class="root">
    <el-row :gutter="10" style="margin-bottom: 10px;">
      <el-col :span="13">
        <div class="box" style="display: flex;justify-content: space-between">
          <div class="text" >
            <div class="DataText">{{NowMonth}}</div>
            <div class="TotalMoney">¥ {{ expenseItem }}</div>
            <div style="color: #999999">总收入：{{ incomeItem }}</div>
            <div><el-button color="#626aef">查看详情</el-button></div>
          </div>
          <div class="month-total-pie" ref="MonthPieDom"></div>
        </div>
      </el-col>
      <el-col :span="11">
        <div class="box">
          <div style="display: flex; justify-content: space-between; align-items: center;margin-right: 60px">
            <span >资产概要</span>
            <span style="color: #999999">当前记录了3个账户</span>
          </div>
          <div class="accounts"  >
            <div class="one-account" v-for="(item,index) in accountList">
              <template v-if="index === 0">
                <svg t="1764230335138" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4794">
                  <path d="M522.24 104.448q2.048 0 5.12-2.048 6.144-4.096 16.384-14.336 12.288-11.264 24.576-16.384t32.768 3.072q10.24 3.072 16.384 8.704t11.776 10.752 12.288 8.704 16.896 3.584 17.408-3.072 13.824-5.632 13.312-3.584 15.872 2.048q10.24 4.096 13.824 11.776t2.048 15.872-7.168 16.384-13.824 13.312q-19.456 12.288-29.696 25.088t-20.48 27.136q-5.12 7.168-11.264 16.384t-11.264 18.432q-6.144 11.264-13.312 21.504l-211.968 0q-7.168-12.288-14.336-23.552-6.144-10.24-13.312-20.48t-12.288-18.432q-10.24-15.36-23.04-27.136t-25.088-20.992q-13.312-9.216-18.432-25.088t9.216-27.136q7.168-5.12 16.384-4.096t19.456 4.096 19.968 6.144 17.92 1.024q14.336-3.072 21.504-10.752t24.576-18.944q13.312-8.192 29.696-5.632t24.576 10.752q4.096 5.12 8.704 8.704t9.728 6.656q5.12 4.096 11.264 7.168zM637.952 309.248q12.288 16.384 30.72 35.84 15.36 17.408 37.888 39.424t52.224 46.592q24.576 21.504 50.688 50.688t45.568 68.096 27.136 89.6-2.56 113.152q-9.216 51.2-36.352 96.768t-72.192 79.36-107.008 53.76-140.8 19.968q-80.896 0-143.36-23.04t-105.984-59.904-69.12-82.432-33.792-90.624q-9.216-50.176-2.56-96.256t24.064-86.528 43.52-74.24 56.832-60.416q26.624-23.552 48.128-44.544t35.84-38.4q17.408-19.456 30.72-36.864l230.4 0zM529.408 623.616l-27.648-27.648-21.504-21.504q-10.24-10.24-14.336-15.36-8.192-7.168-21.504-11.776t-22.528 5.632q-8.192 11.264-4.096 22.016t9.216 16.896l15.36 14.336q11.264 12.288 24.576 26.624 15.36 16.384 33.792 34.816l-1.024 21.504-69.632 0q-9.216 0-15.872 7.168t-6.656 15.36 6.144 14.336 16.384 6.144l69.632 0 0 35.84-69.632 0q-9.216 0-15.872 5.632t-6.656 14.848q-1.024 8.192 5.632 13.824t16.896 5.632l69.632 0 0 20.48q1.024 14.336 9.216 19.968t20.48 5.632q12.288 1.024 19.968-7.68t7.68-17.92l0-20.48 71.68 0q10.24 0 16.896-4.608t5.632-14.848q0-21.504-28.672-21.504l-65.536 0 1.024-34.816 66.56-1.024q14.336 1.024 20.48-5.632t6.144-15.872q0-8.192-6.656-14.848t-20.992-6.656l-66.56 0 0-19.456q17.408-16.384 31.744-31.744 12.288-12.288 24.576-25.088t18.432-17.92q23.552-20.48 10.24-35.84-7.168-9.216-18.432-9.216t-20.48 8.192q-5.12 4.096-17.408 14.848t-24.576 22.016q-14.336 13.312-31.744 29.696z" p-id="4795"></path>
                </svg>
              </template>
              <template v-else>
                <svg t="1764231139968" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5877">
                  <path d="M832.36132813 344.80247107H210.09570287l-3.95507812-14.50195286c-9.22851588-31.640625 6.59179662-65.25878906 36.25488332-78.44238307L639.88085963 75.19797915C655.70117213 67.94700233 674.15820313 67.28782289 690.63769505 73.87961952s29.66308594 18.457031 36.91406276 34.93652368L832.36132813 344.80247107z m-588.6474612-39.55078125h527.34375026L691.29687525 124.63645571c-2.6367185-6.59179662-7.91015625-11.20605494-14.50195363-13.84277345-6.59179662-2.6367185-13.84277344-2.6367185-20.43457006 0.65917945L258.875 288.1130177c-7.91015625 3.29589869-12.52441381 9.88769531-15.16113307 17.13867212z"  p-id="5878"></path><path d="M832.36132813 344.80247107H136.26757812v-32.95898464c0-61.96289037 50.097656-112.06054713 112.06054713-112.06054637h111.40136694L639.88085963 75.19797915C655.70117213 67.94700233 674.15820313 67.28782289 690.63769505 73.87961952s29.66308594 18.457031 36.91406276 34.93652368L832.36132813 344.80247107zM175.81835937 305.25168982h595.23925782L691.29687525 124.63645571c-2.6367185-6.59179662-7.91015625-11.20605494-14.50195363-13.84277345-6.59179662-2.6367185-13.84277344-2.6367185-20.43457006 0.65917945L368.29882787 239.33372131H248.32812525c-37.57324219 0-68.55468775 29.0039065-72.50976588 65.91796851z"  p-id="5879"></path><path d="M834.99804662 858.96262732H189.00195338c-29.0039065 0-52.73437525-23.73046875-52.73437525-52.73437526V305.25168982h698.73046849c29.0039065 0 52.73437525 23.73046875 52.73437526 52.73437526v448.24218698c0 29.0039065-23.73046875 52.73437525-52.73437526 52.73437526zM175.81835937 344.80247107v461.42578099c0 7.25097682 5.93261719 13.18359399 13.18359401 13.18359402h645.99609324c7.25097682 0 13.18359399-5.93261719 13.18359401-13.18359402V357.98606508c0-7.25097682-5.93261719-13.18359399-13.18359401-13.18359401H175.81835937z"  p-id="5880"></path><path d="M719.64160156 680.98411171c-30.98144557 0-56.03027318-25.04882838-56.03027318-56.03027319v-79.1015625c0-30.98144557 25.04882838-56.03027318 56.03027318-56.03027395s56.03027318 25.04882838 56.03027319 56.03027395v79.1015625c0 30.98144557-25.04882838 56.03027318-56.03027319 56.03027317z m0-151.61132839c-9.22851588 0-16.47949193 7.25097682-16.47949193 16.4794927v79.1015625c0 9.22851588 7.25097682 16.47949193 16.47949193 16.47949193s16.47949193-7.25097682 16.47949194-16.47949193v-79.1015625c0-9.22851588-7.25097682-16.47949193-16.47949194-16.4794927zM347.47534154 307.70383825l369.36474609-157.28027317 15.49072318 36.38671875-369.35815456 157.28027317z"  p-id="5881"></path>
                </svg>
              </template>
              <div>
                <div style="font-size: 15px;margin-bottom: 8px">{{ item.type }}</div>
                <div style="font-size: 18px;font-weight: bold;">{{ item.value }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="10" >
      <el-col :span="12">
        <el-row :gutter="10" style="height: 50%; margin-bottom: 10px;">
          <el-col :span="12">
            <div class="box" ref="OutPieDom"></div>
          </el-col>
          <el-col :span="12">
            <div class="box" ref="InPieDom"></div>
          </el-col>
        </el-row>

        <el-row :gutter="10">
          <el-col :span="24">
            <div class="box" style="height: 220px; overflow: hidden">
              <el-table :data="TableData" stripe height="200" style="width: 100%">
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="amount" label="金额" width="100" />
                <el-table-column prop="name" label="商品" />
              </el-table>
            </div>
          </el-col>
        </el-row>
      </el-col>

      <el-col :span="12">
        <div ref="BarchartDom" class="box tall-box"></div>
      </el-col>
    </el-row>
  </div>
</template>


<script setup>
import { ref,onMounted, onUnmounted} from 'vue';
import * as echarts from 'echarts'

// 格式化时间
const formatYearMonth = (date = new Date()) => {
  const year = date.getFullYear();
  const monthNames = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'];
  const month = monthNames[date.getMonth()];
  return `${year}年${month}`;
};

const NowMonth = ref(formatYearMonth());
console.log(NowMonth.value);

// 本月消费汇总
const Total  = ref(0);
Total.value = [
  { value: 1048, name: '收入' },
  { value: 999.08, name: '支出' },
  { value: 0, name: '互转' },
]

// 千分位转换
const formatThousands = (num) => {
  // 处理空值、非数字，兜底返回 "0"
  if (!num || isNaN(Number(num))) return "0";

  // 转为数字并拆分为整数和小数部分
  const [integerPart, decimalPart] = Number(num).toString().split(".");

  // 整数部分添加千分位逗号（核心正则）
  const formattedInteger = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ",");

  // 有小数部分则拼接，无则返回整数部分
  return decimalPart ? `${formattedInteger}.${decimalPart}` : formattedInteger;
};


const expenseItem = ref(
    formatThousands(Total.value.find(item => item.name === '支出').value)
);

const incomeItem = ref(
    formatThousands(2619.98)
);


// 资产
const accountList = ref(null)
accountList.value = [
  {'type':'总资产',"value":"12,309"},
  {'type':'微信钱包',"value":"12,309"},
  {'type':'支付宝',"value":"12,309"},
  {'type':'中国银行(6666)',"value":"12,309"},
  {'type':'交通银行(8888)',"value":"12,309"},
]

const BarchartDom = ref(null)
let BarChart = null

const OutPieDom = ref(null)
let OutPieChart = null

const InPieDom = ref(null)
let InPieChart = null

const TableData = [
  { "date": "2025-11-01", "amount": 245.00, "name": "餐饮美食 - 湘菜馆" },
  { "date": "2025-11-02", "amount": 1299.00, "name": "购物消费 - 电子产品专卖店" },
  { "date": "2025-11-03", "amount": 186.50, "name": "日常用品 - 超市" },
  { "date": "2025-11-05", "amount": 58.00, "name": "交通出行 - 出租车" },
  { "date": "2025-11-07", "amount": 318.00, "name": "餐饮美食 - 火锅城" },
  { "date": "2025-11-09", "amount": 899.00, "name": "购物消费 - 服装店" },
  { "date": "2025-11-10", "amount": 98.00, "name": "娱乐休闲 - 电影院" },
  { "date": "2025-11-12", "amount": 42.00, "name": "餐饮美食 - 咖啡馆" },
  { "date": "2025-11-14", "amount": 528.00, "name": "购物消费 - 美妆店" },
  { "date": "2025-11-15", "amount": 35.80, "name": "日常用品 - 便利店" },
  { "date": "2025-11-17", "amount": 12.00, "name": "交通出行 - 地铁" },
  { "date": "2025-11-18", "amount": 456.00, "name": "餐饮美食 - 日料店" },
  { "date": "2025-11-20", "amount": 178.00, "name": "学习教育 - 书店" },
  { "date": "2025-11-21", "amount": 210.00, "name": "娱乐休闲 - 酒吧" },
  { "date": "2025-11-23", "amount": 688.00, "name": "购物消费 - 家居生活馆" },
  { "date": "2025-11-24", "amount": 38.00, "name": "餐饮美食 - 快餐连锁店" },
  { "date": "2025-11-25", "amount": 267.00, "name": "日常用品 - 大型超市" },
  { "date": "2025-11-27", "amount": 75.00, "name": "交通出行 - 网约车" },
  { "date": "2025-11-28", "amount": 198.00, "name": "餐饮美食 - 烧烤店" },
  { "date": "2025-11-30", "amount": 1350.00, "name": "购物消费 - 母婴用品店" },
  { "date": "2025-11-24", "amount": 38.00, "name": "餐饮美食 - 快餐连锁店" },
  { "date": "2025-11-25", "amount": 267.00, "name": "日常用品 - 大型超市" },
  { "date": "2025-11-27", "amount": 75.00, "name": "交通出行 - 网约车" },
  { "date": "2025-11-28", "amount": 198.00, "name": "餐饮美食 - 烧烤店" },
  { "date": "2025-11-30", "amount": 1350.00, "name": "购物消费 - 母婴用品店" }
]


const MonthPieDom = ref(null)
let MonthPie = null


function getDaysInMonth(year, month) {
  return new Date(year, month, 0).getDate();
}

function prepareMonthlyChartOption(year, month, rawData) {
  const totalDays = getDaysInMonth(year, month);
  const xAxisDates = [];
  for (let i = 1; i <= totalDays; i++) {
    xAxisDates.push(i + '日');
  }

  const defaultIncomeData = new Array(totalDays).fill(null);
  const defaultExpenseData = new Array(totalDays).fill(null);

  // 为了在 formatter 中快速查找，创建一个按日期（日）索引的映射
  const dataMap = {};
  rawData.forEach(item => {
    const d = new Date(item.date);
    if (d.getFullYear() === year && d.getMonth() + 1 === month) {
      const day = d.getDate();
      dataMap[day] = item;
    }
  });

  // 对数据进行对数变换并填充
  rawData.forEach(item => {
    const dataDate = new Date(item.date);
    if (dataDate.getFullYear() === year && dataDate.getMonth() + 1 === month) {
      const day = dataDate.getDate();
      const index = day - 1;

      // 使用 Math.log1p 处理，对0值更友好
      if (item.income !== undefined && item.income !== null && item.income > 0) {
        defaultIncomeData[index] = Math.log1p(item.income);
      }
      if (item.expense !== undefined && item.expense !== null && item.expense > 0) {
        defaultExpenseData[index] = -Math.log1p(item.expense);
      }
    }
  });

  const option = {
    title:{
      text: `${month}月每日收支记录`,
      top: 1,
    },
    color:['#92487A','#ff0000'],
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: function(params) {
        // params[0].axisName 就是 X 轴的标签，例如 "1日"
        const day = parseInt(params[0].axisName);
        let tooltipContent = `${month}月${day}日<br/>`;

        // 遍历 params 数组，它包含了所有系列（收入、支出）的数据
        params.forEach(param => {
          // param.seriesName 是系列名称，例如 "Income" 或 "Expenses"
          // param.value 是经过处理后的值（可能是对数变换后的值）

          // 关键：我们需要显示原始值，而不是处理后的值
          // 幸运的是，我们可以通过 param.dataIndex 获取该数据在原始数组中的索引
          const originalIndex = param.dataIndex;

          // 假设你在设置 option 之前，有原始的收入和支出数据数组
          // 这里需要确保这些数组在当前作用域可访问，或者作为 option 的一部分传入
          // 为了简单起见，我们这里直接从 param.value 反向计算，但这只适用于特定的变换
          // 更好的方法是在 series.data 中存储原始值，或者使用下面的方案二。

          // 注意：由于我们之前对数据进行了对数变换，param.value 是 log1p(原始值)
          // 所以原始值是 Math.expm1(param.value)
          // 但如果原始值是 0，log1p(0) 是 0, Math.expm1(0) 也是 0，这没问题。
          const originalValue = Math.expm1(Math.abs(param.value));

          const seriesName = param.seriesName === 'Income' ? '收入' : '支出';
          tooltipContent += `${seriesName}: ${originalValue.toFixed(2)}元<br/>`;
        });

        return tooltipContent;
      }
    },
    legend: {},
    grid: {
      left: '1%',
      right: '1%',
      bottom: '10%',
      top: '10%',
      containLabel: true
    },
    yAxis: [
      {
        type: 'value',
        show: false,
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { show: false }
      }
    ],
    xAxis: [
      {
        type: 'category',
        axisTick: { show: false },
        data: xAxisDates,
        axisLabel: {
          show: true,
          interval: 0,
          rotate: 45,
          margin: 15,
          align: 'right',
          fontSize: 11
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        }
      }
    ],
    series: [
      {
        name: 'Income',
        type: 'bar',
        stack: 'Total',
        barWidth: '40%',
        emphasis: { focus: 'series' },
        data: defaultIncomeData,
        itemStyle: {
          borderRadius: [50, 50, 0, 0],
        }
      },
      {
        name: 'Expenses',
        type: 'bar',
        stack: 'Total',
        barWidth: '40%',
        emphasis: { focus: 'series' },
        data: defaultExpenseData,
        itemStyle: {
          borderRadius: [0, 0, 50, 50],
        }
      }
    ]
  };

  return option;
}

const BarChartData =[
  {"date": "2025-11-01", "income": 5000.0, "expense": 89.5},
  {"date": "2025-11-02", "income": 0, "expense": 120.3},
  {"date": "2025-11-03", "income": 3000.0, "expense": 45.2},
  {"date": "2025-11-04", "income": 0, "expense": 980.0},
  {"date": "2025-11-05", "income": 8000.0, "expense": 56.8},
  {"date": "2025-11-06", "income": 0, "expense": 75.2},
  {"date": "2025-11-07", "income": 4500.0, "expense": 210.5},
  {"date": "2025-11-08", "income": 0, "expense": 68.9},
  {"date": "2025-11-09", "income": 12000.0, "expense": 150.0},
  {"date": "2025-11-10", "income": 0, "expense": 45.6},
  {"date": "2025-11-11", "income": 6000.0, "expense": 890.0},
  {"date": "2025-11-12", "income": 0, "expense": 102.3},
  {"date": "2025-11-13", "income": 7500.0, "expense": 76.4},
  {"date": "2025-11-14", "income": 0, "expense": 130.8},
  {"date": "2025-11-15", "income": 20000.0, "expense": 3500.0},
  {"date": "2025-11-16", "income": 0, "expense": 68.9},
  {"date": "2025-11-17", "income": 5500.0, "expense": 92.1},
  {"date": "2025-11-18", "income": 0, "expense": 300.5},
  {"date": "2025-11-19", "income": 9000.0, "expense": 67.8},
  {"date": "2025-11-20", "income": 0, "expense": 450.0},
  {"date": "2025-11-21", "income": 3800.0, "expense": 83.4},
  {"date": "2025-11-22", "income": 0, "expense": 1200.0},
  {"date": "2025-11-23", "income": 6500.0, "expense": 56.3},
  {"date": "2025-11-24", "income": 0, "expense": 78.0},
  {"date": "2025-11-25", "income": 15000.0, "expense": 210.5},
  {"date": "2025-11-26", "income": 0, "expense": 98.7},
  {"date": "2025-11-27", "income": 4200.0, "expense": 65.4},
  {"date": "2025-11-28", "income": 0, "expense": 150.0},
  {"date": "2025-11-29", "income": 8800.0, "expense": 7600.0},
  {"date": "2025-11-30", "income": 0, "expense": 89.2}
];

const option1 = {
  title: {
    text: '支出分类比例',
    left: 'center',
    top: 1,
  },
  series: [
    {
      name: '支出',
      type: 'pie',
      center:['50%', '60%'],
      itemStyle: {
        borderRadius: 5
      },
      label: {
        show: true
      },
      emphasis: {
        label: {
          show: true
        }
      },
      data: [
        { value: 40, name: '餐饮' },
        { value: 33, name: '日用品' },
        { value: 28, name: '交通' },
        { value: 22, name: '教育' },
        { value: 20, name: '娱乐' },
        { value: 15, name: '办公' },
        { value: 12, name: '医疗' },
        { value: 10, name: '其他' }
      ]
    },

  ]
};

const option2 = {
  title: {
    text: '收入分类比例',
    left: 'center',
    top: 1,
  },
  series: [
    {
      name: 'Radius Mode',
      type: 'pie',
      center:['50%', '60%'],
      itemStyle: {
        borderRadius: 5
      },
      label: {
        show: true
      },
      emphasis: {
        label: {
          show: true
        }
      },
      data: [
        { value: 40, name: '工资' },
        { value: 33, name: '退款' },
        { value: 28, name: '补助' },
      ]
    },

  ]
};

const option3 = {
  series: [
    {
      name: 'Access From',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      center:['50%', '60%'],
      itemStyle: {
        borderRadius: 10,
      },
      label: {
        show: true,
      },
      data: [
        { value: 1048, name: '收入' },
        { value: 2000, name: '支出' },
        { value: 0, name: '互转' },
      ]
    }
  ]
};

// 初始化图表
const initChart = () => {
  BarChart = echarts.init(BarchartDom.value)
  const chartOption = prepareMonthlyChartOption(2025, 11, BarChartData);
  BarChart.setOption(chartOption);

  OutPieChart = echarts.init(OutPieDom.value)
  OutPieChart.setOption(option1);

  InPieChart = echarts.init(InPieDom.value)
  InPieChart.setOption(option2);

  MonthPie = echarts.init(MonthPieDom.value)
  MonthPie.setOption(option3);
}

// 生命周期钩子
onMounted(() => {
  //挂载后才能操作DOM
  initChart()
})

onUnmounted(() => {
  if (BarChart) {
    BarChart.dispose() // 销毁实例
  }
})



</script>

<style scoped>

.icon {
  width: 40px;
  height: 100%;
  margin: 5px;
  margin-right: 10px;
  fill: #636aef;
}


.box {
  width: 100%;
  height: 100%;
  background-color: white;
  border: 1px solid #ccc;
  box-sizing: border-box;

  border-radius: 5px;
  padding: 16px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
}


.text > * {
  margin-bottom: 10px;
}

.text > :last-child {
  margin-bottom: 0;
}


.DataText {
  font-size: 20px;
}

.TotalMoney {
  font-size: 50px;
  color: #ef4444;
}

.month-total-pie {
  height: 100%;
  width: 40%;
}

.accounts {
  height: 80%;
  display: flex;
  flex-wrap: wrap;
  margin-top: 2px;
}

.one-account  {
  width: 32%;
  height: 50%;
  margin: 8px 3px auto;
  border-radius: 5px;
  background-color: #eff0fd;

  display: flex;
  flex-direction: row;
  align-items: center;
}

.accounts > :first-child {
  background-color: #b1b5f7;
}

.tall-box {
  height: 450px !important;
}

</style>