package com.jtyu.backend.service;

import com.jtyu.backend.model.Follow;

import java.util.List;

public interface FollowService {
    int followUser(Follow follow);
    boolean unfollowUser(Integer id);
}
