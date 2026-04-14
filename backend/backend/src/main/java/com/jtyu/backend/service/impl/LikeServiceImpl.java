package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.LikeMapper;
import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.service.LikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    @Transactional
    public int likePost(Integer postId) {
        // 注意：这里缺少 userId，你的 Controller 没有传 userId
        // 暂时返回 1 模拟成功，后续需要从 token 获取 userId
        // TODO: 从当前登录用户获取 userId
        Integer userId = 1; // 临时模拟

        // 检查是否已点赞
        if (likeMapper.findByUserAndPost(userId, postId) != null) {
            return 0;
        }

        com.jtyu.backend.model.Like like = new com.jtyu.backend.model.Like();
        like.setUserId(userId);
        like.setPostId(postId);
        int result = likeMapper.insert(like);
        if (result > 0) {
            postMapper.updateLikeCount(postId, 1);
        }
        return result;
    }
}
