package cc.nekocc.cyanchatroom.model.mapper;

import cc.nekocc.cyanchatroom.domain.messages.MessagesType;
import org.apache.ibatis.annotations.Param;

public interface ConversationMapper
{
    /**
     * 插入或更新一个会话。
     * 当插入新消息时，如果会话不存在，则会自动创建。
     * 如果已存在，则会更新其最后消息时间。
     *
     * @param conversation_id 会话ID (用户ID或群组ID)
     * @param type 会话类型 (USER 或 GROUP)
     * @param last_message_time 最后一条消息的时间戳 (ISO 8601 格式)
     */
    void upsertConversation(
            @Param("conversation_id") String conversation_id,
            @Param("type") MessagesType type,
            @Param("last_message_time") String last_message_time
    );
}