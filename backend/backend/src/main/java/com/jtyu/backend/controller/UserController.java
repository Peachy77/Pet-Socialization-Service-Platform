package com.jtyu.backend.controller;


import com.jtyu.backend.model.Result;
import com.jtyu.backend.model.User;
import com.jtyu.backend.service.FollowService;
import com.jtyu.backend.service.UserService;
import com.jtyu.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    // POST /users/login - 用户登录
    @PostMapping("/users/login")
    public Result login(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String password = params.get("password");

        if (email == null || password == null) {
            return Result.error("邮箱和密码不能为空");
        }

        Map<String, Object> user = userService.login(email, password);
        if (user == null) {
            return Result.error("邮箱或密码错误");
        }

        // 生成 token
        Integer userId = (Integer) user.get("userId");
        System.out.println("login生成token userId: " + userId);
        String token = JwtUtil.generateToken(userId, email);

        // 构建返回结果（包含 token）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.get("userId"));
        result.put("username", user.get("username"));
        result.put("email", user.get("email"));
        result.put("avatar", user.get("avatar"));
        result.put("bio", user.get("bio"));
        result.put("followerCount", user.get("followerCount"));
        result.put("followingCount", user.get("followingCount"));

        return Result.success(result);
    }


    // POST /users/register - 用户注册
    @PostMapping("/users/register")
    public Result register(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String username = params.get("username");
        String password = params.get("password");

        if (email == null || username == null || password == null) {
            return Result.error("邮箱、用户名和密码不能为空");
        }

        User user = userService.register(email, username, password);
        if (user == null) {
            return Result.error("邮箱已被注册");
        }

        // 生成 token
        String token = JwtUtil.generateToken(user.getUserId(), email);

        // 构建返回结果（包含 token）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("avatar", user.getAvatar());
        result.put("bio", user.getBio());
        result.put("followerCount", user.getFollowerCount());
        result.put("followingCount", user.getFollowingCount());
        return Result.success(user);
    }

    // GET /users - 获取用户列表
    @GetMapping("/users")
    public Result getUsers(@RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "20") Integer pageSize,
                           @RequestParam(required = false) String keyword) {
        Map<String, Object> result = userService.getUserList(keyword, page, pageSize);
        return Result.success(result);
    }

    // GET /users/{id} - 获取指定用户信息
    @GetMapping("/users/{id}")
    public Result getUser(@PathVariable Integer id) {
        Map<String, Object> user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    // GET /users/me - 获取当前用户信息（需要token）
    @GetMapping("/users/me")
    public Result getCurrentUser(@RequestAttribute Integer currentUserId) {
        Map<String, Object> user = userService.getCurrentUser(currentUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    // PUT /users/me - 更新当前用户资料
    @PutMapping("/users/me")
    public Result updateCurrentUser(@RequestAttribute Integer currentUserId,
                                    @RequestBody Map<String, String> params) {
        String username = params.get("username");
        String avatar = params.get("avatar");
        String bio = params.get("bio");

        boolean success = userService.updateProfile(currentUserId, username, avatar, bio);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    // POST /users/follow/{targetUserId} - 关注用户
    @PostMapping("/users/follow/{targetUserId}")
    public Result followUser(@RequestAttribute Integer currentUserId,
                             @PathVariable Integer targetUserId) {
        boolean success = followService.follow(currentUserId, targetUserId);
        if (success) {
            return Result.success("关注成功");
        }
        return Result.error("关注失败");
    }

    // DELETE /users/follow/{targetUserId} - 取消关注
    @DeleteMapping("/users/follow/{targetUserId}")
    public Result unfollowUser(@RequestAttribute Integer currentUserId,
                               @PathVariable Integer targetUserId) {
        boolean success = followService.unfollow(currentUserId, targetUserId);
        if (success) {
            return Result.success("取消关注成功");
        }
        return Result.error("取消关注失败");
    }

    // GET /users/me/following - 获取我关注的人列表
    @GetMapping("/users/me/following")
    public Result getFollowing(@RequestAttribute Integer currentUserId,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getFollowingList(currentUserId, page, pageSize);
        return Result.success(result);
    }

    // GET /users/me/followers - 获取我的粉丝列表
    @GetMapping("/users/me/followers")
    public Result getFollowers(@RequestAttribute Integer currentUserId,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getFollowersList(currentUserId, page, pageSize);
        return Result.success(result);
    }

    // GET /users/me/posts - 获取我的动态列表
    @GetMapping("/users/me/posts")
    public Result getMyPosts(@RequestAttribute Integer currentUserId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getUserPosts(currentUserId, page, pageSize);
        return Result.success(result);
    }

    // GET /users/me/favorites - 获取我的收藏列表
    @GetMapping("/users/me/favorites")
    public Result getMyFavorites(@RequestAttribute Integer currentUserId,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getUserFavorites(currentUserId, page, pageSize);
        return Result.success(result);
    }

    // GET /users/me/orders - 获取我的订单列表
    @GetMapping("/users/me/orders")
    public Result getMyOrders(@RequestAttribute Integer currentUserId,
                              @RequestParam(required = false) String status,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getUserOrders(currentUserId, status, page, pageSize);
        return Result.success(result);
    }

    // GET /users/{userId}/posts - 获取指定用户的动态列表
    @GetMapping("/users/{userId}/posts")
    public Result getUserPosts(@PathVariable Integer userId,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getUserPosts(userId, page, pageSize);
        return Result.success(result);
    }

    // GET /users/{userId}/favorites - 获取指定用户的收藏列表
    @GetMapping("/users/{userId}/favorites")
    public Result getUserFavorites(@PathVariable Integer userId,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getUserFavorites(userId, page, pageSize);
        return Result.success(result);
    }

    // GET /users/{userId}/orders - 获取指定用户的订单列表
    @GetMapping("/users/{userId}/orders")
    public Result getUserOrders(@PathVariable Integer userId,
                                @RequestParam(required = false) String status,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getUserOrders(userId, status, page, pageSize);
        return Result.success(result);
    }

    // GET /users/{userId}/following - 获取指定用户的关注列表
    @GetMapping("/users/{userId}/following")
    public Result getUserFollowing(@PathVariable Integer userId,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getFollowingList(userId, page, pageSize);
        return Result.success(result);
    }

    // GET /users/{userId}/followers - 获取指定用户的粉丝列表
    @GetMapping("/users/{userId}/followers")
    public Result getUserFollowers(@PathVariable Integer userId,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = userService.getFollowersList(userId, page, pageSize);
        return Result.success(result);
    }
}
