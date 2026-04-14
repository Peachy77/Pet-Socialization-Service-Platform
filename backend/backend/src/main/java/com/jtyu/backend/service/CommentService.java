package com.jtyu.backend.service;

import com.jtyu.backend.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByPostId(Integer postId);
    int createComment(Comment comment);
    boolean deleteComment(Integer id);
}
