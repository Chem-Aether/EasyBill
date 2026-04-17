<template >
<v-scale-screen     
  width="1920"
  height="1080"
  :delay="500"
  :fullScreen="false"
  :boxStyle="{
    background: '#03050c',
  }"
  :wrapperStyle="null"
  :autoScale="true"
>

  <div class="admin-link" @click="goToAdmin">
    <svg style="width:16px;height:16px;fill:white;margin-right:6px;" viewBox="0 0 1024 1024">
      <path d="M857.856 0H55.296a53.248 53.248 0 0 0-51.2 53.248V230.4a51.2 51.2 0 0 0 102.4 0V106.496h708.352a102.4 102.4 0 0 1 102.4 102.4v606.464a102.4 102.4 0 0 1-102.4 102.4H105.984v-120.32a51.2 51.2 0 0 0-102.4 0V972.8a53.248 53.248 0 0 0 51.2 51.2h803.072A166.4 166.4 0 0 0 1024 858.368v-691.2A166.4 166.4 0 0 0 857.856 0z" p-id="4975"></path><path d="M91.136 512a57.344 57.344 0 0 0 57.088 57.088h380.672l-103.68 103.68a53.248 53.248 0 0 0 75.264 75.264l198.144-198.4a52.992 52.992 0 0 0 0-75.264l-198.4-198.4a53.248 53.248 0 1 0-75.264 75.264l103.68 103.68H148.224A57.344 57.344 0 0 0 91.136 512z" p-id="4976"></path>
    </svg>
    <a style="color: white; cursor: pointer">进入管理面板</a>
  </div>

  <Header tittle="旅行足迹可视化平台"/>
  <div class="BodyTable">
    <div class="left">
      <NumCharts class="left-1"/>
      <PieCharts class="left-2"/>
      <ListTable class="left-3"/>
    </div>
  <div class="center">
    <TittleMsg class="center-1"/>
    <div class="Buttons">
      <button class="MapButton" @click="Floot">足迹地图</button>
      <button class="MapButton" @click="Plane">航线地图</button>
      <button class="MapButton" @click="Train">铁路地图</button>      
    </div>
    <Map class="center-2" />
    <BarCharts class="center-3"/>
  </div>  
  <RightTable class="right-1"/>
  </div>

</v-scale-screen>
</template>

<script setup>
import VScaleScreen from 'v-scale-screen'
import Header from '@/modules/travel/views/Board/Header.vue'
import NumCharts from '@/modules/travel/views/Board/NumCharts.vue'
import PieCharts from '@/modules/travel/views/Board/PieCharts.vue'
import ListTable from '@/modules/travel/views/Board/ListTable.vue'
import Map from '@/modules/travel/views/Board/Map.vue'
import BarCharts from '@/modules/travel/views/Board/BarCharts.vue'
import TittleMsg from '@/modules/travel/views/Board/TittleMsg.vue'
import RightTable from '@/modules/travel/views/Board/RightTable.vue'

import { useTravleStore } from '@/modules/travel/stores/TravelStore.js'
const store = useTravleStore()

import { useRouter } from 'vue-router'
const router = useRouter()

function Floot(event){
  //设置为铁路面板，普通地图
  store.mapName = '铁路';
  store.mapType = 'floor';
}
function Plane(event){
  //设置为航空面板，航线图
  store.mapName = '航空';
  store.mapType = 'flight';
}
function Train(event){
  //设置为铁路面板，列车图
  store.mapName = '铁路';
  store.mapType = 'train';
}

function goToAdmin(event){
  router.push('/travel/admin')
}
</script>

<style scoped>
*{
  margin: 0px;
  padding: 0px;
  cursor:default;
}

.BodyTable{
  display: flex;
}
.left-3{
  padding-bottom: 5px;
}
button:hover{
    background: radial-gradient(rgba(136, 200, 246, 0),rgba(178, 219, 234, 0.5));
}
.Buttons{
  width: 800px;
  display: flex;
  justify-content: center;
  
}
.MapButton{
  height: 40px;
  width: 150px;
  margin-right: 10px;
  border: 2px solid rgb(6, 251, 247);

  background: radial-gradient(rgba(0, 225, 229, 0),rgba(0, 225, 229, .2));

  font-family:'SimHei';
  font-weight: bold;
  font-size: 20px;
  color: rgb(50, 226, 246);
  z-index: 111;
}

.admin-link {
  position: fixed;
  top: 20px;
  right: 30px;
  z-index: 9999;
  padding: 8px 16px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}
</style>