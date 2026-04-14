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
    public int followUser(Follow follow) {
        // 检查是否已经关注
        Follow exist = followMapper.findByFollowerAndFollowee(
                follow.getFollowerId(), follow.getFolloweeId());
        if (exist != null) {
            return 0;
        }
        int result = followMapper.insert(follow);
        if (result > 0) {
            // 更新关注数和粉丝数
            userMapper.updateFollowingCount(follow.getFollowerId(), 1);
            userMapper.updateFollowerCount(follow.getFolloweeId(), 1);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean unfollowUser(Integer id) {
        Follow follow = followMapper.findById(id);
        if (follow == null) {
            return false;
        }
        int result = followMapper.deleteById(id);
        if (result > 0) {
            userMapper.updateFollowingCount(follow.getFollowerId(), -1);
            userMapper.updateFollowerCount(follow.getFolloweeId(), -1);
            return true;
        }
        return false;
    }
}
