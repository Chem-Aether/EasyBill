import axios from 'axios';
import { ElMessage } from 'element-plus'; // 用于显示提示消息

// 创建 axios 实例
const service = axios.create({
  baseURL: "http://127.0.0.1:8081", // 从环境变量中读取基础 URL
  timeout: 10000, // 请求超时时间

  headers: {'token': ''},
});

// 添加请求拦截器
service.interceptors.request.use(
  (config) => {
    // 排除登录接口
    if (config.url !== '/login') {
      // 从 localStorage 中获取 token
      const token = localStorage.getItem('token');
      if (token) {
        config.headers['token'] = `${token}`; // 在请求头中携带 token
      }
    }
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
service.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    return response;
  }, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  });

// 导出 axios 实例
export default service;