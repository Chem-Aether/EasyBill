<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-container class="content-container">
        <!-- 左侧菜单（账单系统专用） -->
        <el-aside width="220px" style="height: 100vh; background: #390062;">
          <el-menu
              default-active="/bill/home"
              class="el-menu-vertical-demo"
              unique-opened
              background-color="#390062"
              text-color="#fff"
              active-text-color="#ffd04b"
              style="height: 100%; display: flex; flex-direction: column;"
              router
          >
            <!-- 头像区域 -->
            <div class="avatar-box">
              <el-avatar :size="80" :src="avatarUrl" />
              <div class="username">账单管理</div>
            </div>

            <!-- 账单功能菜单 -->
            <el-menu-item index="/bill/home">
              <i class="el-icon-data-board"></i>
              <span>数据统计</span>
            </el-menu-item>

            <el-menu-item index="/bill/add">
              <i class="el-icon-circle-plus"></i>
              <span>记一笔</span>
            </el-menu-item>

            <el-menu-item index="/bill/query">
              <i class="el-icon-search"></i>
              <span>账单查询</span>
            </el-menu-item>

            <el-menu-item index="/bill/classify">
              <i class="el-icon-menu"></i>
              <span>分类统计</span>
            </el-menu-item>

            <el-menu-item index="/bill/account">
              <i class="el-icon-wallet"></i>
              <span>账户管理</span>
            </el-menu-item>

            <!-- 撑开底部 -->
            <div style="flex: 1;"></div>

            <!-- 回到系统主页 -->
            <el-menu-item index="/home" style="border-top: 1px solid #551a8b;">
              <i class="el-icon-house"></i>
              <span>返回主页</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 右侧内容：嵌套账单子路由 -->
        <el-main class="main-content">
          <div class="page-container">
            <!-- 路由出口：显示子页面 -->
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
/* 核心修复：隐藏滚动条，但保留滚动功能 */
.common-layout ::-webkit-scrollbar {
  display: none;
}
.common-layout {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* 布局样式 */
.common-layout {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.layout-container {
  height: 100%;
}

.content-container {
  display: flex;
  height: 100%;
}

/* 左侧菜单 */
.avatar-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
  gap: 10px;
}

.username {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

/* 主内容 */
.main-content {
  background: #f5f7fa;
  overflow: auto;
  padding: 30px;
  height: 100%;
}

.page-container {
  width: 100%;
  height: 100%;
}
</style>