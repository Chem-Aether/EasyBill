<template>
    <div class="register-container">
      <h2 class="register-title">注册</h2>
      <el-form :model="registerForm" @submit.prevent="handleRegister">
        <el-form-item label="用户名">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" class="register-button">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-link">
        已有账号？<el-link type="primary" :underline="false" @click="goToLogin">
          去登录
        </el-link>
      </div>
    </div>
</template>
  
<script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessage } from 'element-plus';
  import { register } from '@/api/auth';
  
  const router = useRouter();
  const registerForm = ref({
    username: '',
    password: '',
    confirmPassword: '',
  });
  
  const handleRegister = async () => {
    if (!registerForm.value.username) {
      ElMessage.warning('用户名不能为空');
      return;
    }
    if (!registerForm.value.password) {
      ElMessage.warning('密码不能为空');
      return;
    }
    if (registerForm.value.password !== registerForm.value.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致！');
      return;
    }

    try {
      const res = await register({
        account: registerForm.value.username,
        password: registerForm.value.password,
      });
      ElMessage.success(res.data.msg || '注册成功');
      router.push('/login'); // 跳转到登录页面
    } catch (error) {
      ElMessage.error(error.response?.data?.msg || '注册失败，请稍后重试');
    }
  };
  
  const goToLogin = () => {
    router.push('/login'); // 跳转到登录页面
  };
</script>
  
<style scoped>
  .register-container {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 10px;
    background-color: rgba(255, 255, 255, 0.9);
  }
  
  .register-title {
    text-align: center;
    color: #ff6b6b;
    margin-bottom: 1.5rem;
  }
  
  .register-button {
    width: 100%;
    background-color: #ff6b6b;
    border-color: #ff6b6b;
    color: white;
  }
  
  .register-button:hover {
    background-color: #ff4757;
    border-color: #ff4757;
  }
  
  .login-link {
    text-align: center;
    margin-top: 1rem;
  }
  
  .login-link .el-link {
    color: #ff6b6b;
  }
  
  .login-link .el-link:hover {
    color: #ff4757;
  }
</style>