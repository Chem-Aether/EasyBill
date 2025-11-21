<template>
    <el-card class="user-profile">
      <h2>用户基本信息</h2>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ user.user_id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ user.user_name }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ user.age }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ user.gender }}</el-descriptions-item>
        <el-descriptions-item label="民族">{{ user.ethnicity }}</el-descriptions-item>
      </el-descriptions>
      <el-decriptions :column="2" border>
        <el-descriptions-item label="年级">{{ user.grade }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ user.class_name }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ user.education }}</el-descriptions-item>
        <el-descriptions-item label="阶段">{{ user.stage }}</el-descriptions-item>
        <el-descriptions-item label="组别">{{ user.group }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ user.role }}</el-descriptions-item>
      </el-decriptions>
    </el-card>
</template>


<script setup>
import { column } from 'element-plus/es/components/table-v2/src/common';
import { ref,onMounted } from 'vue';

const user = ref({
      user_id: '',
      user_name: '',
      age: '',
      gender: '',
      ethnicity: '',
      grade: '',
      class_name: '',
      education: '',
      stage: '',
      group: '',
      role: '',
    })
user.value = {
          user_id: '12345',
          user_name: '王祎恒',
          age: 21,
          gender: '男',
          ethnicity: '汉族',
          grade: '2021级',
          class_name: '药学专业本科2021级团支部',
          education: '本科',
          stage: '在读',
          group: '药学专业',
          role: '团员',
    };


    

</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>