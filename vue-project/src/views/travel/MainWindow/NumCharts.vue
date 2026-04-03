<template>
  <div class="NumCharts" v-if="PanelDataSource"><Border>
    <SmallTittle >{{ PanelDataSource.panel1.tittle }}数据统计</SmallTittle>
    <div class="NumCharts-Box" style="width: 100%;height:90%;">
      <div class="Box-column"  v-for="value,key in PanelDataSource.panel1.data">
        <span>{{ value }}</span>
        <div></div>
        <p>{{ key }}</p>
      </div>
    </div>
  </Border></div>
</template>

<script setup>
import SmallTittle from '@/views/travel/MainWindow/SmallTittle.vue';
import { ref} from 'vue';

const PanelDataSource = ref()
PanelDataSource.value = {
  "panel1":{
      "tittle":"铁路",
      "data":{
          "里程":"4090",
          "时长":"7:51",
          "车次":"3",
          "站点":"4"
      }
  },
  "panel2":{
    "tittle":"铁路",
    "data":[
      {
        "name":"普快",
        "num":"3"
      },
      {
        "name":"动车",
        "num":"2"
      },
      {
        "name":"高速动车",
        "num":"3"
      },
      {
        "name":"直达特快",
        "num":"4"
      },
      {
        "name":"特快列车",
        "num":"5"
      },
      {
        "name":"城际列车",
        "num":"6"
      }
    ]
  }
}
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