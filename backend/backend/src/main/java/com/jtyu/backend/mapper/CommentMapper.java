package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("SELECT * FROM comment WHERE post_id = #{postId} ORDER BY create_time ASC")
    List<Comment> findByPostId(@Param("postId") Integer postId);

    @Select("SELECT * FROM comment WHERE comment_id = #{commentId}")
    Comment findById(@Param("commentId") Integer commentId);

    @Insert("INSERT INTO comment (user_id, post_id, content, images) VALUES (#{userId}, #{postId}, #{content}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insert(Comment comment);

    @Update("UPDATE comment SET content = #{content}, images = #{images} WHERE comment_id = #{commentId}")
    int update(Comment comment);

    @Delete("DELETE FROM comment WHERE comment_id = #{commentId}")
    int deleteById(@Param("commentId") Integer commentId);

    @Delete("DELETE FROM comment WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId}")
    int countByPostId(@Param("postId") Integer postId);
}
