package cc.nekocc.cyanchatroom.model.dto.request.group;

import java.util.UUID;

/*
{
	"type": "SET_MEMBER_ROLE_REQUEST",
	"payload": {
		"group_id": "0197f84d-7e4f-727c-a3e4-54e401f3f635",
		"target_user_id": "0197ef89-9434-7056-ba9d-ba56aba677a1",
		"new_role": "ADMIN"
	}
}
 */
public record SetMemberRoleRequest(UUID client_request_id, UUID group_id, UUID target_user_id, String new_role)
{  }
