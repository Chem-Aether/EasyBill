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
}
