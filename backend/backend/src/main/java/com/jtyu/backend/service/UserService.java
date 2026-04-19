package com.jtyu.backend.service;

import com.jtyu.backend.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    // 登录
    Map<String, Object> login(String email, String password);

    // 注册
    User register(String email, String username, String password);

    // 获取用户信息
    Map<String, Object> getUserById(Integer userId);

    // 获取当前用户信息（通过ID）
    Map<String, Object> getCurrentUser(Integer userId);

    // 获取用户列表（分页，支持关键词）
    Map<String, Object> getUserList(String keyword, Integer page, Integer pageSize);

    // 更新用户资料（用户名、头像、简介）
    boolean updateProfile(Integer userId, String username, String avatar, String bio);

    // 修改密码
    boolean changePassword(Integer userId, String oldPassword, String newPassword, String confirmPassword);

    // 获取用户动态数量
    Long getPostCount(Integer userId);

    // 获取用户收到的点赞总数
    Integer getTotalLikeCount(Integer userId);

    // 获取关注列表
    Map<String, Object> getFollowingList(Integer userId, Integer page, Integer pageSize);

    // 获取粉丝列表
    Map<String, Object> getFollowersList(Integer userId, Integer page, Integer pageSize);

    // 获取用户的动态列表
    Map<String, Object> getUserPosts(Integer userId,Integer currentUserId, Integer page, Integer pageSize);

    // 获取用户的收藏列表
    Map<String, Object> getUserFavorites(Integer userId, Integer page, Integer pageSize);

    // 获取用户的订单列表
    Map<String, Object> getUserOrders(Integer userId, String status, Integer page, Integer pageSize);
}
