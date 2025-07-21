package cc.nekocc.cyanchatroom.model.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GroupMember
{
    private Long id_;
    private UUID group_id_;
    private UUID user_id_;
    private GroupMemberRole role_;
    private OffsetDateTime joined_at_;

    public GroupMember(UUID group_id, UUID user_id, GroupMemberRole role)
    {
        group_id_ = group_id;
        user_id_ = user_id;
        role_ = role;
        joined_at_ = OffsetDateTime.now();
    }

    public GroupMember()
    {  }

    public boolean canManage(GroupMember other_member)
    {
        if (role_ == GroupMemberRole.CREATOR) return true;
        if (role_ == GroupMemberRole.ADMIN && other_member.getRole() == GroupMemberRole.MEMBER) return true;
        return false;
    }

    public void promoteToAdmin()
    {
        if (role_ == GroupMemberRole.MEMBER)
        {
            role_ = GroupMemberRole.ADMIN;
        }
    }

    public void demoteToMember()
    {
        if (role_ == GroupMemberRole.ADMIN)
        {
            role_ = GroupMemberRole.MEMBER;
        }
    }

    public Long getId()
    {
        return id_;
    }

    public void setId(Long id)
    {
        id_ = id;
    }

    public UUID getGroupId()
    {
        return group_id_;
    }

    public void setGroupId(UUID group_id)
    {
        group_id_ = group_id;
    }

    public UUID getUserId()
    {
        return user_id_;
    }

    public void setUserId(UUID user_id)
    {
        user_id_ = user_id;
    }

    public GroupMemberRole getRole()
    {
        return role_;
    }

    public void setRole(GroupMemberRole role)
    {
        role_ = role;
    }

    public OffsetDateTime getJoinedAt()
    {
        return joined_at_;
    }

    public void setJoinedAt(OffsetDateTime joined_at)
    {
        joined_at_ = joined_at;
    }
}
