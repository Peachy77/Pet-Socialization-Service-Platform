package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("SELECT * FROM `order` ORDER BY create_time DESC")
    List<Order> findAll();

    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM `order` WHERE order_id = #{orderId}")
    Order findById(@Param("orderId") Integer orderId);

    @Insert("INSERT INTO `order` (user_id, service_id, appointment_time, remark, price) " +
            "VALUES (#{userId}, #{serviceId}, #{appointmentTime}, #{remark}, #{price})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int insert(Order order);

    @Update("UPDATE `order` SET appointment_time = #{appointmentTime}, remark = #{remark}, price = #{price} WHERE order_id = #{orderId}")
    int update(Order order);

    @Delete("DELETE FROM `order` WHERE order_id = #{orderId}")
    int deleteById(@Param("orderId") Integer orderId);

    @Update("UPDATE `order` SET status = #{status} WHERE order_id = #{orderId}")
    int updateStatus(@Param("orderId") Integer orderId, @Param("status") String status);
}
