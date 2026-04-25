package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.FollowMapper;
import com.jtyu.backend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceImplTest {
    @Mock private FollowMapper followMapper;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private FollowServiceImpl followService;

    // ========== 关注测试 ==========
    @Test
    void testFollow_Success() {
        when(followMapper.exists(1, 2)).thenReturn(0);
        when(followMapper.insert(1, 2)).thenReturn(1);
        when(userMapper.incrementFollowingCount(1)).thenReturn(1);
        when(userMapper.incrementFollowerCount(2)).thenReturn(1);

        boolean result = followService.follow(1, 2);

        assertTrue(result);
        verify(followMapper, times(1)).insert(1, 2);
        verify(userMapper, times(1)).incrementFollowingCount(1);
        verify(userMapper, times(1)).incrementFollowerCount(2);
    }

    @Test
    void testFollow_Self() {
        boolean result = followService.follow(1, 1);

        assertFalse(result);
        verify(followMapper, never()).insert(anyInt(), anyInt());
        verify(userMapper, never()).incrementFollowingCount(anyInt());
    }

    @Test
    void testFollow_AlreadyFollowing() {
        when(followMapper.exists(1, 2)).thenReturn(1);

        boolean result = followService.follow(1, 2);

        assertFalse(result);
        verify(followMapper, never()).insert(anyInt(), anyInt());
        verify(userMapper, never()).incrementFollowingCount(anyInt());
    }

    @Test
    void testFollow_InsertFailed() {
        when(followMapper.exists(1, 2)).thenReturn(0);
        when(followMapper.insert(1, 2)).thenReturn(0);

        boolean result = followService.follow(1, 2);

        assertFalse(result);
        verify(userMapper, never()).incrementFollowingCount(anyInt());
    }

    // ========== 取消关注测试 ==========
    @Test
    void testUnfollow_Success() {
        when(followMapper.exists(1, 2)).thenReturn(1);
        when(followMapper.delete(1, 2)).thenReturn(1);
        when(userMapper.decrementFollowingCount(1)).thenReturn(1);
        when(userMapper.decrementFollowerCount(2)).thenReturn(1);

        boolean result = followService.unfollow(1, 2);

        assertTrue(result);
        verify(followMapper, times(1)).delete(1, 2);
        verify(userMapper, times(1)).decrementFollowingCount(1);
        verify(userMapper, times(1)).decrementFollowerCount(2);
    }

    @Test
    void testUnfollow_NotFollowing() {
        when(followMapper.exists(1, 2)).thenReturn(0);

        boolean result = followService.unfollow(1, 2);

        assertFalse(result);
        verify(followMapper, never()).delete(anyInt(), anyInt());
    }

    // ========== 检查关注状态测试 ==========
    @Test
    void testIsFollowing_True() {
        when(followMapper.exists(1, 2)).thenReturn(1);

        boolean result = followService.isFollowing(1, 2);

        assertTrue(result);
    }

    @Test
    void testIsFollowing_False() {
        when(followMapper.exists(1, 2)).thenReturn(0);

        boolean result = followService.isFollowing(1, 2);

        assertFalse(result);
    }
}
