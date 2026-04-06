<template>
  <div class="forgot-container">
    <div class="forgot-box">
      <h2 class="forgot-title">重置密码</h2>
      <el-form :model="forgotForm" @submit.prevent="handleSubmit">
        <el-form-item label="账号">
          <el-input
            v-model="forgotForm.username"
            placeholder="请输入账号"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input
            v-model="forgotForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="forgotForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-container">
            <el-input
              v-model="forgotForm.captcha"
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
          <el-button type="primary" native-type="submit" class="forgot-button">
            重置密码
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-link">
        已有账号？<el-link type="primary" :underline="false" @click="goToLogin">
          去登录
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { captcha } from '@/api/captcha';
import { forgotPassword } from '@/api/user';

const router = useRouter();
const forgotForm = ref({
  username: '',
  newPassword: '',
  confirmPassword: '',
  captcha: '',
});
const captchaKey = ref('');
const captchaImage = ref('');

const loadCaptcha = async () => {
  try {
    const response = await captcha();
    captchaImage.value = URL.createObjectURL(new Blob([response.data]));
    captchaKey.value = response.headers['captcha-key'];
  } catch (error) {
    console.error('加载验证码失败', error);
  }
};

const handleSubmit = async () => {
  if (!forgotForm.value.username) {
    ElMessage.warning('账号不能为空');
    return;
  }
  if (!forgotForm.value.newPassword) {
    ElMessage.warning('请输入新密码');
    return;
  }
  if (forgotForm.value.newPassword !== forgotForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }
  if (!forgotForm.value.captcha) {
    ElMessage.warning('验证码不能为空');
    return;
  }

  try {
    const res = await forgotPassword({
      account: forgotForm.value.username,
      newPassword: forgotForm.value.newPassword,
      captchaCode: forgotForm.value.captcha,
      captchaKey: captchaKey.value,
    });
    ElMessage.success(res.data.msg || '密码已重置');
    router.push('/login');
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '重置失败，请稍后重试');
    forgotForm.value.captcha = '';
    loadCaptcha();
  }
};

const goToLogin = () => {
  router.push('/login');
};

onMounted(() => {
  loadCaptcha();
});
</script>

<style scoped>
.forgot-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url('/background.png');
  background-size: cover;
  background-position: center;
}

.forgot-box {
  width: 380px;
  padding: 30px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.12);
}

.forgot-title {
  text-align: center;
  color: #ff6b6b;
  margin-bottom: 24px;
  font-size: 28px;
}

.captcha-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.captcha-image {
  width: 110px;
  height: 42px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
}

.forgot-button {
  width: 100%;
  background-color: #ff6b6b;
  border-color: #ff6b6b;
  color: white;
}

.forgot-button:hover {
  background-color: #ff4757;
  border-color: #ff4757;
}

.login-link {
  text-align: center;
  margin-top: 12px;
}

.login-link .el-link {
  color: #ff6b6b;
}

.login-link .el-link:hover {
  color: #ff4757;
}
</style>
