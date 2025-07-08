package cc.nekocc.cyanchatroom.domain.message;

import java.time.OffsetDateTime;

public class Message
{
    private MessageType type_;
    private String val_;
    private OffsetDateTime time_;
    private String uuid_;

    public OffsetDateTime getOffsetTime()
    {
        return time_;
    }
    public MessageType getMessageType()
    {
        return type_;
    }
    public boolean isText()
    {
        return type_ == MessageType.TEXT;
    }
    public boolean isImage()
    {
        return type_ == MessageType.IMAGE;
    }
    public boolean isFileId()
    {
        return type_ == MessageType.FILE_ID;
    }

    public String getValue()
    {
        return val_;
    }
}
