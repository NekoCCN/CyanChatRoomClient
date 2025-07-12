package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record RemoveMemberRequest(UUID group_id, UUID target_user_id)
{  }