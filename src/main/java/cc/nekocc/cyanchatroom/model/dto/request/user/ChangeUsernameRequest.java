package cc.nekocc.cyanchatroom.model.dto.request.user;

import java.util.UUID;

public record ChangeUsernameRequest(UUID client_request_id, String new_user_name, String current_password)
{  }
