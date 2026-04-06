import request from '@/utils/request';

export const getCategories = () => {
  return request({
    url: '/bill/categories',
    method: 'get',
  });
};

export const addCategory = (data) => {
  return request({
    url: '/bill/category',
    method: 'post',
    data,
  });
};

export const updateCategory = (data) => {
  return request({
    url: '/bill/category',
    method: 'put',
    data,
  });
};

export const removeCategory = (cateId) => {
  return request({
    url: `/bill/category/${cateId}`,
    method: 'delete',
  });
};

export const getAccounts = (userId) => {
  return request({
    url: '/bill/accounts',
    method: 'get',
    params: { userId },
  });
};

export const getBillRecords = (params) => {
  return request({
    url: '/bill/records',
    method: 'get',
    params,
  });
};

export const createBillRecord = (data) => {
  return request({
    url: '/bill/record',
    method: 'post',
    data,
  });
};

export const getCategoryStatistics = (params) => {
  return request({
    url: '/bill/statistics/categories',
    method: 'get',
    params,
  });
};
