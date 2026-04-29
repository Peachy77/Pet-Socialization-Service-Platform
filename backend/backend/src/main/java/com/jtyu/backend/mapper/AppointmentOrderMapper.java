package com.jtyu.backend.mapper;

import com.jtyu.backend.model.AppointmentOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppointmentOrderMapper {
    // ========== 订单查询 ==========

    @Select("SELECT o.order_id as orderId, o.user_id as userId, o.service_id as serviceId, o.project_name as projectName, " +
            "o.appointment_time as appointmentTime, o.remark, o.status, o.price, o.create_time as createTime, "+
            "s.name as serviceName, s.address as serviceAddress, " +
            "u.username, u.avatar " +
            "FROM `order` o " +
            "JOIN service s ON o.service_id = s.service_id " +
            "JOIN user u ON o.user_id = u.user_id " +
            "WHERE o.order_id = #{orderId}")
    Map<String, Object> selectById(@Param("orderId") Integer orderId);

    @Select("<script>" +
            "SELECT o.order_id as orderId, o.user_id as userId, o.service_id as serviceId, o.project_name as projectName, o.appointment_time as appointmentTime, " +
            "o.remark, o.status, o.price, o.create_time as createTime, " +
            "s.name as serviceName, s.address as serviceAddress " +
            "FROM `order` o JOIN service s ON o.service_id = s.service_id " +
            "WHERE o.user_id = #{userId} " +
            "<if test='status != null and status != \"\"'> AND o.status = #{status}</if>" +
            " ORDER BY o.create_time DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> selectByUserId(@Param("userId") Integer userId,
                                             @Param("status") String status,
                                             @Param("offset") Integer offset,
                                             @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM `order` WHERE user_id = #{userId} " +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            "</script>")
    Long countByUserId(@Param("userId") Integer userId, @Param("status") String status);

    // ========== 订单操作 ==========

    @Insert("INSERT INTO `order` (user_id, service_id, project_name, appointment_time, remark, price, status) " +
            "VALUES (#{userId}, #{serviceId}, #{projectName}, #{appointmentTime}, #{remark}, #{price}, 'pending')")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int insert(AppointmentOrder order);

    @Update("UPDATE `order` SET status = #{status} WHERE order_id = #{orderId}")
    int updateStatus(@Param("orderId") Integer orderId, @Param("status") String status);

    @Delete("DELETE FROM `order` WHERE order_id = #{orderId} AND user_id = #{userId}")
    int deletePending(@Param("orderId") Integer orderId, @Param("userId") Integer userId);

    // 检查订单是否存在
    @Select("SELECT COUNT(*) FROM `order` WHERE order_id = #{orderId}")
    int existsById(@Param("orderId") Integer orderId);

    // 检查订单是否属于用户
    @Select("SELECT user_id FROM `order` WHERE order_id = #{orderId}")
    Integer selectUserIdByOrderId(@Param("orderId") Integer orderId);
}
