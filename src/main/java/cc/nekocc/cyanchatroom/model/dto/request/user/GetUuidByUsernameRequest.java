package cc.nekocc.cyanchatroom.model.dto.request.user;

import java.util.UUID;

public record GetUuidByUsernameRequest(UUID client_request_id, String username)
{  }
