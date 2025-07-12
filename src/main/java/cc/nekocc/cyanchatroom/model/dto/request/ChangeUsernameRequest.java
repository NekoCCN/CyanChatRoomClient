package cc.nekocc.cyanchatroom.model.dto.request;

public record ChangeUsernameRequest(String new_user_name, String current_password)
{  }