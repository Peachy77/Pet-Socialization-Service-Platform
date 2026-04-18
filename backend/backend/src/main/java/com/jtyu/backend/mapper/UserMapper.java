package com.jtyu.backend.mapper;

import com.jtyu.backend.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    // ========== 认证相关 ==========

    @Select("SELECT user_id, username, email, password, avatar, bio, follower_count, following_count " +
            "FROM user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    @Insert("INSERT INTO user (username, email, password) VALUES (#{username}, #{email}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    // ========== 用户查询 ==========

    @Select("SELECT user_id, username, email, avatar, bio, follower_count, following_count " +
            "FROM user WHERE user_id = #{userId}")
    User selectById(@Param("userId") Integer userId);

    @Select("SELECT user_id, username, email, avatar, bio, follower_count, following_count " +
            "FROM user WHERE user_id = #{userId}")
    Map<String, Object> selectSimpleById(@Param("userId") Integer userId);

    // 获取用户列表（支持关键词搜索）
    @Select("<script>" +
            "SELECT user_id, username, email, avatar, bio, follower_count, following_count " +
            "FROM user WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            " ORDER BY user_id ASC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> selectList(@Param("keyword") String keyword,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM user WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</script>")
    Long countList(@Param("keyword") String keyword);

    // ========== 用户更新 ==========

    @Update("UPDATE user SET username = #{username}, avatar = #{avatar}, bio = #{bio} WHERE user_id = #{userId}")
    int update(User user);
    // 修改密码
    @Update("UPDATE user SET password = #{password} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") Integer userId, @Param("password") String password);

    // ========== 关注/粉丝统计 ==========

    @Update("UPDATE user SET follower_count = follower_count + 1 WHERE user_id = #{userId}")
    int incrementFollowerCount(@Param("userId") Integer userId);

    @Update("UPDATE user SET follower_count = follower_count - 1 WHERE user_id = #{userId}")
    int decrementFollowerCount(@Param("userId") Integer userId);

    @Update("UPDATE user SET following_count = following_count + 1 WHERE user_id = #{userId}")
    int incrementFollowingCount(@Param("userId") Integer userId);

    @Update("UPDATE user SET following_count = following_count - 1 WHERE user_id = #{userId}")
    int decrementFollowingCount(@Param("userId") Integer userId);

    // ========== 关注列表/粉丝列表 ==========

    @Select("SELECT u.user_id, u.username, u.avatar, u.bio " +
            "FROM follow f JOIN user u ON f.followee_id = u.user_id " +
            "WHERE f.follower_id = #{userId} LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectFollowing(@Param("userId") Integer userId,
                                              @Param("offset") Integer offset,
                                              @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM follow WHERE follower_id = #{userId}")
    Long countFollowing(@Param("userId") Integer userId);

    @Select("SELECT u.user_id, u.username, u.avatar, u.bio " +
            "FROM follow f JOIN user u ON f.follower_id = u.user_id " +
            "WHERE f.followee_id = #{userId} LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectFollowers(@Param("userId") Integer userId,
                                              @Param("offset") Integer offset,
                                              @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM follow WHERE followee_id = #{userId}")
    Long countFollowers(@Param("userId") Integer userId);

    // 获取用户收到的点赞总数（所有动态的 like_count 之和）
    @Select("SELECT SUM(like_count) FROM post WHERE user_id = #{userId}")
    Integer selectTotalLikeCount(@Param("userId") Integer userId);
}
