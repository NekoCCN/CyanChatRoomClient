package cc.nekocc.cyanchatroom.model.dto.request.user;

import cc.nekocc.cyanchatroom.model.entity.UserStatus;
import java.util.UUID;

public record UpdateProfileRequest(UUID client_request_id, String nick_name, String signature,
                                   String avatar_file_id, UserStatus status)
{  }