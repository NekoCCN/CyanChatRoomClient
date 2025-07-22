package cc.nekocc.cyanchatroom.features.chatpage.group;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.dto.response.GroupResponse;
import cc.nekocc.cyanchatroom.model.entity.GroupJoinMode;
import cc.nekocc.cyanchatroom.model.entity.GroupMember;
import cc.nekocc.cyanchatroom.model.entity.GroupMemberRole;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.util.UUID;

public class GroupManagementViewModel
{
    private final AppRepository app_repository_ = AppRepository.getInstance();
    private final UUID currentUserId = app_repository_.currentUserProperty().get().getId();

    private final ObservableList<GroupResponse> my_groups_ = FXCollections.observableArrayList();
    private final ObservableList<GroupMember> selected_group_members_ = FXCollections.observableArrayList();
    private final StringProperty selected_group_name_ = new SimpleStringProperty("请先在左侧选择一个群组");
    private final ObservableList<String> join_requests_ = FXCollections.observableArrayList();
    private final StringProperty join_group_id_ = new SimpleStringProperty("");
    private final StringProperty join_message_ = new SimpleStringProperty("");
    private final ObjectProperty<GroupJoinMode> selected_group_join_mode_ = new SimpleObjectProperty<>();
    private final StringProperty selected_group_id_ = new SimpleStringProperty("000-000000-000000-00000");
    public GroupManagementViewModel()
    {
        refreshMyGroups();
    }

    public void refreshMyGroups()
    {
        app_repository_.getGroupIdsByUserId(currentUserId).thenAccept(response ->
        {
            if (response != null && response.getPayload().group_ids() != null)
            {
                Platform.runLater(my_groups_::clear);
                for (UUID groupId : response.getPayload().group_ids())
                {
                    app_repository_.getGroupDetails(groupId).thenAccept(detailsResponse ->
                    {
                        if (detailsResponse != null && detailsResponse.getPayload().success())
                        {
                            Platform.runLater(() -> my_groups_.add(detailsResponse.getPayload()));
                        }
                    });
                }
            }
        });
    }

    public void selectGroup(GroupResponse selected_group)
    {
        if (selected_group == null)
        {
            selected_group_name_.set("请先在左侧选择一个群组");
            selected_group_id_.set("000-000000-000000-00000");
            selected_group_members_.clear();
            join_requests_.clear();
            selected_group_join_mode_.set(null);
            return;
        }
        selected_group_name_.set("群组名称: " + selected_group.name());
        selected_group_id_.set(selected_group.id().toString());
        app_repository_.getGroupMemberList(selected_group.id()).thenAccept(membersResponse ->
        {
            if (membersResponse != null && membersResponse.getPayload().group_members() != null)
            {
                Platform.runLater(() -> selected_group_members_.setAll(membersResponse.getPayload().group_members()));
            }
        });
    }

    public void leaveGroup(GroupResponse groupToLeave)
    {
        if (groupToLeave == null)
            return;
        app_repository_.leaveGroup(groupToLeave.id()).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().status())
                {
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "您已离开群组: " + groupToLeave.name());
                    refreshMyGroups();
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "离开群组失败: " + response.getPayload().message());
                }
            });
        });
    }

    public void joinGroup()
    {
        try
        {
            UUID groupId = UUID.fromString(join_group_id_.get().trim());
            String message = join_message_.get().trim();
            app_repository_.joinGroup(groupId, message).thenAccept(response ->
            {
                Platform.runLater(() ->
                {
                    if (response.getPayload().status())
                    {
                        ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "入群申请已发送。");
                        join_group_id_.set("");
                        join_message_.set("");
                    } else
                    {
                        ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "申请失败: " + response.getPayload().message());
                    }
                });
            });
        } catch (IllegalArgumentException e)
        {
            ViewTool.showAlert(Alert.AlertType.ERROR, "输入无效", "您输入的群组ID格式不正确。");
        }
    }

    public void removeMember(UUID groupId, UUID targetUserId)
    {
        app_repository_.removeMember(groupId, targetUserId).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().status())
                {
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "成员已被移除。");
                    selectGroup(my_groups_.stream().filter(g -> g.id().equals(groupId)).findFirst().orElse(null));
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "失败", "移除成员失败: " + response.getPayload().message());
                }
            });
        });
    }

    public void setMemberRole(UUID groupId, UUID targetUserId, GroupMemberRole newRole)
    {
        app_repository_.setGroupMemberRole(groupId, targetUserId, newRole).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().status())
                {
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "角色已更新。");
                    selectGroup(my_groups_.stream().filter(g -> g.id().equals(groupId)).findFirst().orElse(null));
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "失败", "更新角色失败: " + response.getPayload().message());
                }
            });
        });
    }

    public void changeGroupJoinMode(UUID groupId, GroupJoinMode newMode)
    {
        if (groupId == null || newMode == null) return;
        app_repository_.changeGroupJoinMode(groupId, newMode).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response.getPayload().status())
                {
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "群组加入模式已更新。");
                } else
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "失败", "更新失败: " + response.getPayload().message());
                }
            });
        });
    }

    public void handleJoinRequest(String request, boolean approved)
    {
        System.out.println("处理请求: " + request + ", 结果: " + approved);
    }

    public ObservableList<GroupResponse> getMyGroups()
    {
        return my_groups_;
    }

    public ObservableList<GroupMember> getSelectedGroupMembers()
    {
        return selected_group_members_;
    }

    public ObservableList<String> getJoinRequests()
    {
        return join_requests_;
    }

    public StringProperty selectedGroupNameProperty()
    {
        return selected_group_name_;
    }

    public StringProperty joinGroupIdProperty()
    {
        return join_group_id_;
    }

    public StringProperty joinMessageProperty()
    {
        return join_message_;
    }

    public ObjectProperty<GroupJoinMode> selectedGroupJoinModeProperty()
    {
        return selected_group_join_mode_;
    }

    public StringProperty selectedUUIDProperty() {
        return selected_group_id_;
    }
}