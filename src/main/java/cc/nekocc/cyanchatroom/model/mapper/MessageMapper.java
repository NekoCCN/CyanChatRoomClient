package cc.nekocc.cyanchatroom.model.mapper;

import cc.nekocc.cyanchatroom.domain.message.Message;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MessageMapper
{
    /**
     * 插入单条消息。
     *
     * @param message 要插入的消息对象
     */
    void insertMessage(Message message);

    /**
     * 批量插入多条消息。
     * 用于一次性存入一系列消息记录。
     *
     * @param messages 消息对象列表
     */
    void insertMessagesBatch(@Param("messages") List<Message> messages);

    /**
     * 查询指定会话内的所有消息。
     *
     * @param conversation_id 会话ID
     * @return 消息列表
     */
    List<Message> findAllMessagesByConversationId(@Param("conversation_id") String conversation_id);

    /**
     * 分页查询指定会话内的消息（按时间倒序）。
     *
     * @param conversation_id 会话ID
     * @param limit 获取的条数
     * @param offset 起始位置
     * @return 消息列表
     */
    List<Message> findMessagesByRange(
            @Param("conversation_id") String conversation_id,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * TODO: [缓存预留接口]
     * 删除一个会话中最古老的N条消息。
     * 当一个会话的消息数超过最大缓存限制时调用。
     *
     * @param conversation_id 会话ID
     * @param count 要删除的条数
     */
    void deleteOldestMessages(
            @Param("conversation_id") String conversation_id,
            @Param("count") int count
    );

    /**
     * TODO: [缓存预留接口]
     * 获取一个会话当前的消息总数。
     *
     * @param conversation_id 会话ID
     * @return 消息总数
     */
    int countMessagesInConversation(@Param("conversation_id") String conversation_id);
}
