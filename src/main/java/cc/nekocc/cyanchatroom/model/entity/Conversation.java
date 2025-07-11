package cc.nekocc.cyanchatroom.model.entity;

import java.util.UUID;

public class Conversation
{
    private UUID id_;
    private ConversationType type_;
    private String last_message_time_;

    public Conversation()
    {  }

    public UUID getId()
    {
        return id_;
    }
    public void setId(UUID id)
    {
        id_ = id;
    }
    public ConversationType getType()
    {
        return type_;
    }
    public void setType(ConversationType type)
    {
        type_ = type;
    }
    public String getLastMessageTime()
    {
        return last_message_time_;
    }
    public void setLastMessageTime(String last_message_time)
    {
        last_message_time_ = last_message_time;
    }
}