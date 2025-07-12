package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record GetUserDetailsResponse(UUID request_id, String username, String nick_name, String avatar_url,
                                     String signature, boolean is_online, boolean enabled_key)
{  }
