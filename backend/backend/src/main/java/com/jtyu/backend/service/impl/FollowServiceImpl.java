package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.FollowMapper;
import com.jtyu.backend.mapper.UserMapper;
import com.jtyu.backend.model.Follow;
import com.jtyu.backend.service.FollowService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Mapper
public class FollowServiceImpl implements FollowService {
    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public boolean follow(Integer followerId, Integer followeeId) {
        // 不能关注自己
        if (followerId.equals(followeeId)) {
            return false;
        }

        // 检查是否已关注
        if (followMapper.exists(followerId, followeeId) > 0) {
            return false;
        }

        // 插入关注关系
        int result = followMapper.insert(followerId, followeeId);
        if (result > 0) {
            // 更新关注数
            userMapper.incrementFollowingCount(followerId);
            // 更新粉丝数
            userMapper.incrementFollowerCount(followeeId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unfollow(Integer followerId, Integer followeeId) {
        // 检查是否已关注
        if (followMapper.exists(followerId, followeeId) == 0) {
            return false;
        }

        int result = followMapper.delete(followerId, followeeId);
        if (result > 0) {
            // 更新关注数
            userMapper.decrementFollowingCount(followerId);
            // 更新粉丝数
            userMapper.decrementFollowerCount(followeeId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followeeId) {
        return followMapper.exists(followerId, followeeId) > 0;
    }
}
