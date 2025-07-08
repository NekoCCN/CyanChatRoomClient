package cc.nekocc.cyanchatroom.domain.messages;

import cc.nekocc.cyanchatroom.domain.message.Message;
import java.util.ArrayList;

public class Messages
{
    private MessagesType type_;
    private String id_;
    private ArrayList<Message> messages_;

    public MessagesType getMessagesType()
    {
        return type_;
    }
    public String getId()
    {
        return id_;
    }
    public ArrayList<Message> getMessages()
    {
        return messages_;
    }
    public boolean isGroup()
    {
        return type_ == MessagesType.GROUP;
    }
    public boolean isUser()
    {
        return type_ == MessagesType.USER;
    }
}
