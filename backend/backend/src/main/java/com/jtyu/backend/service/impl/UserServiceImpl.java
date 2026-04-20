package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.*;
import com.jtyu.backend.model.User;
import com.jtyu.backend.service.UserService;
import com.jtyu.backend.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private AppointmentOrderMapper appointmentOrderMapper;

    @Autowired
    private LikeMapper likeMapper;

    //private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Map<String, Object> login(String email, String password) {
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            return null;
        }

//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            return null;
//        }


        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("avatar", user.getAvatar());
        result.put("bio", user.getBio());
        result.put("followerCount", user.getFollowerCount());
        result.put("followingCount", user.getFollowingCount());

        return result;
    }

    @Override
    @Transactional
    public User register(String email, String username, String password) {
        // 检查邮箱是否已存在
        User existing = userMapper.selectByEmail(email);
        if (existing != null) {
            return null;
        }

        //String encodedPassword = passwordEncoder.encode(password);

        String encodedPassword = PasswordUtil.encode(password);
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setAvatar("http://localhost:8080/uploads/cat.png");
        user.setBio("这个人很神秘，什么介绍也没有~");
        user.setFollowerCount(0);
        user.setFollowingCount(0);

        int rows = userMapper.insert(user);
        if (rows > 0) {
            return user;
        }
        return null;
    }

    @Override
    public Map<String, Object> getUserById(Integer userId) {
        Map<String, Object> user = userMapper.selectSimpleById(userId);
        if (user != null) {
            Integer totalLikeCount = getTotalLikeCount(userId);
            user.put("totalLikeCount", totalLikeCount != null ? totalLikeCount : 0);
        }
        return user;
    }

    @Override
    public Map<String, Object> getCurrentUser(Integer userId) {
        Map<String, Object> user = userMapper.selectSimpleById(userId);
        if (user != null) {
            Integer totalLikeCount = getTotalLikeCount(userId);
            user.put("totalLikeCount", totalLikeCount != null ? totalLikeCount : 0);
        }
        return user;
    }

    // 添加一个辅助方法，为列表中的用户批量设置 isFollowing
    private void addIsFollowingToList(List<Map<String, Object>> userList, Integer currentUserId) {
        if (currentUserId == null || userList == null || userList.isEmpty()) {
            // 未登录，全部设为 false
            for (Map<String, Object> user : userList) {
                user.put("isFollowing", false);
            }
            return;
        }

        // 收集所有用户ID
        List<Integer> userIds = userList.stream()
                .map(user -> (Integer) user.get("user_id"))
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return;
        }

        // 批量查询当前用户关注了哪些人
        List<Integer> followingIds = followMapper.selectFollowingIds(currentUserId, userIds);

        for (Map<String, Object> user : userList) {
            Integer userId = (Integer) user.get("user_id");
            boolean isFollowing = followingIds.contains(userId);
            user.put("isFollowing", isFollowing);
        }
    }

    @Override
    public Map<String, Object> getUserList(String keyword, Integer page, Integer pageSize, Integer currentUserId) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = userMapper.selectList(keyword, offset, pageSize);
        Long total = userMapper.countList(keyword);

        // 添加 isFollowing 字段
        addIsFollowingToList(list, currentUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }


    @Override
    public boolean updateProfile(Integer userId, String username, String avatar, String bio) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setAvatar(avatar);
        user.setBio(bio);
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        // 检查新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        // 获取用户当前密码
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

//        // 验证旧密码是否正确
//        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
//            return false;
//        }
//
//        // 加密新密码并更新
//        String encodedNewPassword = passwordEncoder.encode(newPassword);
//        return userMapper.updatePassword(userId, encodedNewPassword) > 0;
//    }
        // 用 PasswordUtil 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 用 PasswordUtil 加密新密码
        String encodedNewPassword = PasswordUtil.encode(newPassword);
        return userMapper.updatePassword(userId, encodedNewPassword) > 0;
    }

    @Override
    public Long getPostCount(Integer userId) {
        return postMapper.countByUserId(userId);
    }

    @Override
    public Integer getTotalLikeCount(Integer userId) {
        Integer count = userMapper.selectTotalLikeCount(userId);
        return count != null ? count : 0;
    }

    @Override
    public Map<String, Object> getFollowingList(Integer userId, Integer page, Integer pageSize, Integer currentUserId) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = userMapper.selectFollowing(userId, offset, pageSize);
        Long total = userMapper.countFollowing(userId);

        // 添加 isFollowing 字段（查看当前用户是否关注了列表中的每个人）
        addIsFollowingToList(list, currentUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getFollowersList(Integer userId, Integer page, Integer pageSize, Integer currentUserId) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = userMapper.selectFollowers(userId, offset, pageSize);
        Long total = userMapper.countFollowers(userId);

        // 添加 isFollowing 字段
        addIsFollowingToList(list, currentUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getUserPosts(Integer userId, Integer currentUserId,Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = postMapper.selectByUserId(userId, offset, pageSize);
        Long total = postMapper.countByUserId(userId);

        if (currentUserId != null && !list.isEmpty()) {
            List<Integer> postIds = list.stream()
                    .map(item -> (Integer) item.get("post_id"))
                    .collect(Collectors.toList());
            List<Integer> likedIds = likeMapper.selectLikedPostIds(currentUserId, postIds);
            for (Map<String, Object> post : list) {
                boolean liked = likedIds.contains(post.get("post_id"));
                post.put("isLiked", liked);
                post.put("is_liked", liked);
            }
        } else {
            for (Map<String, Object> post : list) {
                post.put("isLiked", false);
                post.put("is_liked", false);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getUserFavorites(Integer userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = favoriteMapper.selectFavoritesWithService(userId, offset, pageSize);
        Long total = favoriteMapper.countByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getUserOrders(Integer userId, String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = appointmentOrderMapper.selectByUserId(userId, status, offset, pageSize);
        Long total = appointmentOrderMapper.countByUserId(userId, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }
}
