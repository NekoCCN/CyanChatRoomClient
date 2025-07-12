package cc.nekocc.cyanchatroom.protocol;

/**
 * 和服务端的 Websocket 协议规定
 * 使用非项目规定的下划线命名法（成员变量末尾无_）来处理 JSON
 * @param <T> Payload type
 */
public class ProtocolMessage<T>
{
    private String type;
    private T payload;

    public ProtocolMessage()
    {  }

    public ProtocolMessage(String type, T payload)
    {
        this.type = type;
        this.payload = payload;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public T getPayload()
    {
        return payload;
    }

    public void setPayload(T payload)
    {
        this.payload = payload;
    }
}