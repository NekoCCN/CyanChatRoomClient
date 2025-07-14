package cc.nekocc.cyanchatroom.model.entity;

import java.util.UUID;

public class Conversation
{
    private UUID id_;
    private UUID sender_id_;
    private ConversationType type_;
    private String last_message_time_;

    public Conversation()
    {
    }

    public Conversation(UUID id, UUID sender_id, ConversationType type, String last_message_time)
    {
        this.id_ = id;
        this.sender_id_ = sender_id;
        this.type_ = type;
        this.last_message_time_ = last_message_time;
    }

    public UUID getId()
    {
        return id_;
    }

    public void setId(UUID id)
    {
        this.id_ = id;
    }

    public ConversationType getType()
    {
        return type_;
    }

    public void setType(ConversationType type)
    {
        this.type_ = type;
    }

    public String getLastMessageTime()
    {
        return last_message_time_;
    }

    public void setLastMessageTime(String last_message_time)
    {
        this.last_message_time_ = last_message_time;
    }

    @Override
    public int hashCode()
    {
        return id_.hashCode();
    }
}