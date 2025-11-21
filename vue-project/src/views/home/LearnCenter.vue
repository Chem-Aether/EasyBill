<template>
    这是学习大厅
    <button @click="aaa">aaaaa</button>
</template>


<script setup>
  import { getUsers } from '@/api/message';

const aaa = async () => {
    try {
        // 发起验证码请求
        const res = await getUsers();
        // 根据返回数据弹出提示
        if (res.data.code === '200') {
          console.log(res.data.code, "succe");
        }
        else {
          console.log(res.data.code)

          
          return;
        }
      } catch (error) {
        ElMessage.error('验证码错误，请稍后重试');

        return;
      }
}

</script>
