package cc.nekocc.cyanchatroom.model.mapper;

import cc.nekocc.cyanchatroom.model.entity.Message;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.UUID;

public interface MessageMapper
{
    void insertMessage(Message message);

    void insertMessagesBatch(@Param("messages") List<Message> messages);

    List<Message> findAllMessagesByConversationId(@Param("conversation_id") UUID conversation_id);

    List<Message> findMessagesByRange(
            @Param("conversation_id") UUID conversation_id,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    void deleteOldestMessages(
            @Param("conversation_id") UUID conversation_id,
            @Param("count") int count
    );

    int countMessagesInConversation(@Param("conversation_id") UUID conversation_id);
}