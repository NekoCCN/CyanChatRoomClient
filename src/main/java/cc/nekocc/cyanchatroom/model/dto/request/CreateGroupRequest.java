package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.List;
import java.util.UUID;

/*
{
	"type": "CREATE_GROUP_REQUEST",
	"payload": {
		"group_name": "CyanChat!",
		"member_ids": ["0197ef89-9434-7056-ba9d-ba56aba677a1"]
	}
}

 */
public record CreateGroupRequest(String group_name, List<UUID> member_ids)
{  }