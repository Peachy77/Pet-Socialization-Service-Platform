package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.LikeMapper;
import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.model.Post;
import com.jtyu.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private LikeMapper likeMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getPostList(String keyword, String tag, Integer page, Integer pageSize, Integer currentUserId) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = postMapper.selectList(keyword, tag, offset, pageSize);
        Long total = postMapper.countList(keyword, tag);

        // 批量获取点赞状态
        if (currentUserId != null && !list.isEmpty()) {
            List<Integer> postIds = list.stream()
                    .map(item -> (Integer) item.get("post_id"))
                    .collect(Collectors.toList());
            List<Integer> likedIds = likeMapper.selectLikedPostIds(currentUserId, postIds);
            for (Map<String, Object> post : list) {
                boolean liked = likedIds.contains(post.get("post_id"));
                post.put("isLiked", liked);
                post.put("is_liked", liked);
            }
        } else {
            for (Map<String, Object> post : list) {
                post.put("isLiked", false);
                post.put("is_liked", false);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getPostDetail(Integer postId, Integer currentUserId) {
        Map<String, Object> post = postMapper.selectById(postId);
        if (post != null && currentUserId != null) {
            boolean isLiked = likeMapper.exists(currentUserId, postId) > 0;
            post.put("isLiked", isLiked);
            post.put("is_liked", isLiked);
        } else if (post != null) {
            post.put("isLiked", false);
            post.put("is_liked", false);
        }
        return post;
    }

    @Override
    @Transactional
    public Integer createPost(Integer userId, String content, List<String> images, List<String> tags) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        try {
            post.setImages(objectMapper.writeValueAsString(images));
            post.setTags(objectMapper.writeValueAsString(tags));
        } catch (Exception e) {
            post.setImages("[]");
            post.setTags("[]");
        }
        post.setLikeCount(0);
        post.setCommentCount(0);

        int rows = postMapper.insert(post);
        if (rows > 0) {
            return post.getPostId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deletePost(Integer postId, Integer currentUserId) {
        Integer userId = postMapper.selectUserIdByPostId(postId);
        if (userId == null || !userId.equals(currentUserId)) {
            return false;
        }
        return postMapper.deleteById(postId) > 0;
    }

    @Override
    @Transactional
    public boolean likePost(Integer postId, Integer userId) {
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
    public boolean unlikePost(Integer postId, Integer userId) {
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
    public boolean isLiked(Integer postId, Integer userId) {
        return likeMapper.exists(userId, postId) > 0;
    }
}
