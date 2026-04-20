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

    // 批量查询当前用户关注了哪些用户（在指定用户ID列表中）
    @Select("<script>" +
            "SELECT followee_id FROM follow WHERE follower_id = #{followerId} " +
            "AND followee_id IN " +
            "<foreach collection='followeeIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Integer> selectFollowingIds(@Param("followerId") Integer followerId,
                                     @Param("followeeIds") List<Integer> followeeIds);
}
