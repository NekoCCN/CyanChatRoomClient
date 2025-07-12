package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record RemoveMemberRequest(UUID client_request_id, UUID group_id, UUID target_user_id)
{  }