package cc.nekocc.cyanchatroom.model.entity;

import cc.nekocc.cyanchatroom.model.dto.response.UserLoginResponse;
import java.util.UUID;

public class User
{
    private UUID id_;
    private String nick_name_;
    private String avatar_url_;
    private String signature_;

    public User(UserLoginResponse.UserDTO dto)
    {
        this.id_ = dto.id();
        this.nick_name_ = dto.nick_name();
        this.avatar_url_ = dto.avatar_url();
        this.signature_ = dto.signature();
    }

    public UUID getId()
    {
        return id_;
    }

}
