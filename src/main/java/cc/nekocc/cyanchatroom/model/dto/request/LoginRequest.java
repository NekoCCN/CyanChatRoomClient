package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

/*
Example JSON for REGISTER_REQUEST:
{
    "type": "LOGIN_REQUEST",
    "payload":
    {
        "username": "Neko",
        "password": "143150151"
    }
}
 */
public record LoginRequest(UUID client_request_id, String username, String password)
{  }
