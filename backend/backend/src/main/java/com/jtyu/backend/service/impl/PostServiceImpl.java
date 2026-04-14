package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.PostMapper;
import com.jtyu.backend.model.Post;
import com.jtyu.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Post> getAllPosts() {
        return postMapper.findAll();
    }

    @Override
    public int createPost(Post post) {
        return postMapper.insert(post);
    }

    @Override
    public boolean deletePost(Integer id) {
        return postMapper.deleteById(id) > 0;
    }
}
