package cc.nekocc.cyanchatroom.features.chatpage.contactagree;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.usertablite.UserTabLite;
import cc.nekocc.cyanchatroom.model.dto.response.GetUserDetailsResponse;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContactAgreeController
{
    @FXML
    private VBox user_list_vbox_;
    @FXML
    private Button create_button_;
    @FXML
    private Button refresh_friendship_button_;
    @FXML
    private TextField userid_input_;
    @FXML
    private AnchorPane user_info_send_requset_;
    @FXML
    private Label user_nickname_;
    @FXML
    private StackPane user_avator_pane_;
    @FXML
    private Button search_button_;
    @FXML
    private VBox user_info_vbox_;
    @FXML
    private Label username_label_;
    @FXML
    private Label user_words_label;
    @FXML
    private Button send_request_button_;
    @FXML
    private Button refresh_button_;
    @FXML
    private VBox scroll_contain_pane;

    private ContactAgreeViewModel view_model_;
    private UUID found_user_id_;
    private final Map<FriendRequestViewModel, Node> request_node_map_ = new HashMap<>();

    public void setViewModel(ContactAgreeViewModel viewModel)
    {
        this.view_model_ = viewModel;
        userid_input_.textProperty().bindBidirectional(view_model_.searchUsernameProperty());
        setupRequestListListener();
        setupFriendShipListener();
        view_model_.refreshRequests();
        view_model_.refreshUserList();
    }

    @FXML
    public void initialize()
    {
        user_info_send_requset_.setVisible(false);
        search_button_.getStyleClass().addAll(Styles.BOTTOM);

        search_button_.setOnAction(event ->
        {
            handleSearch();
        });

        refresh_button_.setOnAction(event ->
        {
            refreshUserRequest();
        });

        send_request_button_.setOnAction(event ->
        {
            handleSendRequest();
        });

        refresh_friendship_button_.setOnAction(event ->
        {
            refreshUserList();
        });

        create_button_.setOnAction(event ->
        {
            handleCreateButton();
        });
    }
    private void setupFriendShipListener(){
        view_model_.getUserTabLites().addListener((ListChangeListener<UserTabLite>) c ->{
            while(c.next()){
                if(c.wasAdded()){
                    c.getAddedSubList().forEach(vm->{
                       user_list_vbox_.getChildren().add(vm.getRootPane());
                    });
                }
                if(c.wasRemoved()){
                    c.getRemoved().forEach(vm->{
                        user_list_vbox_.getChildren().remove(vm.getRootPane());
                    });
                }
            }
        });
    }

    private void setupRequestListListener()
    {
        view_model_.getFriendRequests().addListener((ListChangeListener<FriendRequestViewModel>) c ->
        {
            while (c.next())
            {
                if (c.wasRemoved())
                {
                    c.getRemoved().forEach(vm ->
                    {
                        Node nodeToRemove = request_node_map_.remove(vm);
                        if (nodeToRemove != null)
                        {
                            Platform.runLater(() -> scroll_contain_pane.getChildren().remove(nodeToRemove));
                        }
                    });
                }
                if (c.wasAdded())
                {
                    for (FriendRequestViewModel vm : c.getAddedSubList())
                    {
                        if (!request_node_map_.containsKey(vm))
                        {
                            createRequestCardNode(vm);
                        }
                    }
                }
            }
            renderRequestsInOrder();
        });
    }

    private void createRequestCardNode(FriendRequestViewModel vm)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cc/nekocc/cyanchatroom/fxml/FriendRequestCard.fxml"));
            Node cardNode = loader.load();
            FriendRequestCardController cardController = loader.getController();
            cardController.setViewModel(vm);
            request_node_map_.put(vm, cardNode);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void renderRequestsInOrder()
    {
        Platform.runLater(() -> scroll_contain_pane.getChildren().setAll(
                view_model_.getFriendRequests().stream()
                        .map(request_node_map_::get)
                        .collect(Collectors.toList())
        ));
    }

    @FXML
    private void handleSearch()
    {
        view_model_.searchUser().thenAccept(details ->
        {
            Platform.runLater(() -> displaySearchResult(details));
        }).exceptionally(ex ->
        {
            Platform.runLater(() ->
            {
                ViewTool.showAlert(Alert.AlertType.ERROR, "搜索失败", ex.getMessage());
                user_info_send_requset_.setVisible(false);
            });
            return null;
        });
    }

    private void displaySearchResult(GetUserDetailsResponse details)
    {
        found_user_id_ = details.user_id();
        username_label_.setText("用户名：" + details.username());
        user_nickname_.setText("昵称：" + details.nick_name());
        user_words_label.setText("个性签名：" + details.signature());

        user_avator_pane_.getChildren().setAll(ViewTool.getDefaultAvatar(details.nick_name()));
        user_info_send_requset_.setVisible(true);
    }

    @FXML
    private void handleSendRequest()
    {
        if (view_model_ != null && found_user_id_ != null)
        {
            view_model_.sendFriendRequest(found_user_id_);
            user_info_send_requset_.setVisible(false);
            userid_input_.clear();
        }
    }

    public void refreshUserRequest()
    {
        if (view_model_ != null) view_model_.refreshRequests();
    }
    public void refreshUserList(){if(view_model_ != null) view_model_.refreshUserList();}
    public void handleCreateButton (){

    }



}