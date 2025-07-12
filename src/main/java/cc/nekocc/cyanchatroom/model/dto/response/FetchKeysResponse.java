package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record FetchKeysResponse(UUID user_id, String public_key_bundle)
{  }