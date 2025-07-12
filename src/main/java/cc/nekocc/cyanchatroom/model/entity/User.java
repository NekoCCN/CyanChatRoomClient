package cc.nekocc.cyanchatroom.model.entity;

import cc.nekocc.cyanchatroom.model.dto.response.*;
import java.util.UUID;

public class User
{
    private UUID id_;
    private String nickname_;
    private String avatar_url_;
    private String signature_;

    public User(UserOperatorResponse.UserDTO dto)
    {
        this.id_ = dto.id();
        this.nickname_ = dto.nickname();
        this.avatar_url_ = dto.avatar_url();
        this.signature_ = dto.signature();
    }

    public UUID getId()
    {
        return id_;
    }

}
