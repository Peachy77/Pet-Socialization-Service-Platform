package com.jtyu.backend.service;

import com.jtyu.backend.model.Follow;

import java.util.List;

public interface FollowService {

    // 关注用户
    boolean follow(Integer followerId, Integer followeeId);

    // 取消关注
    boolean unfollow(Integer followerId, Integer followeeId);

    // 检查是否已关注
    boolean isFollowing(Integer followerId, Integer followeeId);
}
