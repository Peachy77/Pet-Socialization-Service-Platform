package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper {
    // ========== 通知查询 ==========

    @Select("SELECT m.message_id, m.receiver_id, m.sender_id, m.type, m.content, m.related_id, m.is_read, m.create_time, " +
            "u.username as senderUsername, u.avatar as senderAvatar " +
            "FROM message m " +
            "LEFT JOIN user u ON m.sender_id = u.user_id " +
            "WHERE m.receiver_id = #{receiverId} " +
            "ORDER BY m.create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectByReceiverId(@Param("receiverId") Integer receiverId,
                                                 @Param("offset") Integer offset,
                                                 @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{receiverId} AND is_read = 0")
    Long countUnread(@Param("receiverId") Integer receiverId);

    // ========== 通知操作 ==========

    @Insert("INSERT INTO message (receiver_id, sender_id, type, content, related_id) " +
            "VALUES (#{receiverId}, #{senderId}, #{type}, #{content}, #{relatedId})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    int insert(Message message);

    @Update("UPDATE message SET is_read = 1 WHERE message_id = #{messageId}")
    int markAsRead(@Param("messageId") Integer messageId);

    @Update("UPDATE message SET is_read = 1 WHERE receiver_id = #{receiverId} AND is_read = 0")
    int markAllAsRead(@Param("receiverId") Integer receiverId);

    @Delete("DELETE FROM message WHERE message_id = #{messageId}")
    int deleteById(@Param("messageId") Integer messageId);

}
