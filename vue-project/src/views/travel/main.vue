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
import Header from '@/views/travel/MainWindow/Header.vue'
import NumCharts from '@/views/travel/MainWindow/NumCharts.vue'
import PieCharts from '@/views/travel/MainWindow/PieCharts.vue'
import ListTable from '@/views/travel/MainWindow/ListTable.vue'
import Map from '@/views/travel/MainWindow/Map.vue'
import BarCharts from '@/views/travel/MainWindow/BarCharts.vue'
import TittleMsg from '@/views/travel/MainWindow/TittleMsg.vue'
import RightTable from '@/views/travel/MainWindow/RightTable.vue'


function Floot(event){
  //设置为航空面板，普通地图
  PanelType.value = 'Normal';
  ShowAirport.value=false;
  ShowRoute.value = false;
  ShowTrain.value = false;
  Fresh.value=!Fresh.value;
}
function Plane(event){
  //设置为航空面板，航线图
  PanelType.value = 'Flight';
  ShowAirport.value=true;
  ShowRoute.value = true;
  ShowTrain.value = false;
  Fresh.value=!Fresh.value;
}
function Train(event){
  //设置为铁路面板，列车图
  PanelType.value = 'Train';
  ShowAirport.value=false;
  ShowRoute.value = false;
  ShowTrain.value = true;
  Fresh.value=!Fresh.value;
}
</script>

<style scoped>
*{
  margin: 0px;
  padding: 0px;
  cursor:default;
}
.BodyMain {
  width: 100%;
  height: 100%;
  background-position: center center;
  background-image: url('@/assets/img/pageBg.png');

  display: flex;
  flex-direction: column;
  /* justify-content: center; */
  align-items: center;
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
</style>