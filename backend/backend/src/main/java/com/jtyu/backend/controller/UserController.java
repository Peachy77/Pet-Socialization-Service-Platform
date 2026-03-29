package com.jtyu.backend.controller;


import com.jtyu.backend.model.Result;
import com.jtyu.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

//    // POST /api/auth/login - 用户登录
//    @PostMapping("/api/auth/login")
//    public Result login(@RequestBody Map<String, String> loginData) {
//        String username = loginData.get("username");
//        String password = loginData.get("password");
//
//        if (StringUtils.isAnyBlank(username, password)) {
//            return Result.error("用户名或密码不能为空");
//        }
//
//        User user = userService.login(username, password);
//        if (user == null) {
//            return Result.error("用户名或密码错误");
//        }
//
//        // 生成token
//        String token = jwtUtil.generateToken(user);
//        return Result.success(token);
//    }

//    // POST /api/auth/register - 用户注册
//    @PostMapping("/api/auth/register")
//    public Result register(@RequestBody User user) {
//        int result = userService.register(user);
//        if (result > 0) {
//            return Result.success("注册成功");
//        }
//        return Result.error("注册失败");
//    }

    // GET /api/users - 获取用户列表
    @GetMapping("/api/users")
    public Result getUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    // POST /api/users - 创建用户
    @PostMapping("/api/users")
    public Result createUser(@RequestBody User user) {
        int result = userService.createUser(user);
        if (result > 0) {
            return Result.success("创建用户成功");
        }
        return Result.error("创建用户失败");
    }

    // GET /api/users/{id} - 获取用户信息
    @GetMapping("/api/users/{id}")
    public Result getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    // PUT /api/users/{id} - 更新用户信息
    @PutMapping("/api/users/{id}")
    public Result updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setUserId(id);
        int result = userService.updateUser(user);
        if (result > 0) {
            return Result.success("更新用户成功");
        }
        return Result.error("用户不存在或更新失败");
    }

    // DELETE /api/users/{id} - 删除用户
    @DeleteMapping("/api/users/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return Result.success("删除用户成功");
        }
        return Result.error("用户不存在或已被删除");
    }

}
