package cc.nekocc.cyanchatroom.model.dto.response;

import cc.nekocc.cyanchatroom.model.entity.GroupMember;
import java.util.UUID;

public record GetGroupMembersResponse(
        UUID client_request_id,
        GroupMember[] group_members
)
{  }