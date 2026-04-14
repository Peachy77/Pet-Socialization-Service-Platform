package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.CommentMapper;
import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.model.Comment;
import com.jtyu.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Comment> getCommentsByPostId(Integer postId) {
        return commentMapper.findByPostId(postId);
    }

    @Override
    @Transactional
    public int createComment(Comment comment) {
        int result = commentMapper.insert(comment);
        if (result > 0) {
            postMapper.updateCommentCount(comment.getPostId(), 1);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer id) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return false;
        }
        int result = commentMapper.deleteById(id);
        if (result > 0) {
            postMapper.updateCommentCount(comment.getPostId(), -1);
            return true;
        }
        return false;
    }
}
