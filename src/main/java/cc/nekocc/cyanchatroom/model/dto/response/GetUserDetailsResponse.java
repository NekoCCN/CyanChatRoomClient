package cc.nekocc.cyanchatroom.model.dto.response;

import cc.nekocc.cyanchatroom.model.entity.UserStatus;
import java.util.UUID;

public record GetUserDetailsResponse(UUID client_request_id, boolean request_status, String username, UUID user_id,
                                     String nick_name, String avatar_url, String signature, UserStatus status,
                                     boolean is_online, boolean enabled_key)
{  }
