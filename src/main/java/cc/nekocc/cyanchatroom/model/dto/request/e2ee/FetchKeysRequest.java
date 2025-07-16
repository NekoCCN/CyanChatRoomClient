package cc.nekocc.cyanchatroom.model.dto.request.e2ee;

import java.util.UUID;

public record FetchKeysRequest(UUID client_request_id, UUID user_id)
{  }
