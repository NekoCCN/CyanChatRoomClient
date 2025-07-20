package cc.nekocc.cyanchatroom.features.chatpage.contactagree;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.dto.response.GetUserDetailsResponse;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ContactAgreeViewModel
{

    private final StringProperty search_username_ = new SimpleStringProperty("");
    private final ObservableList<FriendRequestViewModel> friend_requests_ = FXCollections.observableArrayList();
    private final Runnable on_action_callback_;

    public ContactAgreeViewModel(Runnable onActionCallback)
    {
        this.on_action_callback_ = onActionCallback;
    }

    public void refreshRequests()
    {
        UUID current_user_uuid = AppRepository.getInstance().currentUserProperty().get().getId();
        AppRepository.getInstance().getFriendshipList(current_user_uuid).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response != null && response.getPayload().status())
                {
                    var newRequests = response.getPayload().friendships().stream()
                            .map(friendship -> new FriendRequestViewModel(friendship, this::handleRequestAction))
                            .collect(Collectors.toList());
                    friend_requests_.setAll(newRequests);
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "无法获取好友列表");
                }
            });
        });


    }

    private void handleRequestAction(FriendRequestViewModel processedVm)
    {
        refreshRequests();
        if (on_action_callback_ != null)
        {
            on_action_callback_.run();
        }
    }

    public CompletableFuture<GetUserDetailsResponse> searchUser()
    {
        final CompletableFuture<GetUserDetailsResponse> resultFuture = new CompletableFuture<>();
        String username = search_username_.get().trim();
        if (username.isEmpty())
        {
            resultFuture.completeExceptionally(new IllegalArgumentException("用户名不能为空"));
            return resultFuture;
        }

        AppRepository.getInstance().getUuidByUsername(username)
                .thenCompose(uuidResponse ->
                {
                    if (!uuidResponse.getPayload().request_status())
                    {
                        throw new RuntimeException("用户不存在");
                    }
                    return AppRepository.getInstance().getUserDetails(uuidResponse.getPayload().user_id());
                })
                .thenAccept(detailsResponse ->
                {
                    if (!detailsResponse.getPayload().request_status())
                    {
                        throw new RuntimeException("无法获取用户信息");
                    }
                    resultFuture.complete(detailsResponse.getPayload());
                })
                .exceptionally(ex ->
                {
                    resultFuture.completeExceptionally(ex.getCause() != null ? ex.getCause() : ex);
                    return null;
                });

        return resultFuture;
    }

    public void sendFriendRequest(UUID targetUserId)
    {
        AppRepository.getInstance().sendFriendshipRequest(targetUserId).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().status())
                {
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "好友请求已发送");
                    refreshRequests();
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "失败", "发送好友请求失败:"+response.getPayload().message());
                }
            });
        });
    }

    public StringProperty searchUsernameProperty()
    {
        return search_username_;
    }

    public ObservableList<FriendRequestViewModel> getFriendRequests()
    {
        return friend_requests_;
    }
}