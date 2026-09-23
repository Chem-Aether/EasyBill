import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const normalizeBaseUrl = (value, name) => {
  const url = value?.trim().replace(/\/+$/, '')
  if (!url) throw new Error(`Missing environment variable: ${name}`)
  return url
}

export const API_BASE_URL = normalizeBaseUrl(
  import.meta.env.VITE_API_BASE_URL,
  'VITE_API_BASE_URL',
)

export const MAP_BASE_URL = normalizeBaseUrl(
  import.meta.env.VITE_MAP_BASE_URL,
  'VITE_MAP_BASE_URL',
)

const attachInterceptors = (service) => {
  service.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token')
      if (token) config.headers.token = token
      return config
    },
    error => Promise.reject(error),
  )

  service.interceptors.response.use(
    response => response.config.fullResponse ? response : response.data,
    (error) => {
      const response = error.response
      if (!response) {
        ElMessage.error('网络异常或服务器未启动')
        return Promise.reject(error)
      }

      switch (response.status) {
        case 401:
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

      return Promise.reject(error)
    },
  )

  return service
}

export const createRequest = baseURL => attachInterceptors(axios.create({
  baseURL,
  timeout: 10000,
}))

export const apiRequest = createRequest(API_BASE_URL)
export const mapRequest = createRequest(MAP_BASE_URL)

export default apiRequest
