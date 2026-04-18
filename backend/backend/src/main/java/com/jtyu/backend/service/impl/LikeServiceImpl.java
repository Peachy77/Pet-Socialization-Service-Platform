package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.LikeMapper;
import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.service.LikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    @Transactional
    public boolean like(Integer userId, Integer postId) {
        if (likeMapper.exists(userId, postId) > 0) {
            return false;
        }
        int result = likeMapper.insert(userId, postId);
        if (result > 0) {
            postMapper.incrementLikeCount(postId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unlike(Integer userId, Integer postId) {
        if (likeMapper.exists(userId, postId) == 0) {
            return false;
        }
        int result = likeMapper.delete(userId, postId);
        if (result > 0) {
            postMapper.decrementLikeCount(postId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLiked(Integer userId, Integer postId) {
        return likeMapper.exists(userId, postId) > 0;
    }

    @Override
    public int getLikeCount(Integer postId) {
        return likeMapper.countByPostId(postId);
    }
}
