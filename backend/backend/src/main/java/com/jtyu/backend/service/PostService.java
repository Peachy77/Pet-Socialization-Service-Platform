package com.jtyu.backend.service;


import com.jtyu.backend.model.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    int createPost(Post post);
    boolean deletePost(Integer id);
}
