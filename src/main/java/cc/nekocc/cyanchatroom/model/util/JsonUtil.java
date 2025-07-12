package cc.nekocc.cyanchatroom.model.util;

import cc.nekocc.cyanchatroom.protocol.ProtocolMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public final class JsonUtil
{
    private static final Gson GSON = new GsonBuilder().create();

    private JsonUtil()
    {  }

    public static String serialize(Object obj)
    {
        return GSON.toJson(obj);
    }

    public static <T> T deserialize(String json, Class<T> clazz) throws JsonSyntaxException
    {
        return GSON.fromJson(json, clazz);
    }

    public static <T> ProtocolMessage<T> deserializeProtocolMessage(String json, Class<T> payloadType)
    {
        Type protocolType = TypeToken.getParameterized(ProtocolMessage.class, payloadType).getType();
        return GSON.fromJson(json, protocolType);
    }

    public static Gson getGson()
    {
        return GSON;
    }
}