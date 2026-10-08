package com.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.entity.User;
import com.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Autowired
    private UserMapper userMapper;

    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    // 登录逻辑
    public User login(String account, String password) {
        if (account == null || account.isBlank()) {
            throw new RuntimeException("账号不能为空");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("密码不能为空");
        }

        User user = userMapper.selectByAccount(account);
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }
    // 注册
    public User register(User user) {
        if (user == null) {
            throw new RuntimeException("请求数据不能为空");
        }
        if (user.getAccount() == null || user.getAccount().isBlank()) {
            throw new RuntimeException("账号不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("密码不能为空");
        }

        User exist = userMapper.selectOne(new QueryWrapper<User>().eq("account", user.getAccount()));
        if (exist != null) {
            throw new RuntimeException("账号已存在");
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("2");
        }

        userMapper.insert(user);
        return user;
    }
    // 修改密码
    public void resetPassword(String account, String newPassword) {
        if (account == null || account.isBlank()) {
            throw new RuntimeException("账号不能为空");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new RuntimeException("新密码不能为空");
        }

        User user = userMapper.selectByAccount(account);
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        userMapper.updatePasswordByAccount(account, newPassword);
    }
}
