package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Like;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    @Select("SELECT * FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    Like findByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Insert("INSERT INTO `like` (user_id, post_id) VALUES (#{userId}, #{postId})")
    @Options(useGeneratedKeys = true, keyProperty = "likeId")
    int insert(Like like);

    @Delete("DELETE FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    int deleteByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM `like` WHERE post_id = #{postId}")
    int countByPostId(@Param("postId") Integer postId);

    @Delete("DELETE FROM `like` WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Integer postId);
}
