package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.UserMapper;
import com.jtyu.backend.model.User;
import com.jtyu.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.findById(userId);
    }

    @Override
    public int createUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.update(user);
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return userMapper.deleteById(userId) > 0;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public int register(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            return 0;
        }
        // 检查邮箱是否已存在
        existUser = userMapper.findByEmail(user.getEmail());
        if (existUser != null) {
            return 0;
        }
        if (user.getAvatar() == null) {
            user.setAvatar("default.jpg");
        }
        return userMapper.insert(user);
    }
}
