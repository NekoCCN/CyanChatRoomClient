package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record StatusResponse(UUID client_request_id, boolean status, String message, String request_type)
{
}
