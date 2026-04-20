package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    // ========== 评论查询 ==========

//    @Select("SELECT c.comment_id, c.user_id, c.post_id, c.parent_comment_id, c.content, c.images, c.create_time, " +
//            "u.username, u.avatar " +
//            "FROM comment c JOIN user u ON c.user_id = u.user_id " +
//            "WHERE c.post_id = #{postId} AND (c.parent_comment_id = 0 OR c.parent_comment_id IS NULL) " +
//            "ORDER BY c.create_time ASC LIMIT #{offset}, #{pageSize}")
//    List<Map<String, Object>> selectByPostId(@Param("postId") Integer postId,
//                                             @Param("offset") Integer offset,
//                                             @Param("pageSize") Integer pageSize);

    @Select("SELECT c.comment_id, c.user_id, c.post_id, c.parent_comment_id, c.content, c.images, c.create_time, " +
            "u.username, u.avatar, " +
            "COALESCE((SELECT COUNT(*) FROM comment_like WHERE comment_id = c.comment_id), 0) AS like_count, " +
            "COALESCE((SELECT COUNT(*) FROM comment_like WHERE comment_id = c.comment_id AND user_id = #{currentUserId}), 0) > 0 AS is_liked " +
            "FROM comment c JOIN user u ON c.user_id = u.user_id " +
            "WHERE c.post_id = #{postId} AND (c.parent_comment_id = 0 OR c.parent_comment_id IS NULL) " +
            "ORDER BY c.create_time ASC LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectByPostId(@Param("postId") Integer postId,
                                             @Param("offset") Integer offset,
                                             @Param("pageSize") Integer pageSize,
                                             @Param("currentUserId") Integer currentUserId);

    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId} AND (parent_comment_id = 0 OR parent_comment_id IS NULL)")
    Long countByPostId(@Param("postId") Integer postId);

//    @Select("SELECT c.comment_id, c.user_id, c.post_id, c.parent_comment_id, c.content, c.images, c.create_time, " +
//            "u.username, u.avatar " +
//            "FROM comment c JOIN user u ON c.user_id = u.user_id " +
//            "WHERE c.parent_comment_id = #{parentCommentId} ORDER BY c.create_time ASC")
//    List<Map<String, Object>> selectRepliesByParentId(@Param("parentCommentId") Integer parentCommentId);

    @Select("SELECT c.comment_id, c.user_id, c.post_id, c.parent_comment_id, c.content, c.images, c.create_time, " +
            "u.username, u.avatar, " +
            "COALESCE((SELECT COUNT(*) FROM comment_like WHERE comment_id = c.comment_id), 0) AS like_count, " +
            "COALESCE((SELECT COUNT(*) FROM comment_like WHERE comment_id = c.comment_id AND user_id = #{currentUserId}), 0) > 0 AS is_liked " +
            "FROM comment c JOIN user u ON c.user_id = u.user_id " +
            "WHERE c.parent_comment_id = #{parentCommentId} ORDER BY c.create_time ASC")
    List<Map<String, Object>> selectRepliesByParentId(@Param("parentCommentId") Integer parentCommentId,
                                                      @Param("currentUserId") Integer currentUserId);

    @Select("SELECT COUNT(*) FROM comment WHERE parent_comment_id = #{parentCommentId}")
    Long countRepliesByParentId(@Param("parentCommentId") Integer parentCommentId);

    @Select("SELECT c.comment_id, c.user_id, c.post_id, c.parent_comment_id, c.content, c.images, c.create_time, " +
            "u.username, u.avatar " +
            "FROM comment c JOIN user u ON c.user_id = u.user_id " +
            "WHERE c.comment_id = #{commentId}")
    Map<String, Object> selectById(@Param("commentId") Integer commentId);

    // ========== 评论操作 ==========

    @Insert("INSERT INTO comment (user_id, post_id, content, images, parent_comment_id) " +
            "VALUES (#{userId}, #{postId}, #{content}, #{images}, #{parentCommentId})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insert(Comment comment);

    @Delete("DELETE FROM comment WHERE comment_id = #{commentId}")
    int deleteById(@Param("commentId") Integer commentId);

    @Delete("DELETE FROM comment WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Integer postId);

    // 检查评论是否存在
    @Select("SELECT COUNT(*) FROM comment WHERE comment_id = #{commentId}")
    int existsById(@Param("commentId") Integer commentId);

    // 检查是否是自己的评论
    @Select("SELECT user_id FROM comment WHERE comment_id = #{commentId}")
    Integer selectUserIdByCommentId(@Param("commentId") Integer commentId);
}
