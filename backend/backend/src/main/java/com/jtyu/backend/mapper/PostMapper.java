package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {
    @Select("SELECT * FROM post ORDER BY create_time DESC")
    List<Post> findAll();

    @Select("SELECT * FROM post WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Post> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM post WHERE post_id = #{postId}")
    Post findById(@Param("postId") Integer postId);

    @Insert("INSERT INTO post (user_id, content, images, tags) VALUES (#{userId}, #{content}, #{images}, #{tags})")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    int insert(Post post);

    @Update("UPDATE post SET content = #{content}, images = #{images}, tags = #{tags} WHERE post_id = #{postId}")
    int update(Post post);

    @Delete("DELETE FROM post WHERE post_id = #{postId}")
    int deleteById(@Param("postId") Integer postId);

    @Update("UPDATE post SET like_count = like_count + #{delta} WHERE post_id = #{postId}")
    int updateLikeCount(@Param("postId") Integer postId, @Param("delta") int delta);

    @Update("UPDATE post SET comment_count = comment_count + #{delta} WHERE post_id = #{postId}")
    int updateCommentCount(@Param("postId") Integer postId, @Param("delta") int delta);
}
