package cc.nekocc.cyanchatroom.model.service;

import cc.nekocc.cyanchatroom.model.entity.Conversation;
import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import cc.nekocc.cyanchatroom.model.entity.Message;
import cc.nekocc.cyanchatroom.model.impl.MyBatisUtil;
import cc.nekocc.cyanchatroom.model.mapper.ConversationMapper;
import cc.nekocc.cyanchatroom.model.mapper.MessageMapper;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class LocalPersistenceService
{
    public LocalPersistenceService()
    {  }

    public void saveMessage(String server_address, UUID user_id, Message message)
    {
        MyBatisUtil.executeUpdate(server_address, user_id, session -> session.getMapper(MessageMapper.class).insertMessage(message));
    }

    public void saveMessagesBatch(String server_address, UUID user_id, List<Message> messages)
    {
        if (messages == null || messages.isEmpty())
            return;
        MyBatisUtil.executeUpdate(server_address, user_id, session -> session.getMapper(MessageMapper.class).insertMessagesBatch(messages));
    }

    public List<Message> getClipedMessagesForConversation(String server_address, UUID user_id,
                                                          UUID conversation_id, int limit, int offset)
    {
        return MyBatisUtil.executeQuery(server_address, user_id, session ->
                session.getMapper(MessageMapper.class).findMessagesByRange(conversation_id, limit, offset));
    }

    public List<Message> getAllMessagesForConversation(String server_address, UUID user_id,
                                                          UUID conversation_id)
    {
        return MyBatisUtil.executeQuery(server_address, user_id, session ->
                session.getMapper(MessageMapper.class).findAllMessagesByConversationId(conversation_id));
    }

    public int countMessagesInConversation(String server_address, UUID user_id,
                                                     UUID conversation_id)
    {
        return MyBatisUtil.executeQuery(server_address, user_id, session ->
                session.getMapper(MessageMapper.class).countMessagesInConversation(conversation_id));
    }

    public void upsertConversation(String server_address, UUID user_id, UUID conversation_id,
                                   ConversationType type, UUID sender_id, OffsetDateTime last_message_time)
    {
        String time_string = last_message_time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        MyBatisUtil.executeUpdate(server_address, user_id, session -> session.getMapper(ConversationMapper.class)
                .upsertConversation(conversation_id, type, sender_id, time_string));
    }

    public List<Conversation> getAllConversations(String server_address, UUID user_id)
    {
        return MyBatisUtil.executeQuery(server_address, user_id, session -> session.getMapper(ConversationMapper.class).findAllConversationsByUserId(user_id));
    }

    public void deleteOldestMessages(String server_address, UUID user_id, UUID conversation_id, int limit)
    {
        MyBatisUtil.executeUpdate(server_address, user_id, session -> session.getMapper(MessageMapper.class).deleteOldestMessages(conversation_id, limit));
    }
}