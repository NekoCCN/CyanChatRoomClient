package cc.nekocc.cyanchatroom.features.chatpage.group;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.dto.response.GroupResponse;
import cc.nekocc.cyanchatroom.model.entity.GroupJoinMode;
import cc.nekocc.cyanchatroom.model.entity.GroupMember;
import cc.nekocc.cyanchatroom.model.entity.GroupMemberRole;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class GroupManagementController
{

    public Label Group_uuid_label_;
    @FXML
    private ListView<GroupResponse> my_groups_list_;
    @FXML
    private Button refresh_my_groups_button_;
    @FXML
    private Label group_name_label_;
    @FXML
    private ListView<GroupMember> group_members_list_;
    @FXML
    private Button leave_group_button_;
    @FXML
    private TextField join_group_id_field_;
    @FXML
    private TextArea join_message_area_;
    @FXML
    private Button join_group_button_;
    @FXML
    private ListView<String> join_requests_list_;
    @FXML
    private ChoiceBox<GroupJoinMode> join_mode_choice_box_;
    @FXML
    private Button save_settings_button_;
    @FXML
    private Button approve_button_;
    @FXML
    private Button reject_button_;

    private GroupManagementViewModel view_model_;
    private final AppRepository app_repository_ = AppRepository.getInstance();

    @FXML
    public void initialize()
    {
        this.view_model_ = new GroupManagementViewModel();
        bindViewModel();
        setupEventListeners();
        setupCellFactories();

        join_mode_choice_box_.getItems().setAll(GroupJoinMode.values());
    }

    private void bindViewModel()
    {
        my_groups_list_.setItems(view_model_.getMyGroups());
        group_members_list_.setItems(view_model_.getSelectedGroupMembers());
        group_name_label_.textProperty().bind(view_model_.selectedGroupNameProperty());
        Group_uuid_label_.textProperty().bind(view_model_.selectedUUIDProperty());
        Group_uuid_label_.setOnMouseClicked(event ->{
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(Group_uuid_label_.getText());
            clipboard.setContent(content);
            ViewTool.showAlert(Alert.AlertType.INFORMATION, "复制成功", "群组ID已复制到剪贴板\n"+Group_uuid_label_.getText());
        });
        join_group_id_field_.textProperty().bindBidirectional(view_model_.joinGroupIdProperty());
        join_message_area_.textProperty().bindBidirectional(view_model_.joinMessageProperty());
        join_requests_list_.setItems(view_model_.getJoinRequests());
        join_mode_choice_box_.valueProperty().bindBidirectional(view_model_.selectedGroupJoinModeProperty());
    }

    private void setupEventListeners()
    {
        refresh_my_groups_button_.setOnAction(e -> view_model_.refreshMyGroups());
        join_group_button_.setOnAction(e -> view_model_.joinGroup());

        my_groups_list_.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
        {
            view_model_.selectGroup(newSelection);
        });

        leave_group_button_.setOnAction(e ->
        {
            GroupResponse selectedGroup = my_groups_list_.getSelectionModel().getSelectedItem();
            if (selectedGroup != null)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "您确定要离开这个群组吗？", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response ->
                {
                    if (response == ButtonType.YES)
                    {
                        view_model_.leaveGroup(selectedGroup);
                    }
                });
            }
        });

        save_settings_button_.setOnAction(e ->
        {
            GroupResponse selectedGroup = my_groups_list_.getSelectionModel().getSelectedItem();
            GroupJoinMode selectedMode = join_mode_choice_box_.getValue();
            if (selectedGroup != null && selectedMode != null)
            {
                view_model_.changeGroupJoinMode(selectedGroup.id(), selectedMode);
            }
        });

        approve_button_.setOnAction(e ->
        {
            String selectedRequest = join_requests_list_.getSelectionModel().getSelectedItem();
            if (selectedRequest != null)
            {
                view_model_.handleJoinRequest(selectedRequest, true);
            }
        });

        reject_button_.setOnAction(e ->
        {
            String selectedRequest = join_requests_list_.getSelectionModel().getSelectedItem();
            if (selectedRequest != null)
            {
                view_model_.handleJoinRequest(selectedRequest, false);
            }
        });
    }

    private void setupCellFactories()
    {
        my_groups_list_.setCellFactory(param -> new ListCell<>()
        {
            @Override
            protected void updateItem(GroupResponse group, boolean empty)
            {
                super.updateItem(group, empty);
                setText(empty || group == null ? null : group.name());
            }
        });

        group_members_list_.setCellFactory(param ->
        {
            ListCell<GroupMember> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("移除成员");
            MenuItem setAdminItem = new MenuItem("设为管理员");
            MenuItem setMemberItem = new MenuItem("设为普通成员");

            removeItem.setOnAction(event ->
            {
                GroupMember member = cell.getItem();
                GroupResponse group = my_groups_list_.getSelectionModel().getSelectedItem();
                view_model_.removeMember(group.id(), member.getUserId());
            });
            setAdminItem.setOnAction(event ->
            {
                GroupMember member = cell.getItem();
                GroupResponse group = my_groups_list_.getSelectionModel().getSelectedItem();
                view_model_.setMemberRole(group.id(), member.getUserId(), GroupMemberRole.ADMIN);
            });
            setMemberItem.setOnAction(event ->
            {
                GroupMember member = cell.getItem();
                GroupResponse group = my_groups_list_.getSelectionModel().getSelectedItem();
                view_model_.setMemberRole(group.id(), member.getUserId(), GroupMemberRole.MEMBER);
            });

            contextMenu.getItems().addAll(removeItem, new SeparatorMenuItem(), setAdminItem, setMemberItem);

            cell.itemProperty().addListener((obs, oldMember, newMember) ->
            {
                if (newMember != null)
                {
                    app_repository_.getNicknameForUser(newMember.getUserId()).thenAccept(nickname ->
                    {
                        Platform.runLater(() -> cell.setText(nickname + " (" + newMember.getRole() + ")"));
                    });
                } else
                {
                    Platform.runLater(() -> cell.setText(null));
                }
            });

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) ->
            {
                if (isNowEmpty)
                {
                    cell.setContextMenu(null);
                } else
                {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
    }
}