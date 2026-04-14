package com.jtyu.backend.mapper;

import com.jtyu.backend.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User findById(@Param("userId") Integer userId);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Insert("INSERT INTO user (username, password, email, avatar, bio) " +
            "VALUES (#{username}, #{password}, #{email}, #{avatar}, #{bio})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    @Update("UPDATE user SET username = #{username}, email = #{email}, " +
            "avatar = #{avatar}, bio = #{bio} WHERE user_id = #{userId}")
    int update(User user);

    @Delete("DELETE FROM user WHERE user_id = #{userId}")
    int deleteById(@Param("userId") Integer userId);

    @Update("UPDATE user SET follower_count = follower_count + #{delta} WHERE user_id = #{userId}")
    int updateFollowerCount(@Param("userId") Integer userId, @Param("delta") int delta);

    @Update("UPDATE user SET following_count = following_count + #{delta} WHERE user_id = #{userId}")
    int updateFollowingCount(@Param("userId") Integer userId, @Param("delta") int delta);
}
