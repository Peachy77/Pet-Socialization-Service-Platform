package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.CommentLikeMapper;
import com.jtyu.backend.mapper.CommentMapper;
import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.model.Comment;
import com.jtyu.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private PostMapper postMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getCommentsByPostId(Integer postId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = commentMapper.selectByPostId(postId, offset, pageSize);
        Long total = commentMapper.countByPostId(postId);

        // 为每个评论获取回复数量和回复列表
        for (Map<String, Object> comment : list) {
            Integer commentId = (Integer) comment.get("comment_id");
            Long replyCount = commentMapper.countRepliesByParentId(commentId);
            comment.put("reply_count", replyCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public List<Map<String, Object>> getRepliesByCommentId(Integer commentId) {
        return commentMapper.selectRepliesByParentId(commentId);
    }

    @Override
    @Transactional
    public Integer createComment(Integer userId, Integer postId, Integer parentCommentId, String content, List<String> images) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setParentCommentId(parentCommentId != null ? parentCommentId : 0);
        comment.setContent(content);
        try {
            comment.setImages(objectMapper.writeValueAsString(images != null ? images : Collections.emptyList()));
        } catch (Exception e) {
            comment.setImages("[]");
        }

        int rows = commentMapper.insert(comment);
        if (rows > 0) {
            // 更新动态的评论数
            postMapper.incrementCommentCount(postId);
            return comment.getCommentId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer commentId, Integer currentUserId) {
        Integer userId = commentMapper.selectUserIdByCommentId(commentId);
        if (userId == null || !userId.equals(currentUserId)) {
            return false;
        }

        // 获取postId用于更新评论数
        Map<String, Object> comment = commentMapper.selectById(commentId);
        if (comment != null) {
            Integer postId = (Integer) comment.get("post_id");
            int rows = commentMapper.deleteById(commentId);
            if (rows > 0) {
                postMapper.decrementCommentCount(postId);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean likeComment(Integer commentId, Integer userId) {
        if (commentLikeMapper.exists(userId, commentId) > 0) {
            return false;
        }
        return commentLikeMapper.insert(userId, commentId) > 0;
    }

    @Override
    public boolean unlikeComment(Integer commentId, Integer userId) {
        if (commentLikeMapper.exists(userId, commentId) == 0) {
            return false;
        }
        return commentLikeMapper.delete(userId, commentId) > 0;
    }
}
