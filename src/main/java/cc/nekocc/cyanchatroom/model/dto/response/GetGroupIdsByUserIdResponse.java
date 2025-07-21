package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record GetGroupIdsByUserIdResponse(
        UUID client_request_id,
        UUID[] group_ids
)
{  }
