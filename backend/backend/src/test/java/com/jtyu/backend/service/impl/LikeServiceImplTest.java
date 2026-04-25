package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.LikeMapper;
import com.jtyu.backend.mapper.PostMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {
    @Mock private LikeMapper likeMapper;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private LikeServiceImpl likeService;

    // ========== 点赞测试 ==========
    @Test
    void testLike_Success() {
        when(likeMapper.exists(1, 100)).thenReturn(0);
        when(likeMapper.insert(1, 100)).thenReturn(1);
        when(postMapper.incrementLikeCount(100)).thenReturn(1);

        boolean result = likeService.like(1, 100);

        assertTrue(result);
        verify(likeMapper, times(1)).insert(1, 100);
        verify(postMapper, times(1)).incrementLikeCount(100);
    }

    @Test
    void testLike_AlreadyLiked() {
        when(likeMapper.exists(1, 100)).thenReturn(1);

        boolean result = likeService.like(1, 100);

        assertFalse(result);
        verify(likeMapper, never()).insert(anyInt(), anyInt());
        verify(postMapper, never()).incrementLikeCount(anyInt());
    }

    @Test
    void testLike_InsertFailed() {
        when(likeMapper.exists(1, 100)).thenReturn(0);
        when(likeMapper.insert(1, 100)).thenReturn(0);

        boolean result = likeService.like(1, 100);

        assertFalse(result);
        verify(postMapper, never()).incrementLikeCount(anyInt());
    }

    // ========== 取消点赞测试 ==========
    @Test
    void testUnlike_Success() {
        when(likeMapper.exists(1, 100)).thenReturn(1);
        when(likeMapper.delete(1, 100)).thenReturn(1);
        when(postMapper.decrementLikeCount(100)).thenReturn(1);

        boolean result = likeService.unlike(1, 100);

        assertTrue(result);
        verify(likeMapper, times(1)).delete(1, 100);
        verify(postMapper, times(1)).decrementLikeCount(100);
    }

    @Test
    void testUnlike_NotLiked() {
        when(likeMapper.exists(1, 100)).thenReturn(0);

        boolean result = likeService.unlike(1, 100);

        assertFalse(result);
        verify(likeMapper, never()).delete(anyInt(), anyInt());
        verify(postMapper, never()).decrementLikeCount(anyInt());
    }

    // ========== 查询测试 ==========
    @Test
    void testIsLiked_True() {
        when(likeMapper.exists(1, 100)).thenReturn(1);

        boolean result = likeService.isLiked(1, 100);

        assertTrue(result);
    }

    @Test
    void testGetLikeCount() {
        when(likeMapper.countByPostId(100)).thenReturn(42);

        int result = likeService.getLikeCount(100);

        assertEquals(42, result);
    }
}
