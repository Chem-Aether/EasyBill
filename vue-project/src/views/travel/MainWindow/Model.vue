<template>
  <div class="carousel-wrapper">
    <el-carousel arrow="always" :interval="3000">
      <el-carousel-item v-for="img in imgList" :key="img">
        <img :src="img" class="carousel-img" />
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ElCarousel, ElCarouselItem } from 'element-plus'

const imgList = [
  '/src/assets/img/picture1.jpg',
  '/src/assets/img/picture2.jpg',
  '/src/assets/img/picture3.jpg',
  '/src/assets/img/picture4.jpg',
  '/src/assets/img/picture5.jpg',
]
</script>

<style scoped>
/* 🔥 固定轮播图外层高度（你自己改高度） */
.carousel-wrapper {
  width: 100%;
  height: 360px; /* 这里控制轮播图大小！！！ */
}

/* 🔥 让 Element 轮播图填满高度 */
:deep(.el-carousel),
:deep(.el-carousel__container) {
  width: 100%;
  height: 100% !important;
}

/* 🔥 图片填满、不变形、自适应 */
.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
</style>