package cc.nekocc.cyanchatroom.model.entity;

import com.github.f4b6a3.uuid.UuidCreator;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Friendship
{
    private UUID id_;

    private UUID user_one_id_;
    private UUID user_two_id_;
    private FriendshipRequestStatus status_;

    private UUID action_user_id_ ;

    private OffsetDateTime created_at_;
    private OffsetDateTime updated_at_;

    public Friendship()
    {  }

    public Friendship(UUID user_one_id, UUID user_two_id, UUID action_user_id)
    {
        id_ = UuidCreator.getTimeOrderedEpoch();
        user_one_id_ = user_one_id;
        user_two_id_ = user_two_id;
        action_user_id_ = action_user_id;
        status_ = FriendshipRequestStatus.PENDING;
        created_at_ = OffsetDateTime.now();
        updated_at_ = created_at_;
    }

    public Friendship acceptRequest(UUID user_id)
    {
        if (user_id.equals(action_user_id_) && (user_id.equals(user_one_id_) || user_id.equals(user_two_id_)))
        {
            status_ = FriendshipRequestStatus.ACCEPTED;
            updated_at_ = OffsetDateTime.now();
        }
        else
        {
            throw new IllegalArgumentException("User is not authorized to accept this request.");
        }
        return this;
    }

    public Friendship rejectRequest(UUID user_id)
    {
        if (user_id.equals(action_user_id_) && (user_id.equals(user_one_id_) || user_id.equals(user_two_id_)))
        {
            status_ = FriendshipRequestStatus.REJECTED;
            updated_at_ = OffsetDateTime.now();
        }
        else
        {
            throw new IllegalArgumentException("User is not authorized to reject this request.");
        }
        return this;
    }

    public UUID getId()
    {
        return id_;
    }

    public UUID getUserOneId()
    {
        return user_one_id_;
    }

    public FriendshipRequestStatus getStatus()
    {
        return status_;
    }

    public UUID getUserTwoId()
    {
        return user_two_id_;
    }

    public UUID getActionUserId()
    {
        return action_user_id_;
    }

    public OffsetDateTime getCreatedAt()
    {
        return created_at_;
    }

    public OffsetDateTime getUpdatedAt()
    {
        return updated_at_;
    }
}
