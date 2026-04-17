<template>
  <div class="Right">
    <div class="Tittle">
      <SmallTittle style="margin-bottom: 10px;">机票/车票统计</SmallTittle>
    </div>
    <div class="RightTable">
      <Ticket />
    </div>

    <div class="RightModel">
      <Model />
    </div>
  </div>
</template>

<script setup>
import Ticket from './Ticket.vue'
import Model from './Model.vue'
import SmallTittle from '@/modules/travel/views/Board/SmallTittle.vue';
</script>

<style scoped>
* {
  list-style: none;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.Right {
  height: 1020px;
  width: 560px;
  background-color: rgba(81, 46, 238, 0.044);
  padding: 5px 10px 0;
  display: flex;
  flex-direction: column;
}

.Tittle {
  height: 45px;
  flex-shrink: 0;
}

.RightTable {
  width: 540px;
  height: calc(100% - 45px - 300px);

  overflow-y: auto;
  overflow-x: hidden;
  scroll-behavior: smooth;

  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* Chrome 隐藏滚动条 */
.RightTable::-webkit-scrollbar {
  display: none;
}

.RightModel {
  width: 540px;
  height: 300px;
  background-color: bisque;
  flex-shrink: 0;
}
</style>