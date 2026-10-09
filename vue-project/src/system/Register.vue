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
        <el-form-item label="验证码">
          <div class="captcha-row">
            <el-input v-model="registerForm.captcha" placeholder="请输入验证码" />
            <img :src="captchaImage" alt="验证码，点击刷新" @click="loadCaptcha" />
          </div>
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
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessage } from 'element-plus';
  import { captcha, register } from '@/apis/auth.js';
  
  const router = useRouter();
  const registerForm = ref({
    username: '',
    password: '',
    confirmPassword: '',
    captcha: '',
  });
  const captchaImage = ref('');
  const captchaKey = ref('');

  const loadCaptcha = async () => {
    try {
      const response = await captcha();
      captchaImage.value = URL.createObjectURL(new Blob([response.data]));
      captchaKey.value = response.headers['captcha-key'];
    } catch {
      ElMessage.error('验证码加载失败');
    }
  };
  onMounted(loadCaptcha);
  
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
    if (!registerForm.value.captcha) {
      ElMessage.warning('请输入验证码');
      return;
    }

    try {
      const res = await register({
        account: registerForm.value.username,
        password: registerForm.value.password,
        captchaCode: registerForm.value.captcha,
        captchaKey: captchaKey.value,
      });
      ElMessage.success(res.data.msg || '注册成功');
      router.push('/login'); // 跳转到登录页面
    } catch (error) {
      ElMessage.error(error.response?.data?.msg || '注册失败，请稍后重试');
      registerForm.value.captcha = '';
      loadCaptcha();
    }
  };
  
  const goToLogin = () => {
    router.push('/login'); // 跳转到登录页面
  };
</script>

<style scoped>
.captcha-row { display:flex; width:100%; align-items:center; gap:10px; }
.captcha-row img { width:120px; height:40px; object-fit:cover; cursor:pointer; }
</style>
  
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
