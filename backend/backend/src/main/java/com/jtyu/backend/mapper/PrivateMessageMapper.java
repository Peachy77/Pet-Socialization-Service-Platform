package com.jtyu.backend.mapper;

import com.jtyu.backend.model.PrivateMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PrivateMessageMapper {
    // ========== 私信查询 ==========

    // 获取会话列表（最近联系人 + 最后一条消息）
    @Select("SELECT " +
            "  CASE WHEN pm.sender_id = #{userId} THEN pm.receiver_id ELSE pm.sender_id END as userId, " +
            "  u.username, u.avatar, " +
            "  pm.content as lastMessage, " +
            "  DATE_FORMAT(pm.create_time, '%Y-%m-%d %H:%i:%s') as lastMessageTime " +
            "FROM private_message pm " +
            "JOIN user u ON (CASE WHEN pm.sender_id = #{userId} THEN pm.receiver_id ELSE pm.sender_id END) = u.user_id " +
            "WHERE pm.message_id IN ( " +
            "  SELECT MAX(message_id) FROM private_message " +
            "  WHERE sender_id = #{userId} OR receiver_id = #{userId} " +
            "  GROUP BY CASE WHEN sender_id = #{userId} THEN receiver_id ELSE sender_id END " +
            ") " +
            "ORDER BY pm.create_time DESC")
    List<Map<String, Object>> selectConversationList(@Param("userId") Integer userId);

    // 获取与某个用户的聊天记录
    @Select("SELECT pm.message_id, pm.sender_id, pm.receiver_id, pm.content, pm.images, pm.is_read, pm.create_time, " +
            "u1.username as senderUsername, u1.avatar as senderAvatar, " +
            "u2.username as receiverUsername, u2.avatar as receiverAvatar " +
            "FROM private_message pm " +
            "JOIN user u1 ON pm.sender_id = u1.user_id " +
            "JOIN user u2 ON pm.receiver_id = u2.user_id " +
            "WHERE (pm.sender_id = #{userId} AND pm.receiver_id = #{targetUserId}) " +
            "OR (pm.sender_id = #{targetUserId} AND pm.receiver_id = #{userId}) " +
            "ORDER BY pm.create_time ASC LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectConversation(@Param("userId") Integer userId,
                                                 @Param("targetUserId") Integer targetUserId,
                                                 @Param("offset") Integer offset,
                                                 @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM private_message WHERE " +
            "(sender_id = #{userId} AND receiver_id = #{targetUserId}) " +
            "OR (sender_id = #{targetUserId} AND receiver_id = #{userId})")
    Long countConversation(@Param("userId") Integer userId, @Param("targetUserId") Integer targetUserId);

    // ========== 私信操作 ==========

    @Insert("INSERT INTO private_message (sender_id, receiver_id, content, images) " +
            "VALUES (#{senderId}, #{receiverId}, #{content}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    int insert(PrivateMessage message);

    // 标记已读（接收方读取所有来自发送方的消息）
    @Update("UPDATE private_message SET is_read = 1 " +
            "WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    int markAsRead(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

    // ========== 未读统计 ==========

    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{receiverId} AND is_read = 0")
    Long countUnread(@Param("receiverId") Integer receiverId);

    // 获取未读私信按发送方分组
    @Select("SELECT sender_id, COUNT(*) as unreadCount " +
            "FROM private_message " +
            "WHERE receiver_id = #{receiverId} AND is_read = 0 " +
            "GROUP BY sender_id")
    List<Map<String, Object>> selectUnreadGroupBySender(@Param("receiverId") Integer receiverId);

}
