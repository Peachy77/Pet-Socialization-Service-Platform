package com.jtyu.backend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.model.User;
import com.jtyu.backend.service.FollowService;
import com.jtyu.backend.service.UserService;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FollowService followService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== 登录测试 ==========
//    @Test
//    void testLogin_Success() throws Exception {
//        Map<String, String> params = new HashMap<>();
//        params.put("email", "test@example.com");
//        params.put("password", "123456");
//
//        Map<String, Object> mockUser = new HashMap<>();
//        mockUser.put("userId", 1);
//        mockUser.put("username", "testuser");
//        mockUser.put("email", "test@example.com");
//        mockUser.put("avatar", "avatar.png");
//        mockUser.put("bio", "bio");
//        mockUser.put("followerCount", 0);
//        mockUser.put("followingCount", 0);
//
//        when(userService.login("test@example.com", "123456")).thenReturn(mockUser);
//
//        // 不验证 token 的具体值，只验证登录成功
//        mockMvc.perform(post("/users/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(1))
//                .andExpect(jsonPath("$.data.userId").value(1))
//                .andExpect(jsonPath("$.data.token").exists());  // 只验证 token 字段存在
//    }

    @Test
    void testLogin_MissingFields() throws Exception {
        Map<String, String> params = new HashMap<>();

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("邮箱和密码不能为空"));
    }

    @Test
    void testLogin_Failed() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("email", "test@example.com");
        params.put("password", "wrong");

        when(userService.login("test@example.com", "wrong")).thenReturn(null);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("邮箱或密码错误"));
    }

    // ========== 注册测试 ==========
//    @Test
//    void testRegister_Success() throws Exception {
//        Map<String, String> params = new HashMap<>();
//        params.put("email", "new@example.com");
//        params.put("username", "newuser");
//        params.put("password", "123456");
//
//        User mockUser = new User();
//        mockUser.setUserId(2);
//        mockUser.setEmail("new@example.com");
//        mockUser.setUsername("newuser");
//        mockUser.setAvatar("avatar.png");
//        mockUser.setBio("bio");
//        mockUser.setFollowerCount(0);
//        mockUser.setFollowingCount(0);
//
//        when(userService.register("new@example.com", "newuser", "123456")).thenReturn(mockUser);
//
//        mockMvc.perform(post("/users/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(1))
//                .andExpect(jsonPath("$.data.userId").value(2))
//                .andExpect(jsonPath("$.data.token").exists());  // 只验证 token 字段存在
//    }

    @Test
    void testRegister_MissingFields() throws Exception {
        Map<String, String> params = new HashMap<>();

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("邮箱、用户名和密码不能为空"));
    }

    @Test
    void testRegister_EmailExists() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("email", "existing@example.com");
        params.put("username", "newuser");
        params.put("password", "123456");

        when(userService.register("existing@example.com", "newuser", "123456")).thenReturn(null);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("邮箱已被注册"));
    }

    // ========== 获取用户列表 ==========
    @Test
    void testGetUsers_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 10L);

        when(userService.getUserList(null, 1, 20, null)).thenReturn(mockResult);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    // ========== 获取用户详情 ==========
    @Test
    void testGetUser_Success() throws Exception {
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("user_id", 2);
        mockUser.put("username", "other");

        when(userService.getUserById(2)).thenReturn(mockUser);
        when(followService.isFollowing(1, 2)).thenReturn(true);

        mockMvc.perform(get("/users/2")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.isFollowing").value(true));
    }

    @Test
    void testGetUser_NotFound() throws Exception {
        when(userService.getUserById(999)).thenReturn(null);

        mockMvc.perform(get("/users/999")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("用户不存在"));
    }

    // ========== 获取当前用户 ==========
    @Test
    void testGetCurrentUser_Success() throws Exception {
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("user_id", 1);
        mockUser.put("username", "current");

        when(userService.getCurrentUser(1)).thenReturn(mockUser);

        mockMvc.perform(get("/users/me")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    // ========== 更新当前用户 ==========
    @Test
    void testUpdateCurrentUser_Success() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("username", "newname");
        params.put("avatar", "newavatar.png");
        params.put("bio", "新简介");

        when(userService.updateProfile(eq(1), eq("newname"), eq("newavatar.png"), eq("新简介")))
                .thenReturn(true);

        mockMvc.perform(put("/users/me")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("更新成功"));
    }

    @Test
    void testUpdateCurrentUser_WithPassword() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("username", "newname");
        params.put("oldPassword", "123456");
        params.put("newPassword", "newpass");
        params.put("confirmPassword", "newpass");

        when(userService.changePassword(1, "123456", "newpass", "newpass")).thenReturn(true);
        when(userService.updateProfile(eq(1), eq("newname"), isNull(), isNull()))
                .thenReturn(true);

        mockMvc.perform(put("/users/me")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("资料更新成功，密码已修改"));
    }

    // ========== 关注/取消关注 ==========
    @Test
    void testFollowUser_Success() throws Exception {
        when(followService.follow(1, 2)).thenReturn(true);

        mockMvc.perform(post("/users/follow/2")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("关注成功"));
    }

    @Test
    void testFollowUser_Failed() throws Exception {
        when(followService.follow(1, 2)).thenReturn(false);

        mockMvc.perform(post("/users/follow/2")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("关注失败"));
    }

    @Test
    void testUnfollowUser_Success() throws Exception {
        when(followService.unfollow(1, 2)).thenReturn(true);

        mockMvc.perform(delete("/users/follow/2")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("取消关注成功"));
    }

    // ========== 获取我关注的/粉丝列表 ==========
    @Test
    void testGetMyFollowing_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 5L);

        when(userService.getFollowingList(1, 1, 20, 1)).thenReturn(mockResult);

        mockMvc.perform(get("/users/me/following")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetMyFollowers_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 3L);

        when(userService.getFollowersList(1, 1, 20, 1)).thenReturn(mockResult);

        mockMvc.perform(get("/users/me/followers")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    // ========== 获取我的动态/收藏/订单 ==========
    @Test
    void testGetMyPosts_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 10L);

        when(userService.getUserPosts(1, 1, 1, 20)).thenReturn(mockResult);

        mockMvc.perform(get("/users/me/posts")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetMyFavorites_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 5L);

        when(userService.getUserFavorites(1, 1, 20)).thenReturn(mockResult);

        mockMvc.perform(get("/users/me/favorites")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetMyOrders_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 3L);

        when(userService.getUserOrders(eq(1), isNull(), eq(1), eq(20))).thenReturn(mockResult);

        mockMvc.perform(get("/users/me/orders")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    // ========== 获取指定用户的动态/收藏/订单 ==========
    @Test
    void testGetUserPosts_ByUserId() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 8L);

        when(userService.getUserPosts(2, 1, 1, 20)).thenReturn(mockResult);

        mockMvc.perform(get("/users/2/posts")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetUserFavorites_ByUserId() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 2L);

        when(userService.getUserFavorites(2, 1, 20)).thenReturn(mockResult);

        mockMvc.perform(get("/users/2/favorites")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetUserOrders_ByUserId() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 1L);

        when(userService.getUserOrders(eq(2), isNull(), eq(1), eq(20))).thenReturn(mockResult);

        mockMvc.perform(get("/users/2/orders")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    // ========== 获取指定用户的关注/粉丝列表 ==========
    @Test
    void testGetUserFollowing_ByUserId() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 4L);

        when(userService.getFollowingList(2, 1, 20, 1)).thenReturn(mockResult);

        mockMvc.perform(get("/users/2/following")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetUserFollowers_ByUserId() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 6L);

        when(userService.getFollowersList(2, 1, 20, 1)).thenReturn(mockResult);

        mockMvc.perform(get("/users/2/followers")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }
}
