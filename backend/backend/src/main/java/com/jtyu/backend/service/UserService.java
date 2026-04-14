package com.jtyu.backend.service;

import com.jtyu.backend.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Integer userId);

    int createUser(User user);

    int updateUser(User user);

    boolean deleteUser(Integer userId);

    User login(String username, String password);

    int register(User user);
}
