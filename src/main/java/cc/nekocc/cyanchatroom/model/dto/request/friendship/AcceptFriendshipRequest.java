package cc.nekocc.cyanchatroom.model.dto.request.friendship;

import java.util.UUID;

public record AcceptFriendshipRequest(UUID client_request_id, UUID request_id)
{  }
