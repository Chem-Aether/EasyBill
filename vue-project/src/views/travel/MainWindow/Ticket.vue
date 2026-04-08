<template>
  <div class="Ticket" v-if="TicketDataSource" v-for="each in TicketDataSource">
      <div class="TicketHeader" :style="{ 'background-color': color[getRandomInt(0,color.length)]  }"></div>
      <div class="TicketTittle">
      <p class="From">{{ each['From'] }}</p>
      <div class="TicketArrow">
          <p class="TicketCode">{{each['Number']}}</p>
          <p class="TicketArea"></p>
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 90.5 5">
              <rect y="3" width="85.5" height="2" transform="translate(85.5 8) rotate(180)"/>
              <polygon points="90.5 5 79.5 0 80.72 5 90.5 5"/>
          </svg>
          <p>{{each['time']}}</p>            
      </div>
      <p class="To">{{each['To']}}</p>          
      </div>
      <div class="TicketTable">
      <ul>
        <template v-if="each.more.铁路类型">
          <li v-for="key in trainOrder" :key="key">
            {{ key }}：{{ each.more[key] || '-' }}
          </li>
        </template>

        <!-- 航班 -->
        <template v-else>
          <li v-for="key in flightOrder" :key="key">
            {{ key }}：{{ each.more[key] || '-' }}
          </li>
        </template>
      </ul>
      </div>
  </div>
</template>
  
<script setup>
import { ref , nextTick} from 'vue'
import { getTicketData } from '@/api/travel.js'

const trainOrder = ref(['发车时间', '到达时间', '铁路类型', '车型', '里程/km']);
const flightOrder = ref(['起飞时间', '降落时间', '起点', '终点', '注册号', '机型', '里程/km', '经停']);

const TicketDataSource = ref();
TicketDataSource.value = [
    {
        "Number": "MU2320",
        "From": "ZGSZ",
        "To": "ZLXY",
        "time": "2h23min",
        "more": {
            "起点": "深圳宝安",
            "终点": "西安咸阳",
            "起飞时间": "2023/8/8 9:56",
            "降落时间": "2023/8/8 19:56",
            "注册号": "B6616",
            "机型": "A320-300",
            "里程/km": "1104",
            "经停": "吉安井冈山"
        }
    },
    {
        "Number": "G824",
        "From": "西安北",
        "To": "广州南",
        "time": "10h23min",
        "more": {
            "发车时间": "2023/8/8 9:56",
            "到达时间": "2023/8/8 19:56",
            "铁路类型": "高速动车",
            "车型": "CRH380AL",
            "里程/km": "1104"
        }
    },
    {
        "Number": "G6215",
        "From": "广州南",
        "To": "深圳北",
        "time": "1h",
        "more": {
            "发车时间": "2023/8/11 9:56",
            "到达时间": "2023/8/1 19:56",
            "铁路类型": "高速动车",
            "车型": "CR400AF",
            "里程/km": "404"
        }
    },
    {
        "Number": "D2696",
        "From": "咸阳西",
        "To": "西安北",
        "time": "13min",
        "more": {
            "发车时间": "2023/10/3 9:56",
            "到达时间": "2023/10/3 19:56",
            "铁路类型": "动车",
            "车型": "CRH5G",
            "里程/km": "20"
        }
    }
]

getTicketData().then(res => {
  console.log('全部票据数据', res.data)
  TicketDataSource.value = res.data.data
})

//随机颜色盘
const color=ref([
  '#fb7293','#fe9982','#ffca66','#c8e191','#87e3ca','#77eaf9','#33c0e7'
])
//随机设置颜色
function getRandomInt(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min)) + min;
}
</script>
  
  

<style scoped>
*{
  list-style-type: none;
}
svg{
  fill: aliceblue;
}
.Ticket{
  width: 97%;
  height: 300px;
  margin-bottom: 15px;
  box-sizing: border-box;
  border-radius: 20px;
  /* background: radial-gradient(rgba(147, 235, 248, 0),rgba(147, 235, 248, .2)); */
  background: radial-gradient(rgba(65, 250, 170, 0),rgba(65, 250, 170, .2));

  color: rgb(255, 255, 255);
}
.TicketHeader{
  width: 100%;
  height: 40px;

  border-radius: 20px 20px 0px 0px;
  background-color: rgba(234, 150, 255, 0.9);
}
.TicketTittle{
  display: flex;
  justify-content: center;
  align-items: center;

  width: 100%;
  position: relative;
}
.From , .To{
  width: 40%;
  font-size: 30px;
  text-align: center;
  font-weight: bold;
}
.TicketArrow{
  margin: 0px 18px;
  width: 20%;
  position: relative;
}
.TicketArrow p{
  margin: 0px;
  font-size: 20px;

  display: flex;
  justify-content: center;
}
.TicketArrow .TicketCode{
  font-size: 24px;
  color: red;
  font-weight: bold;

  position: absolute;
  top: 5px;
  width: 100%;
}
.TicketArea{
  height: 24px;
}
.TicketTable{
  width: 100%;
}
ul{
  width: 100%;
  height: 140px;

  box-sizing: border-box;
  margin: 0px;
  padding: 0px 20px;
  padding-top: 10px;
}
ul li{
  width: 50%;
  height: 28px;
  display: inline-block;

  font-size: 18px;
  line-height: 28px;
}
</style>
  