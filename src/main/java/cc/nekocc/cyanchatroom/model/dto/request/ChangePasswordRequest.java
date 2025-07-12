package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record ChangePasswordRequest(UUID client_request_id, String current_password, String new_password)
{  }
