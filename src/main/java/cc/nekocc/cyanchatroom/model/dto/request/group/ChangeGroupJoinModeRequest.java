package cc.nekocc.cyanchatroom.model.dto.request.group;

import cc.nekocc.cyanchatroom.model.entity.GroupJoinMode;
import java.util.UUID;

public record ChangeGroupJoinModeRequest(UUID client_request_id, UUID group_id, GroupJoinMode new_mode)
{  }