package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record CheckFriendshipExistsResponse(UUID client_request_id, boolean exists)
{  }
