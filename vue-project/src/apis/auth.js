import request from '@/utils/request'; // 引入封装的 axios 实例

// 获取验证码接口
export const captcha = () => {
    return request({
        url: '/captcha/image',
        method: 'get',
        responseType: 'blob',
        fullResponse: true,
    });
};

export const validate = (userCaptcha, captchaKey) => {
    return request({
        url: '/captcha/validate?userCaptcha=' + userCaptcha + '&captchaKey='+ captchaKey ,
        method: 'get',
    });
}

// 登录接口
export const login = (data) => {
    return request({
        url: '/user/login',
        method: 'post',
        data: data,
        fullResponse: true,
    });
};

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
