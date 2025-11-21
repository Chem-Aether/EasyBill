import request from '@/utils/request'; // 引入封装的 axios 实例

// 获取验证码接口
export const captcha = () => {
    return request({
        url: '/captcha/image',
        method: 'get',
        responseType: 'blob',
    });
};

export const validate = (userCaptcha, captchaKey) => {
    return request({
        url: '/captcha/validate?userCaptcha=' + userCaptcha + '&captchaKey='+ captchaKey ,
        method: 'get',
        });
}