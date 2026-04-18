package com.jtyu.backend.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface CommentLikeMapper {
    @Select("SELECT COUNT(*) FROM comment_like WHERE user_id = #{userId} AND comment_id = #{commentId}")
    int exists(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Insert("INSERT INTO comment_like (user_id, comment_id) VALUES (#{userId}, #{commentId})")
    int insert(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Delete("DELETE FROM comment_like WHERE user_id = #{userId} AND comment_id = #{commentId}")
    int delete(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Select("SELECT COUNT(*) FROM comment_like WHERE comment_id = #{commentId}")
    int countByCommentId(@Param("commentId") Integer commentId);
}
