package cc.nekocc.cyanchatroom.model.entity;

import java.util.UUID;

public class Message
{
    private UUID uuid_;
    private UUID conversation_id_;
    private String type_;
    private String val_;
    private String time_;

    public UUID getUuid()
    {
        return uuid_;
    }
    public void setUuid(UUID uuid)
    {
        uuid_ = uuid;
    }
    public UUID getConversationId()
    {
        return conversation_id_;
    }
    public void setConversationId(UUID conversation_id)
    {
        conversation_id_ = conversation_id;
    }
    public String getType()
    {
        return type_;
    }
    public void setType(String type)
    {
        type_ = type;
    }
    public String getVal()
    {
        return val_;
    }
    public void setVal(String val)
    {
        val_ = val;
    }
    public String getTime()
    {
        return time_;
    }
    public void setTime(String time)
    {
        time_ = time;
    }
}
