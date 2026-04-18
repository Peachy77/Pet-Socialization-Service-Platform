package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Like;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LikeMapper {
    @Select("SELECT COUNT(*) FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    int exists(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Insert("INSERT INTO `like` (user_id, post_id) VALUES (#{userId}, #{postId})")
    int insert(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Delete("DELETE FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    int delete(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM `like` WHERE post_id = #{postId}")
    int countByPostId(@Param("postId") Integer postId);

    // 批量获取用户点赞的帖子ID
    @Select("<script>" +
            "SELECT post_id FROM `like` WHERE user_id = #{userId} AND post_id IN " +
            "<foreach collection='postIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Integer> selectLikedPostIds(@Param("userId") Integer userId, @Param("postIds") List<Integer> postIds);
}
