<template>
  <div class="ListTable" v-if="ScenicSpotSource">
    <Border>
      <SmallTittle  style="margin-bottom: 10px;">行程统计</SmallTittle>
      <div
        ref="scrollContainer"
        class="Content"
        @mouseenter="pauseAutoScroll"
        @mouseleave="resumeAutoScroll"
      >
        <div class="column" v-for="(each, index) in ScenicSpotSource" :key="`${each.name}-${index}`">
          <a href="/SpotsMsg">
          <div class="columnContent">
            <div class="ID">{{ index+1 }}</div>
            <div class="Msg">
              <div class="Name">{{ each.name }}</div>
              <div class="Adder">
                <span style="color: rgb(157, 157, 164);">地址：</span>
                <span>{{ each.Adder }}</span>
              </div>
            </div>
            <div class="Rank" v-html="SetText(each.Rank)"></div>
          </div>
          <div class="UnderLine"></div>
          </a>
        </div>
      </div>
    </Border>
  </div>

</template>

<script setup>
import SmallTittle from '@/modules/travel/views/Board/SmallTittle.vue';
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'



const ScenicSpotSource = ref()
ScenicSpotSource.value = [
    {
      "name":"陕西历史博物馆",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"1"
    },
    {
      "name":"兵马俑博物馆",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"2"
    },
    {
      "name":"西安城墙",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"0"
    },
    {
      "name":"碑林博物院",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"1"
    },
    {
      "name":"西北大学太白校区",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"1"
    },
    {
      "name":"小寨商业街",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"2"
    },
    {
      "name":"大雁塔",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"0"
    },
    {
      "name":"法门寺",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"1"
    },
    {
      "name":"欢乐谷",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"2"
    },
    {
      "name":"钟楼",
      "Adder":"陕西省/西安市/临潼区",
      "Rank":"red"
    }

  ]

const scrollContainer = ref(null)
let animationFrame = 0
let lastFrameTime = 0
let scrollPosition = 0
let paused = false
const SCROLL_SPEED = 18

function autoScroll(timestamp) {
  const container = scrollContainer.value
  if (!container) return

  if (!paused && container.scrollHeight > container.clientHeight) {
    if (lastFrameTime) {
      scrollPosition += SCROLL_SPEED * ((timestamp - lastFrameTime) / 1000)
      if (scrollPosition + container.clientHeight >= container.scrollHeight - 1) {
        scrollPosition = 0
      }
      container.scrollTop = scrollPosition
    }
  }

  lastFrameTime = timestamp
  animationFrame = requestAnimationFrame(autoScroll)
}

function pauseAutoScroll() {
  paused = true
  scrollPosition = scrollContainer.value?.scrollTop || 0
}

function resumeAutoScroll() {
  scrollPosition = scrollContainer.value?.scrollTop || 0
  paused = false
  lastFrameTime = 0
}

onMounted(async () => {
  await nextTick()
  animationFrame = requestAnimationFrame(autoScroll)
})

onBeforeUnmount(() => {
  cancelAnimationFrame(animationFrame)
  animationFrame = 0
})



//设置提示内容
function SetText(params) {
  switch (params) {
    case '0':
      return '<span style="color: rgb(0, 255, 30);">5A景区</span>'
      break;
    case '1':
      return '<span style="color: rgb(255, 0, 0)">人文景区</span>'
      break;

    default:
      return '<span style="color: white">城市景区</span>'
      break;
  }
}

</script>

<style scoped>
  .ListTable{
    font-size: 30px;
    text-align: center;

    box-sizing:border-box;
    padding: 5px 10px;
    height: 340px;
    width: 560px;

  }
  .Content{
    height: 280px;
    overflow-x: hidden;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #258da0 rgba(6, 25, 38, 0.7);
    display: flex;
    align-items:center;
    flex-direction: column;
    padding-right: 4px;
  }
  .Content::-webkit-scrollbar{
    width: 6px;
  }
  .Content::-webkit-scrollbar-track{
    background: rgba(6, 25, 38, 0.7);
  }
  .Content::-webkit-scrollbar-thumb{
    background: #258da0;
    border-radius: 3px;
  }
  .Content::-webkit-scrollbar-thumb:hover{
    background: #36c4d8;
  }

  .column{
    z-index: 0;
    width: 90%;
    height: 65px;
    flex: 0 0 65px;
    margin-top: 10px;

    display: flex;
    align-items:flex-end;
    justify-content:center;
    flex-direction: column;
  }
  .columnContent{
    width: 100%;
    height: 65px;

    display: flex;
    align-items: center;
    justify-content: center;
  }
  .UnderLine{
    width: 90%;
    height: 5px;
    margin-right: 15px;

    margin-top: 5px;
    border-bottom: dashed rgb(0, 220, 255) 3px;
  }
  .ID{
    width: 25px;
    height: 25px;
    border-radius: 25%;

    font-size: 15px;
    text-align: center;
    line-height: 30px;
    color: black;


    background-color: rgb(230, 237, 192);
  }
  .Msg{
    width: 350px;
    height: 60px;
  }
  .Name,.Adder{
    padding-left: 15px;

    text-align: left;
    font-size: 10px;
  }
  .Name{
    height: 40px;
    font-size: 20px;
    line-height: 40px;
    font-weight: bold;

    color: rgb(24, 144, 255);
  }
  .Adder{
    height: 20px;

    color: beige;
  }
  .Rank{
    width: 100px;
    height: 30px;

    font-size: 15px;
    text-align: center;
    line-height: 30px;
    color: rgb(0, 255, 30);
  }
  .red{
    color: yellow;
  }
</style>
