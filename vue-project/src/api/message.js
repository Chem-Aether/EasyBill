import request from '@/utils/request';

// 获取用户列表
export const getUsers = () => {
  return request({
    url: '/user/getall',
    method: 'get',
  });
};