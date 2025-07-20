package cc.nekocc.cyanchatroom.model.dto.request;

/*
Example JSON for REQUEST_FILE_UPLOAD:
{
	"type": "REQUEST_FILE_UPLOAD",
	"payload":
	{
			"file_name": "无情的测试文件",
			"file_size": 9215976,
			"expires_in_hours": 1
	}
}
 */
public record FileUploadRequest(String file_name, Long file_size,
                                Integer expires_in_hours, String client_request_id)
{  }
