package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Follow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {
    @Select("SELECT COUNT(*) FROM follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    int exists(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);

    @Insert("INSERT INTO follow (follower_id, followee_id) VALUES (#{followerId}, #{followeeId})")
    int insert(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);

    @Delete("DELETE FROM follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    int delete(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);
}
