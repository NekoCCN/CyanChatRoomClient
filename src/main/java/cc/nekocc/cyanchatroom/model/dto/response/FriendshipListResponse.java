package cc.nekocc.cyanchatroom.model.dto.response;

import cc.nekocc.cyanchatroom.model.entity.Friendship;
import java.util.List;
import java.util.UUID;

public record FriendshipListResponse(UUID client_request_id, boolean status, List<Friendship> friendships)
{  }
