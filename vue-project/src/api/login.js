import request from '@/utils/request'; // 引入封装的 axios 实例

// 登录接口
export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data: data,
  });
};