<template>
    <div class="Barcharts" v-if="CityPercentSource"><Border>
      <SmallTittle>城市探索报告</SmallTittle>
      <div style="width: 100%;height:90%;">
        <div ref="Bar" style="width: 100%;height:100%;"></div>        
      </div>
    </Border></div>
</template>

<script setup>
import SmallTittle from '@/modules/travel/views/Board/SmallTittle.vue';
import {onMounted, ref, watch, nextTick} from 'vue'
import * as echarts from 'echarts';

//取DOM元素
const Bar = ref();
const CityPercentSource = ref()
//初始化地图数据
CityPercentSource.value = [
    {
        "name": "北京市",
        "went": "3",
        "total": "16",
        "percent": "18.75"
    },
    {
        "name": "江苏省",
        "went": "2",
        "total": "13",
        "percent": "15.38"
    },
    {
        "name": "浙江省",
        "went": "5",
        "total": "11",
        "percent": "45.45"
    },
    {
        "name": "安徽省",
        "went": "2",
        "total": "16",
        "percent": "12.5"
    },
    {
        "name": "福建省",
        "went": "7",
        "total": "9",
        "percent": "77.77"
    },
    {
        "name": "江西省",
        "went": "3",
        "total": "11",
        "percent": "27.27"
    },
    {
        "name": "山东省",
        "went": "1",
        "total": "16",
        "percent": "6.25"
    },
    {
        "name": "河南省",
        "went": "2",
        "total": "17",
        "percent": "11.76"
    },
    {
        "name": "湖北省",
        "went": "1",
        "total": "13",
        "percent": "7.69"
    },
    {
        "name": "湖南省",
        "went": "1",
        "total": "14",
        "percent": "7.14"
    },
    {
        "name": "广东省",
        "went": "3",
        "total": "21",
        "percent": "14.28"
    },
    {
        "name": "天津市",
        "went": "3",
        "total": "16",
        "percent": "18.75"
    },
    {
        "name": "广西壮族自治区",
        "went": "6",
        "total": "14",
        "percent": "42.85"
    },
    {
        "name": "海南省",
        "went": "2",
        "total": "4",
        "percent": "50"
    },
    {
        "name": "重庆市",
        "went": "2",
        "total": "38",
        "percent": "5.26"
    },
    {
        "name": "四川省",
        "went": "0",
        "total": "21",
        "percent": "0"
    },
    {
        "name": "贵州省",
        "went": "0",
        "total": "9",
        "percent": "0"
    },
    {
        "name": "云南省",
        "went": "0",
        "total": "16",
        "percent": "0"
    },
    {
        "name": "西藏自治区",
        "went": "0",
        "total": "7",
        "percent": "0"
    },
    {
        "name": "陕西省",
        "went": "8",
        "total": "10",
        "percent": "80"
    },
    {
        "name": "甘肃省",
        "went": "2",
        "total": "14",
        "percent": "14.28"
    },
    {
        "name": "青海省",
        "went": "1",
        "total": "8",
        "percent": "12.5"
    },
    {
        "name": "河北省",
        "went": "1",
        "total": "11",
        "percent": "9.09"
    },
    {
        "name": "宁夏回族自治区",
        "went": "4",
        "total": "5",
        "percent": "80"
    },
    {
        "name": "新疆维吾尔自治区",
        "went": "2",
        "total": "14",
        "percent": "14.28"
    },
    {
        "name": "台湾省",
        "went": "0",
        "total": "5",
        "percent": "0"
    },
    {
        "name": "香港特别行政区",
        "went": "0",
        "total": "4",
        "percent": "0"
    },
    {
        "name": "澳门特别行政区",
        "went": "0",
        "total": "5",
        "percent": "0"
    },
    {
        "name": "山西省",
        "went": "1",
        "total": "11",
        "percent": "9.09"
    },
    {
        "name": "内蒙古自治区",
        "went": "1",
        "total": "12",
        "percent": "8.33"
    },
    {
        "name": "辽宁省",
        "went": "1",
        "total": "14",
        "percent": "7.14"
    },
    {
        "name": "吉林省",
        "went": "1",
        "total": "9",
        "percent": "11.11"
    },
    {
        "name": "黑龙江省",
        "went": "1",
        "total": "13",
        "percent": "7.69"
    },
    {
        "name": "上海市",
        "went": "1",
        "total": "16",
        "percent": "6.25"
    }
]

//渐变色方案
const yData1GradientColor = [
  {
      offset: 0, //offset表示位置【0,1】范围，0表示起始位置，1表示结束位置
      color: '#8063B0' // 起始位置设置此颜色，终止位置设置下面的颜色
  },
  {
      offset: 1,
      color: '#342A61'
  }
]
const yData2GradientColor = [
    {
        offset: 0,
        color: '#9781D4'
    },
    {
        offset: 1,
        color: '#3BA1E3'
    }
]

//构建配置项
function CreateBarOption(Data) {
  let Bar_option = {
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    },
    backgroundColor :'rgba(50,50,50,0.7)',
    borderColor:'#6e8fda',
    textStyle:{
      color:'#ffff',
      align:'left'
    },
  },
  legend:{
    show: true,
    top:"5%",
    textStyle:{
      color:"#ffffff",
    },
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  dataset: {
    dimensions: ['name','went','total', 'percent'],
    source: Data,
  },
  xAxis: { 
    type: 'category',
    axisLabel:{
      show: true,
      rotate:"60",
      formatter: function (params) {
                            var val = "";
                            if (params.length > 4) {
                                val = params.substr(0, 4) + '...';
                                return val;
                            } else {
                                return params;
                            }
                        }
    },
    axisTick: {
      show:false,
      alignWithLabel: false,
    },
  },
  yAxis: [
        {
            type: 'value',
            yAxisIndex: 0,
        },
        {
            type: 'value',
            min: 0,
            max: 100,
            interval: 25, 
            axisLabel: {
                formatter: "{value}%",
            },
        },
    ],
  series: [
  { 
      type: 'bar',
      name:'已探索',
      barGap: "-100%",
      z:1,
      itemStyle: {
                borderRadius: [20, 20, 6, 6],
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, yData2GradientColor),
            }
    },
    { 
      type: 'bar' ,
      name:'总计',
      z:0,
      itemStyle: {
                borderRadius: [20, 20, 6, 6], 
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, yData1GradientColor),
            }
    },
    {
      type: 'line',
      name: '探索率(%)',
      z:2,
      yAxisIndex: 1,
      symbolSize: 10,
      smooth:true,
      itemStyle: {
          color: '#d82aaf',
      },
      lineStyle: {
          color: "#d82aaf",
      },

    }
  ]
};
  return Bar_option;
}

//初始化挂载
var myChart = null;
onMounted(() => {
    myChart = echarts.init(Bar.value);
    myChart.setOption(CreateBarOption(CityPercentSource.value),true);
})
</script>

<style scoped>
  .Barcharts{
    font-size: 30px;
    text-align: center;
    color: red;

    box-sizing:border-box;
    padding: 5px 10px;
    height: 340px;
    width: 800px;
  }

</style>