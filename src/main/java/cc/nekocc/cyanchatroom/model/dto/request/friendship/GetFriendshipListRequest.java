package cc.nekocc.cyanchatroom.model.dto.request.friendship;

import java.util.UUID;

public record GetFriendshipListRequest(UUID client_request_id, UUID user_id)
{  }
