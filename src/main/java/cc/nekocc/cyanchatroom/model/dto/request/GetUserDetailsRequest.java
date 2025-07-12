package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record GetUserDetailsRequest(UUID client_request_id, UUID user_id)
{  }
