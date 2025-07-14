package cc.nekocc.cyanchatroom.model.factories;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.model.dto.response.GetUserDetailsResponse;
import cc.nekocc.cyanchatroom.model.entity.UserStatus;

public class StatusFactory
{
    public static Status fromUser(GetUserDetailsResponse user)
    {
        if (user == null)
        {
            return null;
        }

        if (!user.is_online())
        {
            return Status.OFFLINE;
        }

        return switch (user.status())
        {
            case ONLINE -> Status.ONLINE;
            case BUSY -> Status.BUSY;
            case AWAY -> Status.AWAY;
            case DO_NOT_DISTURB -> Status.DO_NOT_DISTURB;
        };
    }

    public static UserStatus fromStatus(Status status)
    {
        return switch (status)
        {
            case ONLINE -> UserStatus.ONLINE;
            case BUSY -> UserStatus.BUSY;
            case AWAY -> UserStatus.AWAY;
            case DO_NOT_DISTURB -> UserStatus.DO_NOT_DISTURB;
            case OFFLINE -> null;
        };
    }
}
