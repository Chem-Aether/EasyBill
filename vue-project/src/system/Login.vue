<template>
    <div class="login-container">
      <div class="login-background"></div>
      <div class="login-box">
        <h2 class="login-title">登录</h2>
        <el-form :model="loginForm" @submit.prevent="handleLogin">
          <el-form-item label="账　号">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入账号"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item label="密　码">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item label="验证码">
            <div class="captcha-container">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                prefix-icon="Key"
              />
              <img
                :src="captchaImage"
                alt="验证码"
                class="captcha-image"
                @click="loadCaptcha"
              />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" native-type="submit" class="login-button">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-links">
          <el-link type="danger" :underline="false" @click="handleForgotPassword">
            忘记密码？
          </el-link>
          <el-link type="danger" :underline="false" @click="handleRegister">
            注册
          </el-link>
        </div>
      </div>
    </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import { ElMessage } from 'element-plus';
  import { useRouter } from 'vue-router'; // 引入 useRouter

  // API接口
  import { captcha, validate, login } from '@/apis/auth.js';

  onMounted(() => {
      loadCaptcha();
    });

  const router = useRouter(); // 获取路由实例

  // 表单数据
  const loginForm = ref({
    username: '',
    password: '',
    captcha: '',
  });
  
  const captchaKey = ref();

  // 定义验证码图片的 URL
  const captchaImage = ref('');

  // 加载验证码
  const loadCaptcha = async () => {
    try {
      const response = await captcha(); // 调用验证码接口
      // 将二进制数据转换为 URL
      const imageUrl = URL.createObjectURL(new Blob([response.data]));
      captchaImage.value = imageUrl; // 更新验证码图片
      captchaKey.value = response.headers['captcha-key']
      console.log(captchaKey.value)
    } catch (error) {
      console.error('加载验证码失败', error);
    }
  };


  // 处理登录
  const handleLogin = async () => {
    const { username, password, captcha } = loginForm.value;

    // 表单验证
    if (!username) {
      ElMessage.warning('账号不能为空');
      return;
    }
    if (!password) {
      ElMessage.warning('密码不能为空');
      return;
    }
    if (!captcha) {
      ElMessage.warning('验证码不能为空');
      return;
    }


    try {
      // 这里如果验证码错误，会直接抛出异常进入 catch
      await validate(captcha, captchaKey.value);
      // 能走到这里 = 验证码正确
    } catch (error) {
      // 后端返回 400 会进入这里
      ElMessage.error(error.response?.data?.msg || '验证码错误！');
      loadCaptcha();
      loginForm.value.captcha = '';
      return;
    }


    const loginData = {
      account: username,
      password: password,
    };

    try {
      const res = await login(loginData);
      ElMessage.success(res.data.msg || '登录成功');
      const token = res.headers.token;
      localStorage.setItem('token', token);
      router.push('/home');

    } catch (error) {
      ElMessage.error(error.response?.data?.msg || '登录失败，请稍后重试');
      loadCaptcha();
      loginForm.value.captcha = '';
    }
  };
  
  // 忘记密码
  const handleForgotPassword = () => {
    router.push('/forgot-password');
  };
  
  // 注册
  const handleRegister = () => {
    router.push('/register');
  };
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100vh;
  background-image: url('/background.png');
  background-size: cover;
  background-position: center;
}

.login-box {
  background: rgba(255, 255, 255, 0.9);
  padding: 2.5rem;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 350px;
  margin-right: 10%;
}

.login-title {
  text-align: center;
  color: #ff6b6b;
  margin-bottom: 1.5rem;
}

:deep(.el-input__prefix) {
  display: none;
}

.form-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.form-item :deep(.el-form-item__label) {
  text-align: left;
  margin-bottom: 0.5rem;
  color: #ff6b6b;
}

.form-item :deep(.el-input) {
  width: 100%;
}

.captcha-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 100px;
  height: 40px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.login-button {
  width: 100%;
  background-color: #ff6b6b;
  border-color: #ff6b6b;
  color: white;
}

.login-button:hover {
  background-color: #ff4757;
  border-color: #ff4757;
}

.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
}

.login-links .el-link {
  color: #ff6b6b;
}

.login-links .el-link:hover {
  color: #ff4757;
}
</style>

