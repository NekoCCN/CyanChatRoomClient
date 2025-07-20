package cc.nekocc.cyanchatroom.features.chatpage;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.contact.ContactListViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeController;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeViewModel;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import cc.nekocc.cyanchatroom.model.entity.Message;
import cc.nekocc.cyanchatroom.model.factories.StatusFactory;
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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
                    currentUserStatusProperty().set(StatusFactory.fromUser(response.getPayload()));
                });
                loadActiveFriendships();
            } else
            {
                chat_tabs_.clear();
                chat_tab_map_.clear();
                current_username_.set("");
            }
        }));
        app_repository_.addMessageListener(this::handleIncomingMessage);
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
            loadActiveFriendships();
        }
    }

    public void loadActiveFriendships()
    {
        UUID currentUserId = app_repository_.currentUserProperty().get().getId();
        app_repository_.getActiveFriendshipList(currentUserId).thenAccept(response ->
        {
            if (response != null && response.getPayload().friendships() != null)
            {
                // Java 流比 C++ Ranges 舒服不少看起来
                var newTabs = response.getPayload().friendships().stream()
                        .map(friendship ->
                        {
                            UUID oppositeId = friendship.getUserOneId().equals(currentUserId) ? friendship.getUserTwoId() : friendship.getUserOneId();
                            return chat_tab_map_.compute(oppositeId,(id, _)->
                                    new ChatTabViewModel(oppositeId, ConversationType.USER, contact_list_view_model_::addOrUpdateContact));
                        })
                        .collect(Collectors.toList());
                Platform.runLater(() -> chat_tabs_.setAll(newTabs));
            }
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
        ChatTabViewModel targetTab = chat_tab_map_.get(message.getConversationId());
        if (targetTab != null)
        {
            targetTab.getChatWindowViewModel().receiveMessage(message);
        } else
        {
            loadActiveFriendships();
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
                controller.setViewModel(new ContactAgreeViewModel(this::loadActiveFriendships));

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

    public ContactListViewModel getContactListViewModel()
    {
        return contact_list_view_model_;
    }
}