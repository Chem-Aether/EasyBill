<template>
  <div class="NumCharts" v-if="PanelDataSource"><Border>
    <SmallTittle >{{ tittle }}数据统计</SmallTittle>
    <div class="NumCharts-Box" style="width: 100%;height:90%;">
      <div class="Box-column"  v-for="item in PanelDataSource">
        <span>{{ item.value }}</span>
        <div></div>
        <p>{{ item.label }}</p>
      </div>
    </div>
  </Border></div>
</template>

<script setup>
import SmallTittle from '@/views/travel/MainWindow/SmallTittle.vue';
import { ref} from 'vue';
import {getTicketDashboard} from '@/api/travel.js'

const tittle = ref("铁路");
const PanelDataSource = ref()
PanelDataSource.value = {
    "tittle":"铁路",
    "data":[
      {name:"里程", value:"4090"},
      {name:"时长", value:"10d7h9m"},
      {name:"车次", value:"3"},
      {name:"站点", value:"4"},
    ],
}

getTicketDashboard('flight').then(res => {
  console.log('统计数据', res.data)
  PanelDataSource.value = res.data || []
})
</script>
  
<style scoped>
  .NumCharts{
    font-size: 30px;
    text-align: center;
    color: rgb(253, 252, 252);

    box-sizing:border-box;
    padding: 5px 10px;
    height: 340px;
    width: 560px;

  }
  .Box-column div{
    
    width: 125px;
    height: 125px;
    
    /* box-sizing: border-box; */
    margin-top: 50px;
  }
  .NumCharts-Box {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: center;

  }
  .NumCharts-Box > * p {
    width: 100%; 
    height: 50px;
    line-height: 50px;
  }
  .Box-column{
    position: relative;
  }
  .Box-column span{
    position: absolute;
    top: 50px;
    left: 0px;

    width: 125px;
    height: 125px;

    font-size: 30px;
    font-weight: bold;
    text-align: center;
    line-height: 125px;
  }
  .Box-column:nth-child(1){
    color: rgb(247, 170, 30);
  }
  .Box-column:nth-child(1) div{
    background-image: url("@/assets/img/circular/yellow.gif");
    background-size: cover;
  }
  .Box-column:nth-child(2){
    color: rgb(0, 225, 229);
  }
  .Box-column:nth-child(2) div{
    background-image: url("@/assets/img/circular/blue.gif");
    background-size: cover;
  }
  .Box-column:nth-child(3){
    color: rgb(65, 250, 170);
  }
  .Box-column:nth-child(3) div{
    background-image: url("@/assets/img/circular/green.gif");
    background-size: cover;
  }
  .Box-column:nth-child(4){
    color: rgb(2247, 35, 119);
  }
  .Box-column:nth-child(4) div{
    background-image: url("@/assets/img/circular/red.gif");
    background-size: cover;
  }
</style>