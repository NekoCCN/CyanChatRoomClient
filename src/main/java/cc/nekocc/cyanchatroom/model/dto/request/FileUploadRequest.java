package cc.nekocc.cyanchatroom.model.dto.request;

public record FileUploadRequest(String file_name, Long file_size, Integer expires_in_hours, String client_id)
{  }