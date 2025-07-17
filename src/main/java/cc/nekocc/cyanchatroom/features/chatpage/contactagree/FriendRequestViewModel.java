package cc.nekocc.cyanchatroom.features.chatpage.contactagree;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.Friendship;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.Alert;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class FriendRequestViewModel
{

    private final StringProperty sender_name_ = new SimpleStringProperty("加载中...");
    private final StringProperty status_text_ = new SimpleStringProperty();
    private final BooleanProperty is_pending_from_other_ = new SimpleBooleanProperty(false);
    private final Friendship friendship_;
    private final Consumer<FriendRequestViewModel> on_action_callback_;

    public FriendRequestViewModel(Friendship friendship, Consumer<FriendRequestViewModel> onActionCallback)
    {
        this.friendship_ = friendship;
        this.on_action_callback_ = onActionCallback;
        loadUserDetails();
    }

    private void loadUserDetails()
    {
        UUID currentUserUuid = AppRepository.getInstance().currentUserProperty().get().getId();
        UUID oppositeId = friendship_.getUserOneId().equals(currentUserUuid) ? friendship_.getUserTwoId() : friendship_.getUserOneId();

        AppRepository.getInstance().getUserDetails(oppositeId).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().request_status())
                {
                    sender_name_.set(response.getPayload().nick_name());
                } else
                {
                    sender_name_.set("未知用户");
                }
                updateStatusText(currentUserUuid);
            });
        });
    }

    private void updateStatusText(UUID currentUserUuid)
    {
        switch (friendship_.getStatus())
        {
            case PENDING:
                if (friendship_.getActionUserId().equals(currentUserUuid))
                {
                    status_text_.set("等待对方接受");
                } else
                {
                    is_pending_from_other_.set(true);
                }
                break;
            case ACCEPTED:
                status_text_.set("已接受");
                break;
            case REJECTED:
                status_text_.set("已拒绝");
                break;
        }
    }

    public void acceptRequest()
    {
        AppRepository.getInstance().acceptFriendshipRequest(friendship_.getId()).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().status())
                {
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "已接受好友请求");
                    on_action_callback_.accept(this);
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "接受好友请求失败");
                }
            });
        });
    }

    public void rejectRequest()
    {
        AppRepository.getInstance().rejectFriendshipRequest(friendship_.getId()).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().status())
                {
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "已拒绝好友请求");
                    on_action_callback_.accept(this);
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "拒绝好友请求失败");
                }
            });
        });
    }

    public StringProperty senderNameProperty()
    {
        return sender_name_;
    }

    public StringProperty statusTextProperty()
    {
        return status_text_;
    }

    public BooleanProperty isPendingFromOtherProperty()
    {
        return is_pending_from_other_;
    }

    public Friendship getFriendship()
    {
        return friendship_;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FriendRequestViewModel that = (FriendRequestViewModel) o;
        return Objects.equals(friendship_.getId(), that.friendship_.getId());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(friendship_.getId());
    }
}