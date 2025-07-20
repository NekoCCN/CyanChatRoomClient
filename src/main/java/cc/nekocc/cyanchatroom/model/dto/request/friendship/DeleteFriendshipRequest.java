package cc.nekocc.cyanchatroom.model.dto.request.friendship;

import java.util.UUID;

public record DeleteFriendshipRequest(UUID client_request_id, UUID friendship_id, UUID request_id)
{  }
