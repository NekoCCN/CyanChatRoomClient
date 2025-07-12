package cc.nekocc.cyanchatroom.model.entity;

import java.util.UUID;
import com.github.f4b6a3.uuid.UuidCreator;

public class Message
{
    private UUID uuid_;
    private UUID conversation_id_;
    private UUID sender_id_;
    private boolean is_outgoing_;
    private String type_;
    private String val_;
    private String time_;

    public Message()
    {  }

    public Message(UUID recipient_id, UUID sender_id, boolean is_outgoing,
                   String content_type, String content)
    {
        uuid_ = UuidCreator.getTimeOrderedEpoch();
        conversation_id_ = recipient_id;
        sender_id_ = sender_id;
        is_outgoing_ = is_outgoing;
        type_ = content_type;
        val_ = content;
        time_ = java.time.OffsetDateTime.now().toString();
    }

    public Message(UUID recipient_id, UUID sender_id, boolean is_outgoing,
                   String content_type, String content, String time)
    {
        uuid_ = UuidCreator.getTimeOrderedEpoch();
        conversation_id_ = recipient_id;
        sender_id_ = sender_id;
        is_outgoing_ = is_outgoing;
        type_ = content_type;
        val_ = content;
        time_ = time;
    }

    public UUID getUuid()
    {
        return uuid_;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid_ = uuid;
    }

    public UUID getConversationId()
    {
        return conversation_id_;
    }

    public void setConversationId(UUID conversation_id)
    {
        this.conversation_id_ = conversation_id;
    }

    public UUID getSenderId()
    {
        return sender_id_;
    }

    public void setSenderId(UUID sender_id)
    {
        this.sender_id_ = sender_id;
    }

    public boolean isOutgoing()
    {
        return is_outgoing_;
    }

    public void setOutgoing(boolean is_outgoing)
    {
        this.is_outgoing_ = is_outgoing;
    }

    public String getType()
    {
        return type_;
    }

    public void setType(String type)
    {
        this.type_ = type;
    }

    public String getVal()
    {
        return val_;
    }

    public void setVal(String val)
    {
        this.val_ = val;
    }

    public String getTime()
    {
        return time_;
    }

    public void setTime(String time)
    {
        this.time_ = time;
    }
}