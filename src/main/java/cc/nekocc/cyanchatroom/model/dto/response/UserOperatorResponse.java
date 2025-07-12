package cc.nekocc.cyanchatroom.model.dto.response;

import cc.nekocc.cyanchatroom.model.entity.UserStatus;
import java.util.UUID;

public record UserOperatorResponse(UUID client_request_id, boolean success, String message, UserDTO user)
{
    public record UserDTO(UUID id, String nickname, String avatar_url, String signature, UserStatus status)
    {  }
}
