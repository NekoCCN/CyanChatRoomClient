package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

/*
{
	"type": "CHAT_MESSAGE",
	"payload": {
		"recipient_type": "USER",
		"recipient_id": "0197ef89-9434-7056-ba9d-ba56aba677a1",
		"content_type": "TEXT",
		"is_encrypted": false,
		"content": "Hello World!!!"
	}
}
 */
public record ChatMessageRequest(UUID client_request_id, String recipient_type, UUID recipient_id,
                                 String content_type, boolean is_encrypted, String content)
{  }
