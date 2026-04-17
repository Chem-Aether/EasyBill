import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建实例
const service = axios.create({
    baseURL: "http://127.0.0.1:8081",
    timeout: 10000
})

// =================== 请求拦截器 ===================
service.interceptors.request.use(
    (config) => {
        // 携带 token
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.token = token
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// =================== 响应拦截器 ===================
service.interceptors.response.use(
    (response) => {
        // 如果标记了 fullResponse，直接返回完整对象
        if (response.config.fullResponse) {
            return response;
        }
        // 其他接口照常返回 data，不动！
        return response.data;
    },
    (error) => {
        // 非 2xx 进入这里：统一处理全局异常
        const res = error.response

        // 无网络 / 服务器崩了
        if (!res) {
            ElMessage.error('网络异常或服务器未启动')
            return Promise.reject(error)
        }

        // 通用状态码处理（企业标准）
        switch (res.status) {
            case 401:
                // token 过期 / 未登录：清空并跳登录
                ElMessage.error('登录已过期，请重新登录')
                localStorage.removeItem('token')
                router.push('/login')
                break

            case 403:
                ElMessage.error('无权限访问')
                break

            case 404:
                ElMessage.error('接口不存在')
                break

            case 500:
                ElMessage.error('服务器异常')
                break

            default:
                break
        }

        // 继续把错误抛给页面 catch
        return Promise.reject(error)
    }
)

export default service