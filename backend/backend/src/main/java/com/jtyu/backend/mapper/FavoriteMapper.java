package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    @Select("SELECT * FROM favorite WHERE user_id = #{userId}")
    List<Favorite> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND service_id = #{serviceId}")
    Favorite findByUserAndService(@Param("userId") Integer userId, @Param("serviceId") Integer serviceId);

    @Insert("INSERT INTO favorite (user_id, service_id) VALUES (#{userId}, #{serviceId})")
    @Options(useGeneratedKeys = true, keyProperty = "favoriteId")
    int insert(Favorite favorite);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND service_id = #{serviceId}")
    int deleteByUserAndService(@Param("userId") Integer userId, @Param("serviceId") Integer serviceId);

    @Select("SELECT COUNT(*) FROM favorite WHERE service_id = #{serviceId}")
    int countByServiceId(@Param("serviceId") Integer serviceId);
}
