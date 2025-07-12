package cc.nekocc.cyanchatroom.model.dto.request;

import java.util.UUID;

public record ChangeGroupJoinModeRequest(UUID group_id, String new_mode)
{  }
