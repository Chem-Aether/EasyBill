<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-container class="content-container">
        <!-- 左侧菜单 -->
        <el-aside width="220px" style="height: 100vh; background: #390062;">
          <el-menu
              default-active="/user/profile"
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
              <div class="username">Qi GuoHao</div>
            </div>

            <!-- 用户菜单 -->
            <el-menu-item index="/user/profile">
              <i class="el-icon-user"></i>
              <span>个人资料</span>
            </el-menu-item>

            <el-menu-item index="/user/info">
              <i class="el-icon-setting"></i>
              <span>账户设置</span>
            </el-menu-item>

            <el-menu-item index="/user/reset-pwd">
              <i class="el-icon-lock"></i>
              <span>修改密码</span>
            </el-menu-item>

            <!-- 撑开底部 -->
            <div style="flex: 1;"></div>

            <!-- 退出登录 -->
            <el-menu-item index="/exit" style="border-top: 1px solid #551a8b;">
              <i class="el-icon-switch-button"></i>
              <span>退出登录</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 右侧主内容 -->
        <el-main class="main-content">
          <div class="page-container">
            <h2 class="page-title">欢迎使用个人管理系统</h2>

            <!-- 四个功能卡片 -->
            <div class="card-grid">
              <el-card
                  shadow="hover"
                  class="card-item"
                  @click="$router.push('/travel')"
              >
                <div class="card-icon">✈️</div>
                <h3>旅行管理</h3>
                <p>机票、火车票、行程管理</p>
              </el-card>

              <el-card
                  shadow="hover"
                  class="card-item"
                  @click="$router.push('/bill')"
              >
                <div class="card-icon">💰</div>
                <h3>账单管理</h3>
                <p>记账、统计、分类、报表</p>
              </el-card>

              <el-card
                  shadow="hover"
                  class="card-item"
                  @click="$router.push('/diary')"
              >
                <div class="card-icon">📔</div>
                <h3>日记管理</h3>
                <p>记录生活、心情、随笔</p>
              </el-card>

              <el-card
                  shadow="hover"
                  class="card-item"
                  @click="$router.push('/movie')"
              >
                <div class="card-icon">🎬</div>
                <h3>影视剧管理</h3>
                <p>追剧、观影、收藏记录</p>
              </el-card>
            </div>
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
.common-layout ::-webkit-scrollbar {
  display: none;
}
.common-layout {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

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
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  margin-bottom: 30px;
  color: #333;
}

/* 卡片布局 */
.card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
}

.card-item {
  border-radius: 15px;
  text-align: center;
  padding: 40px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.card-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.card-icon {
  font-size: 50px;
  margin-bottom: 20px;
}

.card-item h3 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #333;
}

.card-item p {
  color: #999;
  font-size: 14px;
}
</style>