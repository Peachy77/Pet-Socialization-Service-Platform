package com.jtyu.backend.service;


import com.jtyu.backend.model.Post;

import java.util.List;
import java.util.Map;

public interface PostService {

    // 获取动态列表（支持关键词、标签搜索）
    Map<String, Object> getPostList(String keyword, String tag, Integer page, Integer pageSize, Integer currentUserId);

    // 获取动态详情
    Map<String, Object> getPostDetail(Integer postId, Integer currentUserId);

    // 发布动态
    Integer createPost(Integer userId, String content, List<String> images, List<String> tags);

    // 删除动态
    boolean deletePost(Integer postId, Integer currentUserId);

    // 点赞动态
    boolean likePost(Integer postId, Integer userId);

    // 取消点赞
    boolean unlikePost(Integer postId, Integer userId);

    // 检查是否已点赞
    boolean isLiked(Integer postId, Integer userId);
}
