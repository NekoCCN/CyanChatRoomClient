package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record FetchKeysResponse(UUID client_request_id, boolean succeed, UUID user_id, String public_key_bundle)
{  }