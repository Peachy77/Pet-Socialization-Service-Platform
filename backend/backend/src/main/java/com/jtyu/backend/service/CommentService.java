package com.jtyu.backend.service;

import com.jtyu.backend.model.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {
    // 获取评论列表（只返回一级评论，parent_comment_id = 0）
    Map<String, Object> getCommentsByPostId(Integer postId, Integer page, Integer pageSize, Integer currentUserId);

    // 获取评论的回复列表
    List<Map<String, Object>> getRepliesByCommentId(Integer commentId, Integer currentUserId);

    // 发表评论或回复（parentCommentId = 0 表示一级评论，否则表示回复某条评论）
    Integer createComment(Integer userId, Integer postId, Integer parentCommentId, String content, List<String> images);

    // 删除评论
    boolean deleteComment(Integer commentId, Integer currentUserId);

    // 点赞评论
    boolean likeComment(Integer commentId, Integer userId);

    // 取消点赞评论
    boolean unlikeComment(Integer commentId, Integer userId);
}
