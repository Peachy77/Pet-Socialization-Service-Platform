package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {
    // ========== 动态查询 ==========

    @Select("SELECT p.post_id, p.user_id, p.content, p.images, p.tags, p.like_count, p.comment_count, p.create_time, " +
            "u.username, u.avatar " +
            "FROM post p JOIN user u ON p.user_id = u.user_id WHERE p.post_id = #{postId}")
    Map<String, Object> selectById(@Param("postId") Integer postId);

    // 获取动态列表（支持关键词、标签搜索）
    @Select("<script>" +
            "SELECT p.post_id, p.user_id, p.content, p.images, p.tags, p.like_count, p.comment_count, p.create_time, " +
            "u.username, u.avatar " +
            "FROM post p JOIN user u ON p.user_id = u.user_id WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'> AND p.content LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "<if test='tag != null and tag != \"\"'> AND JSON_SEARCH(p.tags, 'one', #{tag}) IS NOT NULL</if>" +
            " ORDER BY p.create_time DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> selectList(@Param("keyword") String keyword,
                                         @Param("tag") String tag,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM post p WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'> AND p.content LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "<if test='tag != null and tag != \"\"'> AND JSON_SEARCH(p.tags, 'one', #{tag}) IS NOT NULL</if>" +
            "</script>")
    Long countList(@Param("keyword") String keyword, @Param("tag") String tag);

    // 获取用户的动态列表
    @Select("SELECT p.post_id, p.user_id, p.content, p.images, p.tags, p.like_count, p.comment_count, p.create_time, " +
            "u.username, u.avatar " +
            "FROM post p JOIN user u ON p.user_id = u.user_id " +
            "WHERE p.user_id = #{userId} ORDER BY p.create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectByUserId(@Param("userId") Integer userId,
                                             @Param("offset") Integer offset,
                                             @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM post WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Integer userId);

    // ========== 动态操作 ==========

    @Insert("INSERT INTO post (user_id, content, images, tags) VALUES (#{userId}, #{content}, #{images}, #{tags})")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    int insert(Post post);

    @Delete("DELETE FROM post WHERE post_id = #{postId}")
    int deleteById(@Param("postId") Integer postId);

    @Update("UPDATE post SET like_count = like_count + 1 WHERE post_id = #{postId}")
    int incrementLikeCount(@Param("postId") Integer postId);

    @Update("UPDATE post SET like_count = like_count - 1 WHERE post_id = #{postId}")
    int decrementLikeCount(@Param("postId") Integer postId);

    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE post_id = #{postId}")
    int incrementCommentCount(@Param("postId") Integer postId);

    @Update("UPDATE post SET comment_count = comment_count - 1 WHERE post_id = #{postId}")
    int decrementCommentCount(@Param("postId") Integer postId);

    // 检查动态是否存在
    @Select("SELECT COUNT(*) FROM post WHERE post_id = #{postId}")
    int existsById(@Param("postId") Integer postId);

    // 检查是否是自己的动态
    @Select("SELECT user_id FROM post WHERE post_id = #{postId}")
    Integer selectUserIdByPostId(@Param("postId") Integer postId);
}
