package cc.nekocc.cyanchatroom.model.mapper;

import cc.nekocc.cyanchatroom.model.entity.Conversation;
import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.UUID;

public interface ConversationMapper
{
    void upsertConversation(
            @Param("conversation_id") UUID conversation_id,
            @Param("type") ConversationType type,
            @Param("sender_id") UUID sender_id,
            @Param("last_message_time") String last_message_time
    );

    List<Conversation> findAllConversationsByUserId(@Param("user_id") UUID user_id);
}