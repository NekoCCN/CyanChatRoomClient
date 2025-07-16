package cc.nekocc.cyanchatroom.model.dto.request.friendship;

import java.util.UUID;

public record CheckFriendshipExistsRequest(UUID client_request_id, UUID user_id1, UUID user_id2)
{  }
