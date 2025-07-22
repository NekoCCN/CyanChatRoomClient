package cc.nekocc.cyanchatroom.features.chatpage;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.contact.ContactListViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeController;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeViewModel;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.dto.response.GetUserDetailsResponse;
import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import cc.nekocc.cyanchatroom.model.entity.Friendship;
import cc.nekocc.cyanchatroom.model.entity.Message;
import cc.nekocc.cyanchatroom.model.factories.StatusFactory;
import cc.nekocc.cyanchatroom.model.dto.response.GroupResponse;
import cc.nekocc.cyanchatroom.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ChatPageViewModel
{
    private final AppRepository app_repository_ = AppRepository.getInstance();

    private final StringProperty current_username_ = new SimpleStringProperty();
    private final ObjectProperty<Status> current_user_status_ = new SimpleObjectProperty<>();

    private final ConcurrentHashMap<UUID, ChatTabViewModel> chat_tab_map_ = new ConcurrentHashMap<>();
    private final ObservableList<ChatTabViewModel> chat_tabs_ = FXCollections.observableArrayList();
    private final ObjectProperty<ChatTabViewModel> selected_chat_tab_ = new SimpleObjectProperty<>();

    public enum SidePane
    {TALK, CONTACTS}

    private final ObjectProperty<SidePane> active_side_pane_ = new SimpleObjectProperty<>(SidePane.TALK);

    private final BooleanProperty setting_shown_ = new SimpleBooleanProperty(false);
    private final BooleanProperty contact_agree_shown_ = new SimpleBooleanProperty(false);
    private Stage setting_stage_;
    private Stage contact_agree_stage_;
    private final ContactListViewModel contact_list_view_model_ = new ContactListViewModel();

    private Stage group_management_stage_;
    private final BooleanProperty group_management_shown_ = new SimpleBooleanProperty(false);
    private Runnable onPersonalIconClick;

    public ChatPageViewModel()
    {
        setupListeners();
        loadInitialData();
    }

    private void setupListeners()
    {
        app_repository_.currentUserProperty().addListener((obs, oldUser, newUser) -> Platform.runLater(() ->
        {
            if (newUser != null)
            {
                current_username_.set(newUser.getNickname());
                AppRepository.getInstance().getUserDetails(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response ->
                {
                    if (response.getPayload().request_status())
                    {
                        currentUserStatusProperty().set(StatusFactory.fromUser(response.getPayload()));
                    }
                });
                loadActiveConversations();
            } else
            {
                chat_tabs_.clear();
                chat_tab_map_.clear();
                current_username_.set("");
                contact_list_view_model_.clearContacts();
            }
        }));
        app_repository_.addMessageListener(this::handleIncomingMessage);

        selected_chat_tab_.addListener((obs, oldTab, newTab) ->
        {
            if (newTab == null)
            {
                onPersonalIconClick = null;
            } else if (newTab.getConversationType() == ConversationType.GROUP)
            {
                onPersonalIconClick = this::showGroupManagement;
            } else
            {
                onPersonalIconClick = null;
            }
        });
    }

    public void showGroupManagement()
    {
        if (group_management_stage_ == null)
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cc/nekocc/cyanchatroom/fxml/GroupManagement.fxml"));
                Scene scene = new Scene(loader.load());

                group_management_stage_ = new Stage();
                group_management_stage_.setTitle("群组管理");
                group_management_stage_.getIcons().add((new Image(Objects.requireNonNull(getClass().getResource("/Image/contack_icon.png")).toExternalForm())));
                group_management_stage_.setScene(scene);
                group_management_shown_.bind(group_management_stage_.showingProperty());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        group_management_stage_.show();
        group_management_stage_.toFront();
    }

    private void loadInitialData()
    {
        if (app_repository_.currentUserProperty().get() != null)
        {
            current_username_.set(app_repository_.currentUserProperty().get().getNickname());
            AppRepository.getInstance().getUserDetails(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response ->
            {
                currentUserStatusProperty().set(StatusFactory.fromUser(response.getPayload()));
            });
            loadActiveConversations();
        }
    }

    public void loadActiveConversations()
    {
        UUID currentUserId = app_repository_.currentUserProperty().get().getId();
        if (currentUserId == null)
            return;

        CompletableFuture<List<GetUserDetailsResponse>> friendsDetailsFuture = app_repository_.getActiveFriendshipList(currentUserId)
                .thenCompose(response ->
                {
                    if (response == null || response.getPayload() == null || response.getPayload().friendships() == null)
                    {
                        return CompletableFuture.completedFuture(new ArrayList<>());
                    }
                    List<CompletableFuture<GetUserDetailsResponse>> detailFutures = response.getPayload().friendships().stream()
                            .map(friendship ->
                            {
                                UUID oppositeId = friendship.getUserOneId().equals(currentUserId) ? friendship.getUserTwoId() : friendship.getUserOneId();
                                return app_repository_.getUserDetails(oppositeId).thenApply(ProtocolMessage::getPayload);
                            })
                            .toList();
                    return CompletableFuture.allOf(detailFutures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> detailFutures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
                });

        CompletableFuture<List<GroupResponse>> groupsDetailsFuture = app_repository_.getGroupIdsByUserId(currentUserId)
                .thenCompose(response ->
                {
                    if (response == null || response.getPayload() == null || response.getPayload().group_ids() == null || response.getPayload().group_ids().length == 0)
                    {
                        return CompletableFuture.completedFuture(new ArrayList<>());
                    }
                    List<CompletableFuture<GroupResponse>> detailFutures = Stream.of(response.getPayload().group_ids())
                            .map(groupId -> app_repository_.getGroupDetails(groupId).thenApply(ProtocolMessage::getPayload))
                            .collect(Collectors.toList());
                    return CompletableFuture.allOf(detailFutures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> detailFutures.stream().map(CompletableFuture::join).filter(GroupResponse::success).collect(Collectors.toList()));
                });

        friendsDetailsFuture.thenCombine(groupsDetailsFuture, (friendDetails, groupDetails) ->
        {
            Platform.runLater(() ->
            {
                contact_list_view_model_.clearContacts();

                List<ChatTabViewModel> allTabs = new ArrayList<>();

                for (GetUserDetailsResponse detail : friendDetails)
                {
                    if (detail.request_status())
                    {
                        contact_list_view_model_.addOrUpdateContact(detail.username(), StatusFactory.fromUser(detail));
                        ChatTabViewModel tab = chat_tab_map_.computeIfAbsent(detail.user_id(), id -> new ChatTabViewModel(id, ConversationType.USER, (name, status) ->
                        {
                        }));
                        tab.updateDetails(detail.nick_name(), StatusFactory.fromUser(detail));
                        allTabs.add(tab);
                    }
                }

                for (GroupResponse group : groupDetails)
                {
                    contact_list_view_model_.addGroupContact(group.name());
                    ChatTabViewModel tab = chat_tab_map_.computeIfAbsent(group.id(), id -> new ChatTabViewModel(id, ConversationType.GROUP, (name, status) ->
                    {
                    }));
                    tab.updateDetails(group.name(), Status.ONLINE);
                    allTabs.add(tab);
                }

                chat_tabs_.setAll(allTabs);
            });
            return null;
        }).exceptionally(ex ->
        {
            Platform.runLater(() -> ViewTool.showAlert(Alert.AlertType.ERROR, "加载失败", "加载联系人列表时出错: " + ex.getMessage()));
            return null;
        });
    }

    public void updateUserStatus(Status new_status)
    {
        if (new_status == null || new_status.equals(current_user_status_.get()))
            return;

        if (new_status == Status.OFFLINE)
        {
            Platform.runLater(() -> AppRepository.getInstance().getUserDetails(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response ->
            {
                currentUserStatusProperty().set(StatusFactory.fromUser(response.getPayload()));
            }));
            ViewTool.showAlert(Alert.AlertType.INFORMATION, "离线切换失败", "你要切换离线状态直接退出程序就行。");
            return;
        }

        UUID currentUserId = app_repository_.currentUserProperty().get().getId();
        app_repository_.getUserDetails(currentUserId)
                .thenCompose(response -> app_repository_.updateProfile(
                        response.getPayload().nick_name(),
                        response.getPayload().signature(),
                        response.getPayload().avatar_url(),
                        StatusFactory.fromStatus(new_status)
                ))
                .thenAccept(updateResponse ->
                {
                    if (updateResponse != null && updateResponse.getPayload().status())
                    {
                        Platform.runLater(() -> current_user_status_.set(new_status));
                    } else
                    {
                        Platform.runLater(() ->
                        {
                            AppRepository.getInstance().getUserDetails(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response ->
                            {
                                currentUserStatusProperty().set(StatusFactory.fromUser(response.getPayload()));
                            });
                            ViewTool.showAlert(Alert.AlertType.ERROR, "更新状态失败", "发生错误:账户状态修改事件无法正常执行，请检查网络是否链接或者服务器是否还在运行");
                        });
                    }
                });
    }

    private void handleIncomingMessage(Message message)
    {
        if (message.isOutgoing())
            return;

        ChatTabViewModel target_tab = chat_tab_map_.get(message.getConversationId());
        if (target_tab != null)
        {
            target_tab.getChatWindowViewModel().receiveMessage(message);
        } else
        {
            loadActiveConversations();
        }
    }

    public void showSetting()
    {
        if (setting_stage_ == null)
        {
            setting_stage_ = new Stage();
            setting_stage_.setTitle("设置");
            setting_stage_.getIcons().add((new Image(Objects.requireNonNull(getClass().getResource("/Image/setting_stage_icon.png")).toExternalForm())));
            setting_stage_.setScene(new Scene(ViewTool.loadFXML("fxml/Setting.fxml").getRoot()));
            setting_shown_.bind(setting_stage_.showingProperty());
        }
        setting_stage_.show();
        setting_stage_.toFront();
    }

    public void showContactAgree()
    {
        if (contact_agree_stage_ == null)
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cc/nekocc/cyanchatroom/fxml/ContactAgreePage.fxml"));
                Scene scene = new Scene(loader.load());
                ContactAgreeController controller = loader.getController();

                controller.setViewModel(new ContactAgreeViewModel(this::loadActiveConversations));

                contact_agree_stage_ = new Stage();
                contact_agree_stage_.setTitle("好友管理");
                contact_agree_stage_.getIcons().add((new Image(Objects.requireNonNull(getClass().getResource("/Image/contact_agree_page_icon.png")).toExternalForm())));
                contact_agree_stage_.setScene(scene);
                contact_agree_stage_.setUserData(controller);
                contact_agree_shown_.bind(contact_agree_stage_.showingProperty());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        contact_agree_stage_.show();
        contact_agree_stage_.toFront();
        ContactAgreeController controller = (ContactAgreeController) contact_agree_stage_.getUserData();
        if (controller != null)
        {
            controller.refreshUserRequest();
            controller.refreshUserList();
        }
    }

    public void uploadAndSendFile()
    {
        ChatTabViewModel selected_tab = selected_chat_tab_.get();
        if (selected_tab != null)
        {
            ChatWindowViewModel chat_window = selected_tab.getChatWindowViewModel();
            chat_window.sendFile(selected_tab.getConversationType());
        } else
        {
            ViewTool.showAlert(Alert.AlertType.WARNING, "未选择聊天", "请先选择一个聊天窗口再发送文件。");
        }
    }

    public ObservableList<ChatTabViewModel> getChatTabs()
    {
        return chat_tabs_;
    }

    public ObjectProperty<ChatTabViewModel> selectedChatTabProperty()
    {
        return selected_chat_tab_;
    }

    public StringProperty currentUsernameProperty()
    {
        return current_username_;
    }

    public ObjectProperty<Status> currentUserStatusProperty()
    {
        return current_user_status_;
    }

    public BooleanProperty settingShownProperty()
    {
        return setting_shown_;
    }

    public BooleanProperty contactAgreeShownProperty()
    {
        return contact_agree_shown_;
    }

    public ObjectProperty<SidePane> activeSidePaneProperty()
    {
        return active_side_pane_;
    }

    public Runnable getOnPersonalIconClick()
    {
        return onPersonalIconClick;
    }

    public ContactListViewModel getContactListViewModel()
    {
        return contact_list_view_model_;
    }
}