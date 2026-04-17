<template>
  <div class="map" v-if="showCityList">
    <button class="Up" @click="Up">上一级</button>
    <div ref="charts" style="width: 100%;height:570px;padding-top: 10px;box-sizing: border-box;"></div>
  </div>

</template>

<script setup>
import { ref, onMounted ,watch} from 'vue';
import * as echarts from 'echarts';
import {getVisitedCities} from '@/modules/travel/apis/travel.js'
import data from '@/assets/中国_市.json'

// 航线
const Route = ref();
// 机场
const Airport  = ref();
// 火车站
const RailwayStation  = ref();
// DOM元素
const charts = ref(null);
// 城市足迹
const showCityList = ref([]);
// 地图编码
const MapCode = ref('11');
// 注册图表
let MapChart = null;

const GeoData = ref(data);

let station = [
  {"name":"西安北","value":[108.938512,34.376164]},
  {"name":"广州南","value":[113.269323,22.988558]},
  {"name":"西宁","value":[101.814339,36.620183]},
  {"name":"深圳北","value":[114.029225,22.609581]},
  {"name":"南京南","value":[118.798171,31.96873]},
  {"name":"上海虹桥","value":[121.320666,31.194106]},
  {"name":"咸阳西","value":[108.666737,34.330999]},
  {"name":"青岛北","value":[120.374402,36.168923]},
]

function CreateMapOption(name,MapData = [],route = true,airport = true,train = true){
  let option = {
          geo: {
              map: name,
              roam: true,
              selectedMode: false,
              zoom: 1.6,
              center: [105, 39],
              top: 'top',
              show: true,
              label: {
                show: true,
                color: '#ffffff',
                formatter: (params) => {
                  return MapData.includes(params.name) ? params.name : ''
                }
              },
              // 地图样式
              itemStyle: {
                  borderColor: "rgba(111, 241, 184)",
                  borderWidth: 0.4,
                  areaColor: {
                      type: "radial",
                      x: 0.5,
                      y: 0.5,
                      r: 0.8,
                      colorStops: [
                          {
                              offset: 0,
                              color: "rgba(147, 235, 248, 0)", // 0% 处的颜色
                          },
                          {
                              offset: 1,
                              color: "rgba(147, 235, 248, .2)", // 100% 处的颜色
                          },
                      ],
                      globalCoord: false, // 缺为 false
                  },
                  shadowColor: "rgba(128, 217, 248, .3)",
                  shadowOffsetX: -2,
                  shadowOffsetY: 2,
                  shadowBlur: 10,
              },
              // 鼠标悬停样式
              emphasis: {
                  label: {
                      show: false,
                  },
                  itemStyle: {
                      areaColor:{
                          type: "radial",
                          x: 0.5,
                          y: 0.5,
                          r: 0.8,
                          colorStops: [
                              {
                                  offset: 0,
                                  color: "rgba(147, 235, 248, 0)", // 0% 处的颜色
                              },
                              {
                                  offset: 1,
                                  color: "rgba(56,155,183, .8)", // 100% 处的颜色
                              },
                          ],
                          globalCoord: false, // 缺为 false
                      },
                      borderWidth: 1,
                  },
              },
          },
          visualMap: {
            type:'piecewise',
            show:false,
            pieces: [
              {lte: 1,color:'rgba(82,219,174,0.6)'},
              {lte: 0,color:{
                      type: "radial",
                      x: 0.5,
                      y: 0.5,
                      r: 0.8,
                      colorStops: [
                          {
                              offset: 0,
                              color: "rgba(147, 235, 248, 0)", // 0% 处的颜色
                          },
                          {
                              offset: 1,
                              color: "rgba(147, 235, 248, .2)", // 100% 处的颜色
                          },
                      ],
                      globalCoord: false, // 缺为 false
                  }},
            ],
            seriesIndex:0,
            left: 20,
            bottom: 20,
          },
          tooltip: {
            trigger: 'item',
            axisPointer: {
              type: 'shadow'
            },
            backgroundColor :'rgba(50,50,50,0.7)',
            borderColor:'#6e8fda',
            textStyle:{
              color:'#ffff',
              align:'left'
            },
            formatter:  (params) => {
                  if(params.value){
                    return `${params.name}<br />已探索：<strong style="color: red;">${params.value}%</strong>`
                  }
                  else{
                    return `${params.name}<br /><strong style="color: red;">尚未探索</strong>`
                  }
          }
          },
          legend:{
            show:false,
            selected:{
              'route':route,
              'airport':airport,
              'train':train,
            }
          },
          series: [
            {
              type: "map",
              map: name,
              geoIndex: 0,
              data: MapData.map(city => ({name: city,value: 1})),
              selectedMode: false,
              silent: true,
              label: { show: false, },
              itemStyle: {opacity: 1,},
            },
            {
              name:'airport',
              geoIndex:0,
              type:'scatter',
              data:Route.value,
              coordinateSystem:"geo",
              itemStyle: {
                borderWidth:1,
                color: 'rgb(65, 250, 170)',
              },
              tooltip:{
                trigger:'item',
                formatter:'{b}',
              },
            },
            {
              name:'route',
              geoIndex:0,
              type:'lines',
              data:Airport.value,
              lineStyle:{
                color:'rgb(255, 255, 255)',
                width:2,
                curveness:0.3, //弧度
              },
                zlevel:1,
                effect: {
                show: true,
                period: 4,
                trailLength: 0,
                color: 'rgb(255, 202, 102,.8)',
                symbolSize: 15,
                symbol:'path://M-122.27,388.39c1.54.13,2.81,1.22,3.85,3.27a14.85,14.85,0,0,1,1.54,6.75v7.75l20.62,12.9a1,1,0,0,1,.5,1.09l-.78,5.39a.91.91,0,0,1-.31.59.53.53,0,0,1-.19.15,1,1,0,0,1-.89.12l-18.95-5.51v12.47l5.94,3.39a1,1,0,0,1,.55.93v4.64a1.12,1.12,0,0,1-.33.77l0,0a1.21,1.21,0,0,1-.9.27l-10.62-1.51-10.61,1.51a1,1,0,0,1-.86-.23,1.13,1.13,0,0,1-.39-.82l0-4.64a1.06,1.06,0,0,1,.33-.77.56.56,0,0,1,.22-.17l5.93-3.39V420.92l-18.94,5.47a1.14,1.14,0,0,1-1-.17,1,1,0,0,1-.42-.87v-6.17a1,1,0,0,1,.54-.91l19.82-12.18v-7.7a15,15,0,0,1,1.54-6.75c1-2,2.31-3.12,3.86-3.25Z',
              },
              tooltip:{
                trigger:'item',
                formatter: '{b}',
              },
            },
            {
              name:'train',
              Enabled:false,
              geoIndex:0,
              type:'scatter',
              data:station,
              coordinateSystem:"geo",
              symbol:'path://M895.616384 347.812188q0 10.22977-0.511489 19.436563t-1.534466 19.436563q-9.206793 84.907093-37.338661 163.164835t-71.096903 150.377622-99.228771 138.613387-121.734266 127.872128q-9.206793 11.252747-23.528472 11.252747-15.344655 0-24.551449-11.252747-65.470529-61.378621-122.245754-128.895105t-100.251748-141.170829-71.608392-152.935065-36.315684-165.210789q0-8.183816-0.511489-15.344655t-0.511489-15.344655q0-71.608392 28.131868-135.032967t76.211788-110.481518 113.038961-74.677323 138.613387-27.62038 138.101898 27.62038 112.527473 74.677323 76.211788 110.481518 28.131868 135.032967zM540.643357 507.396603q33.758242 0 63.424575-12.787213t51.66034-34.26973 34.781219-50.637363 12.787213-61.89011-12.787213-61.89011-34.781219-50.637363-51.66034-34.26973-63.424575-12.787213-63.424575 12.787213-52.171828 34.26973-35.292707 50.637363-12.787213 61.89011 12.787213 61.89011 35.292707 50.637363 52.171828 34.26973 63.424575 12.787213z',
              symbolSize:10,
              itemStyle: {
                // borderWidth:1,
                color: 'rgb(256,0,0)',
              },
              label:{
                show:true,
                position:'bottom',
                formatter:'{b}',
                fontSize:12,
              },
              tooltip:{
                trigger:'item',
                formatter:'{b}',
              },
            },
          ]
};
  return option;
}

getVisitedCities().then(res => {
  console.log(res.data)
  showCityList.value = res.data
  init()
})

const init = () => {

  MapChart = echarts.init(charts.value);
  echarts.registerMap('map', GeoData.value);

  MapChart.setOption(CreateMapOption('map', showCityList.value, false, false, true), true);

  MapChart.on('click',(event) => {
    console.log(data);
    data.features.forEach(element => {
      if (element.properties.name === event.name)
      {
        console.log(element.properties);
        MapCode.value = element.properties.adcode;
        console.log(MapCode.value);
      };
    });
})

}



function Up(){
  let code;
  if (typeof(GeoData.features[0].properties.parent)=='string') {
    code = JSON.parse(GeoData.features[0].properties.parent).adcode
  }
  else{
    code = GeoData.features[0].properties.parent.adcode
  }
  let A = String(code).substring(0,2)
  let B = String(code).substring(2,4)
  let C = GeoData.features.length
  // console.log(A,B,C);
  if(C=='1'){
    if(A!='10'){MapCode.value = A+B+'00'}
    else{MapCode.value = 'china'} 
  }
  else if(B!='00'){
    MapCode.value = A+'0000'
  }
  else{
    MapCode.value = 'china'
  }
}

</script>

<style scoped>
  .map{
    height: 560px;
    width: 800px;
    position: relative;
  }
  
  .Up{
    width: 100px;
    height: 40px;
    
    position: absolute;
    bottom: 10px;
    right: 20px;
    z-index: 111;
    border: 2px solid rgb(6, 251, 247);

    background: radial-gradient(rgba(0, 225, 229, 0),rgba(0, 225, 229, .2));

    font-family:'SimHei';
    font-weight: bold;
    font-size: 20px;
    color: rgb(50, 226, 246);
  }
  button:hover{
    background: radial-gradient(rgba(136, 200, 246, 0),rgba(178, 219, 234, 0.5));
  }
</style>