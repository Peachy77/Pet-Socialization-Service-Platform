package com.jtyu.backend.service;

public interface LikeService {
    boolean like(Integer userId, Integer postId);

    boolean unlike(Integer userId, Integer postId);

    boolean isLiked(Integer userId, Integer postId);

    int getLikeCount(Integer postId);
}
