package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.*;
import com.jtyu.backend.model.User;
import com.jtyu.backend.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock private UserMapper userMapper;
    @Mock private FollowMapper followMapper;
    @Mock private PostMapper postMapper;
    @Mock private FavoriteMapper favoriteMapper;
    @Mock private AppointmentOrderMapper appointmentOrderMapper;
    @Mock private LikeMapper likeMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Map<String, Object> testUserMap;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(PasswordUtil.encode("123456"));
        testUser.setAvatar("avatar.png");
        testUser.setBio("测试用户");
        testUser.setFollowerCount(0);
        testUser.setFollowingCount(0);

        testUserMap = new HashMap<>();
        testUserMap.put("user_id", 1);
        testUserMap.put("username", "testuser");
        testUserMap.put("email", "test@example.com");
        testUserMap.put("avatar", "avatar.png");
        testUserMap.put("bio", "测试用户");
        testUserMap.put("follower_count", 0);
        testUserMap.put("following_count", 0);
    }

    // ========== 登录测试 ==========
    @Test
    void testLogin_Success() {
        when(userMapper.selectByEmail("test@example.com")).thenReturn(testUser);

        Map<String, Object> result = userService.login("test@example.com", "123456");

        assertNotNull(result);
        assertEquals(1, result.get("userId"));
        assertEquals("testuser", result.get("username"));
        verify(userMapper, times(1)).selectByEmail("test@example.com");
    }

    @Test
    void testLogin_WrongPassword() {
        when(userMapper.selectByEmail("test@example.com")).thenReturn(testUser);

        Map<String, Object> result = userService.login("test@example.com", "wrongpassword");

        assertNull(result);
        verify(userMapper, times(1)).selectByEmail("test@example.com");
    }

    @Test
    void testLogin_UserNotFound() {
        when(userMapper.selectByEmail("notexist@example.com")).thenReturn(null);

        Map<String, Object> result = userService.login("notexist@example.com", "123456");

        assertNull(result);
        verify(userMapper, times(1)).selectByEmail("notexist@example.com");
    }

    // ========== 注册测试 ==========
    @Test
    void testRegister_Success() {
        when(userMapper.selectByEmail("new@example.com")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(2);
            return 1;
        });

        User result = userService.register("new@example.com", "newuser", "123456");

        assertNotNull(result);
        assertEquals(2, result.getUserId());
        assertEquals("new@example.com", result.getEmail());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        when(userMapper.selectByEmail("test@example.com")).thenReturn(testUser);

        User result = userService.register("test@example.com", "another", "123456");

        assertNull(result);
        verify(userMapper, never()).insert(any(User.class));
    }

    // ========== 修改密码测试 ==========
    @Test
    void testChangePassword_Success() {
        when(userMapper.selectById(1)).thenReturn(testUser);
        when(userMapper.updatePassword(eq(1), anyString())).thenReturn(1);

        boolean result = userService.changePassword(1, "123456", "newpass", "newpass");

        assertTrue(result);
        verify(userMapper, times(1)).updatePassword(eq(1), anyString());
    }

    @Test
    void testChangePassword_WrongOldPassword() {
        when(userMapper.selectById(1)).thenReturn(testUser);

        boolean result = userService.changePassword(1, "wrong", "newpass", "newpass");

        assertFalse(result);
        verify(userMapper, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    void testChangePassword_NewPasswordMismatch() {
        boolean result = userService.changePassword(1, "123456", "newpass", "different");

        assertFalse(result);
        verify(userMapper, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    void testChangePassword_UserNotFound() {
        when(userMapper.selectById(999)).thenReturn(null);

        boolean result = userService.changePassword(999, "123456", "newpass", "newpass");

        assertFalse(result);
        verify(userMapper, never()).updatePassword(anyInt(), anyString());
    }

    // ========== 更新资料测试 ==========
    @Test
    void testUpdateProfile_Success() {
        when(userMapper.update(any(User.class))).thenReturn(1);

        boolean result = userService.updateProfile(1, "newname", "newavatar.png", "新简介");

        assertTrue(result);
        verify(userMapper, times(1)).update(any(User.class));
    }

    // ========== 获取用户信息测试 ==========
    @Test
    void testGetUserById_Success() {
        when(userMapper.selectSimpleById(1)).thenReturn(testUserMap);
        when(userMapper.selectTotalLikeCount(1)).thenReturn(100);

        Map<String, Object> result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.get("user_id"));
        assertEquals(100, result.get("totalLikeCount"));
    }

    @Test
    void testGetUserById_WithNullLikeCount() {
        when(userMapper.selectSimpleById(1)).thenReturn(testUserMap);
        when(userMapper.selectTotalLikeCount(1)).thenReturn(null);

        Map<String, Object> result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(0, result.get("totalLikeCount"));
    }
    // ========== getUserList 测试 ==========
    @Test
    void testGetUserList_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", 2);
        user.put("username", "other");
        mockList.add(user);

        when(userMapper.selectList("test", 0, 20)).thenReturn(mockList);
        when(userMapper.countList("test")).thenReturn(1L);
        when(followMapper.selectFollowingIds(eq(1), anyList())).thenReturn(Arrays.asList(2));

        Map<String, Object> result = userService.getUserList("test", 1, 20, 1);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    @Test
    void testGetUserList_NoLogin() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(new HashMap<>());

        when(userMapper.selectList(null, 0, 20)).thenReturn(mockList);
        when(userMapper.countList(null)).thenReturn(1L);

        Map<String, Object> result = userService.getUserList(null, 1, 20, null);

        assertNotNull(result);
        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
        assertFalse((Boolean) list.get(0).get("isFollowing"));
    }

    // ========== getCurrentUser 测试 ==========
    @Test
    void testGetCurrentUser_Success() {
        when(userMapper.selectSimpleById(1)).thenReturn(testUserMap);
        when(userMapper.selectTotalLikeCount(1)).thenReturn(50);

        Map<String, Object> result = userService.getCurrentUser(1);

        assertNotNull(result);
        assertEquals(50, result.get("totalLikeCount"));
    }

    @Test
    void testGetCurrentUser_NotFound() {
        when(userMapper.selectSimpleById(999)).thenReturn(null);

        Map<String, Object> result = userService.getCurrentUser(999);

        assertNull(result);
    }

    // ========== getFollowingList 测试 ==========
    @Test
    void testGetFollowingList_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", 2);
        mockList.add(user);

        when(userMapper.selectFollowing(1, 0, 20)).thenReturn(mockList);
        when(userMapper.countFollowing(1)).thenReturn(1L);
        when(followMapper.selectFollowingIds(eq(1), anyList())).thenReturn(Arrays.asList(2));

        Map<String, Object> result = userService.getFollowingList(1, 1, 20, 1);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    // ========== getFollowersList 测试 ==========
    @Test
    void testGetFollowersList_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", 2);
        mockList.add(user);

        when(userMapper.selectFollowers(1, 0, 20)).thenReturn(mockList);
        when(userMapper.countFollowers(1)).thenReturn(1L);
        when(followMapper.selectFollowingIds(eq(1), anyList())).thenReturn(new ArrayList<>());

        Map<String, Object> result = userService.getFollowersList(1, 1, 20, 1);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    // ========== getUserPosts 测试 ==========
    @Test
    void testGetUserPosts_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> post = new HashMap<>();
        post.put("post_id", 100);
        mockList.add(post);

        when(postMapper.selectByUserId(1, 0, 20)).thenReturn(mockList);
        when(postMapper.countByUserId(1)).thenReturn(1L);
        when(likeMapper.selectLikedPostIds(eq(1), anyList())).thenReturn(Arrays.asList(100));

        Map<String, Object> result = userService.getUserPosts(1, 1, 1, 20);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    @Test
    void testGetUserPosts_NoLogin() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> post = new HashMap<>();
        post.put("post_id", 100);
        mockList.add(post);

        when(postMapper.selectByUserId(1, 0, 20)).thenReturn(mockList);
        when(postMapper.countByUserId(1)).thenReturn(1L);

        Map<String, Object> result = userService.getUserPosts(1, null, 1, 20);

        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
        assertFalse((Boolean) list.get(0).get("isLiked"));
        verify(likeMapper, never()).selectLikedPostIds(anyInt(), anyList());
    }

    // ========== getUserFavorites 测试 ==========
    @Test
    void testGetUserFavorites_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(new HashMap<>());

        when(favoriteMapper.selectFavoritesWithService(1, 0, 20)).thenReturn(mockList);
        when(favoriteMapper.countByUserId(1)).thenReturn(1L);

        Map<String, Object> result = userService.getUserFavorites(1, 1, 20);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    // ========== getUserOrders 测试 ==========
    @Test
    void testGetUserOrders_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(new HashMap<>());

        when(appointmentOrderMapper.selectByUserId(eq(1), eq("pending"), eq(0), eq(20)))
                .thenReturn(mockList);
        when(appointmentOrderMapper.countByUserId(eq(1), eq("pending"))).thenReturn(1L);

        Map<String, Object> result = userService.getUserOrders(1, "pending", 1, 20);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    // ========== getPostCount 测试 ==========
    @Test
    void testGetPostCount() {
        when(postMapper.countByUserId(1)).thenReturn(5L);

        Long result = userService.getPostCount(1);

        assertEquals(5L, result);
    }

    // ========== getTotalLikeCount 测试 ==========
    @Test
    void testGetTotalLikeCount() {
        when(userMapper.selectTotalLikeCount(1)).thenReturn(100);

        Integer result = userService.getTotalLikeCount(1);

        assertEquals(100, result);
    }

    @Test
    void testGetTotalLikeCount_Zero() {
        when(userMapper.selectTotalLikeCount(1)).thenReturn(null);

        Integer result = userService.getTotalLikeCount(1);

        assertEquals(0, result);
    }

    @Test
    void testGetTotalLikeCount_Existing() {
        when(userMapper.selectTotalLikeCount(1)).thenReturn(100);

        Integer result = userService.getTotalLikeCount(1);

        assertEquals(100, result);
    }
}
