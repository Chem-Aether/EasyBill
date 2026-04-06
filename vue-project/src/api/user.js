import request from '@/utils/request';

export const register = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data,
  });
};

export const forgotPassword = (data) => {
  return request({
    url: '/user/forgot-password',
    method: 'post',
    data,
  });
};
