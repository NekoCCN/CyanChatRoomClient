package cc.nekocc.cyanchatroom.model.dto.request;

public record ChangePasswordRequest(String current_password, String new_password)
{  }