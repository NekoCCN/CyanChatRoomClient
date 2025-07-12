package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record SetMemberRoleRequest(UUID group_id, UUID target_user_id, String new_role)
{  }