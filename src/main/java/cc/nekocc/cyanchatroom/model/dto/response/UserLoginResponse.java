package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record UserLoginResponse(boolean success, String message, UserDTO user)
{
    public record UserDTO(UUID id, String nick_name, String avatar_url, String signature)
    {  }
}