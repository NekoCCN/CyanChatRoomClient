package cc.nekocc.cyanchatroom.model.entity;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.dto.response.*;
import cc.nekocc.cyanchatroom.model.factories.StatusFactory;
import cc.nekocc.cyanchatroom.protocol.ProtocolMessage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<ProtocolMessage<GetUserDetailsResponse>> getUserDetailsResponse()
    {
        return AppRepository.getInstance().getUserDetails(id_);
    }

    public UUID getId()
    {

        return id_;
    }

}
