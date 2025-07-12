package cc.nekocc.cyanchatroom.model.dto.request;

import com.google.gson.JsonObject;

public record PublishKeysRequest(JsonObject key_bundle)
{  }