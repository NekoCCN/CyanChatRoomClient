package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record LeaveGroupRequest(UUID client_request_id, UUID group_id)
{  }