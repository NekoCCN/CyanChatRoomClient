package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record JoinGroupRequest(UUID client_request_id, UUID group_id, String request_message)
{  }