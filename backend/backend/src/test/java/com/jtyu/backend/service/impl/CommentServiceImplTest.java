package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.CommentLikeMapper;
import com.jtyu.backend.mapper.CommentMapper;
import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.model.Comment;
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
public class CommentServiceImplTest {
    @Mock private CommentMapper commentMapper;
    @Mock private CommentLikeMapper commentLikeMapper;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Map<String, Object> mockComment;
    private List<String> mockImages;

    @BeforeEach
    void setUp() {
        mockComment = new HashMap<>();
        mockComment.put("comment_id", 1);
        mockComment.put("user_id", 1);
        mockComment.put("post_id", 100);
        mockComment.put("content", "测试评论");
        mockComment.put("create_time", new Date());

        mockImages = Arrays.asList("img1.jpg", "img2.jpg");
    }

    // ========== getCommentsByPostId 测试 ==========
    @Test
    void testGetCommentsByPostId_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(mockComment);

        when(commentMapper.selectByPostId(eq(100), eq(0), eq(20), anyInt()))
                .thenReturn(mockList);
        when(commentMapper.countByPostId(100)).thenReturn(1L);
        when(commentMapper.selectRepliesByParentId(eq(1), anyInt()))
                .thenReturn(new ArrayList<>());
        when(commentMapper.countRepliesByParentId(1)).thenReturn(0L);

        Map<String, Object> result = commentService.getCommentsByPostId(100, 1, 20, 1);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        verify(commentMapper, times(1)).selectByPostId(eq(100), eq(0), eq(20), anyInt());
    }

    @Test
    void testGetCommentsByPostId_NoLogin() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(mockComment);

        when(commentMapper.selectByPostId(eq(100), eq(0), eq(20), eq(0)))
                .thenReturn(mockList);
        when(commentMapper.countByPostId(100)).thenReturn(1L);
        when(commentMapper.selectRepliesByParentId(eq(1), eq(0)))
                .thenReturn(new ArrayList<>());
        when(commentMapper.countRepliesByParentId(1)).thenReturn(0L);

        Map<String, Object> result = commentService.getCommentsByPostId(100, 1, 20, null);

        assertNotNull(result);
        verify(commentMapper, times(1)).selectByPostId(eq(100), eq(0), eq(20), eq(0));
    }

    // ========== getRepliesByCommentId 测试 ==========
    @Test
    void testGetRepliesByCommentId_Success() {
        List<Map<String, Object>> mockReplies = new ArrayList<>();
        mockReplies.add(mockComment);

        when(commentMapper.selectRepliesByParentId(eq(1), eq(1))).thenReturn(mockReplies);

        List<Map<String, Object>> result = commentService.getRepliesByCommentId(1, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetRepliesByCommentId_NoLogin() {
        List<Map<String, Object>> mockReplies = new ArrayList<>();
        mockReplies.add(mockComment);

        when(commentMapper.selectRepliesByParentId(eq(1), eq(0))).thenReturn(mockReplies);

        List<Map<String, Object>> result = commentService.getRepliesByCommentId(1, null);

        assertNotNull(result);
    }

    // ========== createComment 测试 ==========
    @Test
    void testCreateComment_Success() {
        when(commentMapper.insert(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setCommentId(100);
            return 1;
        });
        when(postMapper.incrementCommentCount(100)).thenReturn(1);

        Integer commentId = commentService.createComment(1, 100, 0, "新评论", mockImages);

        assertNotNull(commentId);
        assertEquals(100, commentId);
        verify(postMapper, times(1)).incrementCommentCount(100);
    }

    @Test
    void testCreateComment_WithParentId() {
        when(commentMapper.insert(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setCommentId(101);
            return 1;
        });
        when(postMapper.incrementCommentCount(100)).thenReturn(1);

        Integer commentId = commentService.createComment(1, 100, 50, "回复评论", null);

        assertNotNull(commentId);
        verify(commentMapper, times(1)).insert(any(Comment.class));
    }

    @Test
    void testCreateComment_Failed() {
        when(commentMapper.insert(any(Comment.class))).thenReturn(0);

        Integer commentId = commentService.createComment(1, 100, 0, "新评论", null);

        assertNull(commentId);
        verify(postMapper, never()).incrementCommentCount(anyInt());
    }

    // ========== deleteComment 测试 ==========
    @Test
    void testDeleteComment_Success() {
        when(commentMapper.selectUserIdByCommentId(100)).thenReturn(1);
        when(commentMapper.selectById(100)).thenReturn(mockComment);
        when(commentMapper.deleteById(100)).thenReturn(1);
        when(postMapper.decrementCommentCount(100)).thenReturn(1);

        boolean result = commentService.deleteComment(100, 1);

        assertTrue(result);
        verify(commentMapper, times(1)).deleteById(100);
        verify(postMapper, times(1)).decrementCommentCount(100);
    }

    @Test
    void testDeleteComment_NotOwner() {
        when(commentMapper.selectUserIdByCommentId(100)).thenReturn(2); // 作者是2

        boolean result = commentService.deleteComment(100, 1); // 当前用户是1

        assertFalse(result);
        verify(commentMapper, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteComment_NotFound() {
        when(commentMapper.selectUserIdByCommentId(999)).thenReturn(null);

        boolean result = commentService.deleteComment(999, 1);

        assertFalse(result);
    }

    // ========== likeComment 测试 ==========
    @Test
    void testLikeComment_Success() {
        when(commentLikeMapper.exists(1, 100)).thenReturn(0);
        when(commentLikeMapper.insert(1, 100)).thenReturn(1);

        boolean result = commentService.likeComment(100, 1);

        assertTrue(result);
        verify(commentLikeMapper, times(1)).insert(1, 100);
    }

    @Test
    void testLikeComment_AlreadyLiked() {
        when(commentLikeMapper.exists(1, 100)).thenReturn(1);

        boolean result = commentService.likeComment(100, 1);

        assertFalse(result);
        verify(commentLikeMapper, never()).insert(anyInt(), anyInt());
    }

    // ========== unlikeComment 测试 ==========
    @Test
    void testUnlikeComment_Success() {
        when(commentLikeMapper.exists(1, 100)).thenReturn(1);
        when(commentLikeMapper.delete(1, 100)).thenReturn(1);

        boolean result = commentService.unlikeComment(100, 1);

        assertTrue(result);
        verify(commentLikeMapper, times(1)).delete(1, 100);
    }

    @Test
    void testUnlikeComment_NotLiked() {
        when(commentLikeMapper.exists(1, 100)).thenReturn(0);

        boolean result = commentService.unlikeComment(100, 1);

        assertFalse(result);
        verify(commentLikeMapper, never()).delete(anyInt(), anyInt());
    }
}
