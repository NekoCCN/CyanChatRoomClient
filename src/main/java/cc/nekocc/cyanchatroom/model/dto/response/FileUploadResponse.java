package cc.nekocc.cyanchatroom.model.dto.response;

import java.util.UUID;

public record FileUploadResponse(String file_id, String upload_url, String client_id)
{  }