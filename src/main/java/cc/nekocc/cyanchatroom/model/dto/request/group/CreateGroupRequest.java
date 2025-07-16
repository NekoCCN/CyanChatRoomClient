package cc.nekocc.cyanchatroom.model.dto.request.group;

import java.util.List;
import java.util.UUID;

public record CreateGroupRequest(UUID client_request_id, String group_name, List<UUID> member_ids)
{  }
