package cc.nekocc.cyanchatroom.model;

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

/**
 * 本地持久化服务
 */
public class LocalPersistenceService
{
    public LocalPersistenceService()
    {
        MyBatisUtil.init(); // 确保数据库和表已初始化
    }

    // --- Message Methods ---

    public void saveMessage(Message message)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(MessageMapper.class).insertMessage(message));
    }

    public void saveMessagesBatch(List<Message> messages)
    {
        if (messages == null || messages.isEmpty()) return;
        MyBatisUtil.executeUpdate(session -> session.getMapper(MessageMapper.class).insertMessagesBatch(messages));
    }

    public List<Message> getMessagesForConversation(UUID conversation_id, int limit, int offset)
    {
        return MyBatisUtil.executeQuery(session -> session.getMapper(MessageMapper.class).findMessagesByRange(conversation_id, limit, offset));
    }

    public int countMessagesInConversation(UUID conversation_id)
    {
        return MyBatisUtil.executeQuery(session -> session.getMapper(MessageMapper.class).countMessagesInConversation(conversation_id));
    }

    public void deleteOldestMessages(UUID conversation_id, int count)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(MessageMapper.class).deleteOldestMessages(conversation_id, count));
    }

    public void upsertConversation(UUID conversation_id, ConversationType type, OffsetDateTime last_message_time)
    {
        String time_string = last_message_time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        MyBatisUtil.executeUpdate(session -> session.getMapper(ConversationMapper.class).upsertConversation(conversation_id, type, time_string));
    }

    public List<Conversation> getAllConversations()
    {
        return MyBatisUtil.executeQuery(session -> session.getMapper(ConversationMapper.class).findAll());
    }
}