package cc.nekocc.cyanchatroom.features.chatpage;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.features.chatpage.contact.ContactListController;
import cc.nekocc.cyanchatroom.features.chatpage.userinfo.UserInfoController;
import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChatPageController implements Initializable
{
    @FXML
    public Button refresh_user_list_button_;
    @FXML
    private AnchorPane user_info_window_;
    @FXML
    private TextArea message_input;
    @FXML
    private ImageView talk_icon_;
    @FXML
    private ImageView contact_icon_;
    @FXML
    private ImageView file_enter_button_;
    @FXML
    private ImageView setting_icon_;
    @FXML
    private AnchorPane scroll_root_pane_;
    @FXML
    private ImageView contact_agreement_icon_;
    @FXML
    private Label username_title_label_;
    @FXML
    private Label username_label_;
    @FXML
    private ScrollPane list_scrollPane_;
    @FXML
    private ChoiceBox<Status> user_status_;
    @FXML
    private AnchorPane chat_windows_pane_;
    @FXML
    private VBox user_list_vbox_;
    @FXML
    private Button enter_button_;
    @FXML
    private Button e2ee_enter_button_;

    private ChatPageViewModel view_model_;
    private ChatWindowsController chat_window_controller_;
    private ContactListController contact_list_controller_;
    private SimpleObjectProperty<Node> current_selected_tab_controller_ = new SimpleObjectProperty<>();
    private Node contact_list_node_;
    private UserInfoController user_info_controller_ = new UserInfoController();
    private final Map<ChatTabViewModel, Node> tab_node_map_ = new HashMap<>();
    private final DropShadow glow_effect_ = new DropShadow();
    private Node current_active_icon_;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.view_model_ = new ChatPageViewModel();
        loadSubViews();
        setupStylesAndEffects();
        setupBindings();
        setupEventListeners();
        setupTabListListener();
    }

    private void loadSubViews()
    {
        try
        {
            FXMLLoader chat_loader = new FXMLLoader(getClass().getResource("/cc/nekocc/cyanchatroom/fxml/ChatWindow.fxml"));
            AnchorPane chat_window_node = chat_loader.load();
            chat_window_controller_ = chat_loader.getController();
            AnchorPane.setTopAnchor(chat_window_node, 0.0);
            AnchorPane.setBottomAnchor(chat_window_node, 0.0);
            AnchorPane.setLeftAnchor(chat_window_node, 0.0);
            AnchorPane.setRightAnchor(chat_window_node, 0.0);
            chat_windows_pane_.getChildren().add(chat_window_node);
            chat_window_node.setVisible(false);

            FXMLLoader user_info_loader = ViewTool.loadFXML("fxml/UserInfo.fxml");
            user_info_controller_ = user_info_loader.getController();

            FXMLLoader contact_loader = new FXMLLoader(getClass().getResource("/cc/nekocc/cyanchatroom/fxml/ContactList.fxml"));
            contact_list_node_ = contact_loader.load();
            contact_list_controller_ = contact_loader.getController();
            var vm = view_model_.getContactListViewModel();
            vm.setOnDataReadyCallback(str ->
            {

                Platform.runLater(()->{
                    user_info_controller_.loadUserInfo(str);
                    AnchorPane.setLeftAnchor(user_info_controller_.getRootPane(),0.0);
                    AnchorPane.setRightAnchor(user_info_controller_.getRootPane(),0.0);
                    AnchorPane.setTopAnchor(user_info_controller_.getRootPane(),0.0);
                    AnchorPane.setBottomAnchor(user_info_controller_.getRootPane(),0.0);
                    user_info_window_.getChildren().setAll(user_info_controller_.getRootPane());
                });
            });

            contact_list_controller_.setViewModel(vm);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void setupStylesAndEffects()
    {
        glow_effect_.setColor(Color.rgb(73, 136, 240));
        glow_effect_.setSpread(0.8);
        glow_effect_.setRadius(12);

        current_active_icon_ = talk_icon_;
        talk_icon_.setEffect(glow_effect_);

        user_status_.getStyleClass().addAll(Styles.ROUNDED, Styles.ACCENT);
        enter_button_.getStyleClass().addAll(Styles.ROUNDED, Styles.ACCENT);
        e2ee_enter_button_.getStyleClass().addAll(Styles.ROUNDED, Styles.ACCENT);
    }

    private void setupBindings()
    {
        username_label_.textProperty().bind(view_model_.currentUsernameProperty());
        username_title_label_.textProperty().bind(Bindings.createStringBinding(() ->
                {
                    String name = view_model_.currentUsernameProperty().get();
                    return (name == null || name.isEmpty()) ? "" : name.substring(0, 1).toUpperCase();
                },
                view_model_.currentUsernameProperty()
        ));

        user_status_.setOnAction(_ ->
        {
            Status selectedStatus = user_status_.getValue();
            if (selectedStatus != null)
            {
                view_model_.updateUserStatus(selectedStatus);
            }
        });

        refresh_user_list_button_.setOnAction(_ -> view_model_.loadActiveFriendships());

        user_status_.setItems(FXCollections.observableArrayList(Status.values()));
        user_status_.valueProperty().bindBidirectional(view_model_.currentUserStatusProperty());
        enter_button_.disableProperty().bind(view_model_.selectedChatTabProperty().isNull());

        BooleanBinding isGroupChatBinding = Bindings.createBooleanBinding(() ->
        {
            ChatTabViewModel selected_tab = view_model_.selectedChatTabProperty().get();
            if (selected_tab == null)
            {
                return false;
            }

            return selected_tab.getConversationType() == ConversationType.GROUP;
        }, view_model_.selectedChatTabProperty());

        e2ee_enter_button_.disableProperty().bind(
                view_model_.selectedChatTabProperty().isNull()
                        .or(isGroupChatBinding));
    }

    private void setupEventListeners()
    {

        initIconEffect(talk_icon_);
        talk_icon_.setOnMouseClicked(e -> switchSidePane(ChatPageViewModel.SidePane.TALK, talk_icon_));

        initIconEffect(contact_icon_);
        contact_icon_.setOnMouseClicked(e -> switchSidePane(ChatPageViewModel.SidePane.CONTACTS, contact_icon_));

        initIconEffect(contact_agreement_icon_);
        contact_agreement_icon_.setOnMouseClicked(e -> view_model_.showContactAgree());

        initIconEffect(setting_icon_);
        setting_icon_.setOnMouseClicked(e -> view_model_.showSetting());

        final KeyCodeCombination sendMessageCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
        message_input.setOnKeyPressed(e ->
        {
            if (sendMessageCombination.match(e) && view_model_.selectedChatTabProperty().get() != null)
            {
                view_model_.selectedChatTabProperty().get().getChatWindowViewModel().sendMessage();
            }
        });

        enter_button_.setOnAction(e ->
        {
            if (view_model_.selectedChatTabProperty().get() != null)
            {
                view_model_.selectedChatTabProperty().get().getChatWindowViewModel().sendMessage();
            }
        });

        e2ee_enter_button_.setOnAction(e ->
        {
            if (view_model_.selectedChatTabProperty().get() != null)
            {
                view_model_.selectedChatTabProperty().get().getChatWindowViewModel().sendE2EEMessage();
            }
        });

        initIconEffect(file_enter_button_);

        file_enter_button_.setOnMouseClicked(e ->
        {
            if (view_model_.selectedChatTabProperty().get() != null)
            {
                view_model_.selectedChatTabProperty().get().getChatWindowViewModel().sendFile();
            }
        });

        view_model_.selectedChatTabProperty().addListener((obs, old_tab, new_tab) ->
        {
            updateChatWindow(old_tab, new_tab);
            updateTabSelectionStyles(new_tab);
        });

        view_model_.activeSidePaneProperty().addListener((obs, oldPane, newPane) ->
        {
            if (newPane == ChatPageViewModel.SidePane.TALK)
            {
                scroll_root_pane_.getChildren().setAll(list_scrollPane_);
                chat_windows_pane_.setVisible(true);
                user_info_window_.setVisible(false);
                updateChatWindow(null, view_model_.selectedChatTabProperty().get());
            } else
            {
                scroll_root_pane_.getChildren().setAll(contact_list_node_);
                chat_windows_pane_.setVisible(false);
                user_info_window_.getChildren().clear();
                user_info_window_.setVisible(true);
            }
        });
    }

    private void setupTabListListener()
    {
        view_model_.getChatTabs().addListener((ListChangeListener<ChatTabViewModel>) c ->
        {
            while (c.next())
            {
                if (c.wasRemoved())
                {
                    c.getRemoved().forEach(tab_node_map_::remove);
                }
                if (c.wasAdded())
                {
                    for (ChatTabViewModel vm : c.getAddedSubList())
                    {
                        if (!tab_node_map_.containsKey(vm))
                        {
                            createTabNode(vm);
                        }
                    }
                }
            }
            Platform.runLater(this::renderTabsInOrder);
        });
    }

    private void createTabNode(ChatTabViewModel vm)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cc/nekocc/cyanchatroom/fxml/ChatTab.fxml"));
            Node tab_node = loader.load();
            tab_node.setOnMouseEntered(e ->{
                if(current_selected_tab_controller_.isNotEqualTo(tab_node).get()){
                    tab_node.setStyle("-fx-background-color: #F0F0F0;");
                }
            });
            tab_node.setOnMouseExited(e ->{
                if(current_selected_tab_controller_.isNotEqualTo(tab_node).get()){
                    tab_node.setStyle("-fx-background-color: transparent");
                }
            });
            ChatTabController tabController = loader.getController();
            tabController.setData(vm, () ->
            {
                if (view_model_.activeSidePaneProperty().get() != ChatPageViewModel.SidePane.TALK)
                {
                    switchSidePane(ChatPageViewModel.SidePane.TALK, talk_icon_);
                }
                view_model_.selectedChatTabProperty().set(vm);
            });
            tab_node_map_.put(vm, tab_node);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void renderTabsInOrder()
    {
        user_list_vbox_.getChildren().setAll(
                view_model_.getChatTabs().stream()
                        .map(tab_node_map_::get)
                        .collect(Collectors.toList())
        );
        updateTabSelectionStyles(view_model_.selectedChatTabProperty().get());
    }

    private void updateTabSelectionStyles(ChatTabViewModel selectedViewModel)
    {

        tab_node_map_.forEach((vm, node) ->
        {
            if (node != null)
            {
                if (vm.equals(selectedViewModel))
                {
                    current_selected_tab_controller_.set(node);
                    node.setStyle("-fx-background-color: #3574F0; -fx-background-radius: 8;");
                } else
                {
                    node.setStyle("-fx-background-color: transparent;");
                }
            }
        });
    }

    private void updateChatWindow(ChatTabViewModel oldTab, ChatTabViewModel newTab)
    {
        if (newTab == null)
        {
            chat_window_controller_.getRootPane().setVisible(false);
        } else
        {
            if (oldTab != null)
            {
                message_input.textProperty().unbindBidirectional(oldTab.getChatWindowViewModel().messageInputTextProperty());
            }
            message_input.textProperty().bindBidirectional(newTab.getChatWindowViewModel().messageInputTextProperty());
            message_input.clear();

            chat_window_controller_.setViewModel(newTab.getChatWindowViewModel());
            chat_window_controller_.getRootPane().setVisible(true);
        }
    }

    private void switchSidePane(ChatPageViewModel.SidePane pane, ImageView icon)
    {
        if (current_active_icon_ != icon)
        {
            current_active_icon_.setEffect(null);
            icon.setEffect(glow_effect_);
            current_active_icon_ = icon;
            view_model_.activeSidePaneProperty().set(pane);
        }
    }

    private void initIconEffect(ImageView icon)
    {
        icon.setOnMouseEntered(e ->
        {
            if (current_active_icon_ != icon) icon.setEffect(new Glow(0.6));
        });
        icon.setOnMouseExited(e ->
        {
            if (current_active_icon_ != icon) icon.setEffect(null);
        });
    }
}