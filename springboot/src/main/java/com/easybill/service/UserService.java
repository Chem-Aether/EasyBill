package com.easybill.service;

import com.easybill.entity.User;
import com.easybill.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<User> findAll() {
        return userMapper.findAll();
    }

    // 登录逻辑
    public User login(String account, String password) {
        // 根据账号查用户（返回 entity）
        User user = userMapper.selectByAccount(account);

        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        // 密码比对
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    public User register(User user) {
        // 判断账号是否已存在
        User exist = userMapper.selectByAccount(user.getAccount());
        if (exist != null) {
            throw new RuntimeException("账号已存在");
        }

        // 插入数据库
        userMapper.insert(user);

        return user;
    }
}
