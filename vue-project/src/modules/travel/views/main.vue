<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-container class="content-container">
        <!-- 左侧菜单 -->
        <el-aside width="220px" style="height: 100vh; background: #390062;">
          <el-menu
              default-active="/travel/admin/trainTicket"
              class="el-menu-vertical-demo"
              unique-opened
              background-color="#390062"
              text-color="#fff"
              active-text-color="#ffd04b"
              style="height: 100%; display: flex; flex-direction: column;"
              router
          >
            <div class="avatar-box">
              <el-avatar :size="80" :src="avatarUrl" />
              <div class="username">旅行管理</div>
            </div>



            <el-menu-item index="/travel/admin/trainTicket">
              <i class="el-icon-tickets"></i>
              <span>火车票管理</span>
            </el-menu-item>

            <el-menu-item index="/travel/admin/flightTicket">
              <i class="el-icon-plane"></i>
              <span>飞机票管理</span>
            </el-menu-item>

            <div style="flex: 1;"></div>

            <div style="padding:10px;text-align:center;color:#fff;font-size:12px;font-size: medium">
              <router-link to="/home" style="color:#fff;margin:0 4px">首页</router-link>
              |
              <router-link to="/travel" style="color:#fff;margin:0 4px">大屏</router-link>
            </div>
          </el-menu>
        </el-aside>

        <!-- 右侧子路由 -->
        <el-main class="main-content">
          <div class="page-container">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const avatarUrl = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
</script>

<style scoped>
.common-layout ::-webkit-scrollbar { display: none; }
.common-layout { -ms-overflow-style: none; scrollbar-width: none; }

.common-layout { width: 100vw; height: 100vh; overflow: hidden; }
.layout-container { height: 100%; }
.content-container { display: flex; height: 100%; }

.avatar-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
  gap: 10px;
}
.username { color: #fff; font-size: 18px; font-weight: bold; }

.main-content {
  background: #f5f7fa;
  overflow: auto;
  padding: 30px;
  height: 100%;
}
.page-container { width: 100%; height: 100%; }
</style>