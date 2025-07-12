package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record ErrorResponse(String error, String request_type)
{  }