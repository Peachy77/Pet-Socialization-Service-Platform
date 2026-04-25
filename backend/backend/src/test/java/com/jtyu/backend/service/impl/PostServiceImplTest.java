package com.jtyu.backend.service.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.LikeMapper;
import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.model.Post;
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
public class PostServiceImplTest {
    @Mock private PostMapper postMapper;
    @Mock private LikeMapper likeMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private Map<String, Object> testPost;

    @BeforeEach
    void setUp() {
        testPost = new HashMap<>();
        testPost.put("post_id", 1);
        testPost.put("user_id", 1);
        testPost.put("content", "测试动态内容");
        testPost.put("like_count", 0);
        testPost.put("comment_count", 0);
    }

    // ========== 发布动态测试 ==========
    @Test
    void testCreatePost_Success() {
        when(postMapper.insert(any(Post.class))).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setPostId(100);
            return 1;
        });

        List<String> images = Arrays.asList("img1.jpg", "img2.jpg");
        List<String> tags = Arrays.asList("宠物", "萌宠");

        Integer postId = postService.createPost(1, "新动态内容", images, tags);

        assertNotNull(postId);
        assertEquals(100, postId);
        verify(postMapper, times(1)).insert(any(Post.class));
    }

    // ========== 删除动态测试 ==========
    @Test
    void testDeletePost_Success() {
        when(postMapper.selectUserIdByPostId(1)).thenReturn(1);
        when(postMapper.deleteById(1)).thenReturn(1);

        boolean result = postService.deletePost(1, 1);

        assertTrue(result);
        verify(postMapper, times(1)).deleteById(1);
    }

    @Test
    void testDeletePost_WrongUser() {
        when(postMapper.selectUserIdByPostId(1)).thenReturn(2); // 作者是2

        boolean result = postService.deletePost(1, 1); // 当前用户是1

        assertFalse(result);
        verify(postMapper, never()).deleteById(anyInt());
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(postMapper.selectUserIdByPostId(999)).thenReturn(null);

        boolean result = postService.deletePost(999, 1);

        assertFalse(result);
        verify(postMapper, never()).deleteById(anyInt());
    }

    // ========== 点赞动态测试 ==========
    @Test
    void testLikePost_Success() {
        when(likeMapper.exists(1, 100)).thenReturn(0);
        when(likeMapper.insert(1, 100)).thenReturn(1);
        when(postMapper.incrementLikeCount(100)).thenReturn(1);

        boolean result = postService.likePost(100, 1);

        assertTrue(result);
        verify(likeMapper, times(1)).insert(1, 100);
        verify(postMapper, times(1)).incrementLikeCount(100);
    }

    @Test
    void testLikePost_AlreadyLiked() {
        when(likeMapper.exists(1, 100)).thenReturn(1);

        boolean result = postService.likePost(100, 1);

        assertFalse(result);
        verify(postMapper, never()).incrementLikeCount(anyInt());
    }

    // ========== 取消点赞测试 ==========
    @Test
    void testUnlikePost_Success() {
        when(likeMapper.exists(1, 100)).thenReturn(1);
        when(likeMapper.delete(1, 100)).thenReturn(1);
        when(postMapper.decrementLikeCount(100)).thenReturn(1);

        boolean result = postService.unlikePost(100, 1);

        assertTrue(result);
        verify(likeMapper, times(1)).delete(1, 100);
        verify(postMapper, times(1)).decrementLikeCount(100);
    }

    // ========== 获取动态列表测试 ==========
    @Test
    void testGetPostList_WithLikedStatus() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> post1 = new HashMap<>();
        post1.put("post_id", 1);
        mockList.add(post1);

        when(postMapper.selectList(null, null, 0, 20)).thenReturn(mockList);
        when(postMapper.countList(null, null)).thenReturn(1L);
        when(likeMapper.selectLikedPostIds(eq(1), anyList())).thenReturn(Arrays.asList(1));

        Map<String, Object> result = postService.getPostList(null, null, 1, 20, 1);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
        assertTrue((Boolean) list.get(0).get("isLiked"));
    }

    @Test
    void testGetPostList_NoLogin() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> post1 = new HashMap<>();
        post1.put("post_id", 1);
        mockList.add(post1);

        when(postMapper.selectList(null, null, 0, 20)).thenReturn(mockList);
        when(postMapper.countList(null, null)).thenReturn(1L);

        Map<String, Object> result = postService.getPostList(null, null, 1, 20, null);

        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
        assertFalse((Boolean) list.get(0).get("isLiked"));
        verify(likeMapper, never()).selectLikedPostIds(anyInt(), anyList());
    }
    // ========== getPostDetail 测试 ==========
    @Test
    void testGetPostDetail_Success_WithLogin() {
        Map<String, Object> mockPost = new HashMap<>();
        mockPost.put("post_id", 100);
        mockPost.put("content", "测试内容");

        when(postMapper.selectById(100)).thenReturn(mockPost);
        when(likeMapper.exists(1, 100)).thenReturn(1);

        Map<String, Object> result = postService.getPostDetail(100, 1);

        assertNotNull(result);
        assertTrue((Boolean) result.get("isLiked"));
    }

    @Test
    void testGetPostDetail_Success_NoLogin() {
        Map<String, Object> mockPost = new HashMap<>();
        mockPost.put("post_id", 100);

        when(postMapper.selectById(100)).thenReturn(mockPost);

        Map<String, Object> result = postService.getPostDetail(100, null);

        assertNotNull(result);
        assertFalse((Boolean) result.get("isLiked"));
    }

    @Test
    void testGetPostDetail_NotFound() {
        when(postMapper.selectById(999)).thenReturn(null);

        Map<String, Object> result = postService.getPostDetail(999, 1);

        assertNull(result);
    }

    // ========== getPostList 空列表测试 ==========
    @Test
    void testGetPostList_EmptyList() {
        when(postMapper.selectList(null, null, 0, 20)).thenReturn(new ArrayList<>());
        when(postMapper.countList(null, null)).thenReturn(0L);

        Map<String, Object> result = postService.getPostList(null, null, 1, 20, 1);

        assertNotNull(result);
        assertEquals(0L, result.get("total"));
        verify(likeMapper, never()).selectLikedPostIds(anyInt(), anyList());
    }

    // ========== likePost 失败场景 ==========
    @Test
    void testLikePost_InsertFailed() {
        when(likeMapper.exists(1, 100)).thenReturn(0);
        when(likeMapper.insert(1, 100)).thenReturn(0);

        boolean result = postService.likePost(100, 1);

        assertFalse(result);
        verify(postMapper, never()).incrementLikeCount(anyInt());
    }

    // ========== unlikePost 失败场景 ==========
    @Test
    void testUnlikePost_DeleteFailed() {
        when(likeMapper.exists(1, 100)).thenReturn(1);
        when(likeMapper.delete(1, 100)).thenReturn(0);

        boolean result = postService.unlikePost(100, 1);

        assertFalse(result);
        verify(postMapper, never()).decrementLikeCount(anyInt());
    }
}
