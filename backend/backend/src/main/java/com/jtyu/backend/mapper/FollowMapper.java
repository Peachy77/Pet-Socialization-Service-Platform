package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Follow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {
    @Select("SELECT * FROM follow WHERE followee_id = #{userId}")
    List<Follow> findFollowersByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM follow WHERE follower_id = #{userId}")
    List<Follow> findFollowingsByUserId(@Param("userId") Integer userId);
    @Select("SELECT * FROM follow WHERE follow_id = #{followId}")
    Follow findById(@Param("followId") Integer followId);

    @Select("SELECT * FROM follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    Follow findByFollowerAndFollowee(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);

    @Insert("INSERT INTO follow (follower_id, followee_id) VALUES (#{followerId}, #{followeeId})")
    @Options(useGeneratedKeys = true, keyProperty = "followId")
    int insert(Follow follow);

    @Delete("DELETE FROM follow WHERE follow_id = #{followId}")
    int deleteById(@Param("followId") Integer followId);

    @Delete("DELETE FROM follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    int deleteByFollowerAndFollowee(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);

    @Select("SELECT COUNT(*) FROM follow WHERE followee_id = #{userId}")
    int countFollowers(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM follow WHERE follower_id = #{userId}")
    int countFollowings(@Param("userId") Integer userId);
}
