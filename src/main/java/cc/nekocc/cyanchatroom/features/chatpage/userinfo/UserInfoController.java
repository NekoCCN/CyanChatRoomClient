package cc.nekocc.cyanchatroom.features.chatpage.userinfo;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.dto.response.FriendshipListResponse;
import cc.nekocc.cyanchatroom.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserInfoController implements Initializable {
    @FXML
    private AnchorPane root_pane_;
    @FXML
    private Label username_label_;
    @FXML 
    private Label nickname_label_;
    @FXML
    private Label userwords_label_;
    @FXML
    private Button dele_friendship_;
    @FXML
    private StackPane avator_stackpane_;

    private TreeItem< String> current_selected_contact_;
    
    private final StringProperty nickname_property_ = new SimpleStringProperty("未加载");
    private final StringProperty username_property_ = new SimpleStringProperty("未加载");
    private final StringProperty userwords_property_ = new SimpleStringProperty("未加载");
    private  UUID opposite_user_id_;
    public void initialize(URL url, ResourceBundle resource_bundle) {
        setupStyle();
        setupBinding();
    }

    public void loadUserInfo(String username,TreeItem<String> callback) {
        current_selected_contact_ = callback;
        Thread.startVirtualThread(() -> {
            var uuid = AppRepository.getInstance().getUuidByUsername(username).join();
            var details = AppRepository.getInstance().getUserDetails(uuid.getPayload().user_id()).join();
            Platform.runLater(() -> {
                username_property_.set(username);
                opposite_user_id_ = uuid.getPayload().user_id();
                nickname_property_.set(details.getPayload().nick_name());
                userwords_property_.set(details.getPayload().signature());
                avator_stackpane_.getChildren().setAll(ViewTool.getDefaultAvatar(nickname_property_.get()));
            });



        });
    }
    
    private void setupBinding() {
        username_label_.textProperty().bind(username_property_);
        nickname_label_.textProperty().bind(nickname_property_);
        userwords_label_.textProperty().bind(userwords_property_);
    }

    private void setupStyle() {
        dele_friendship_.getStyleClass().addAll(Styles.DANGER);
        dele_friendship_.setOnAction(_->{
            Alert alert = ViewTool.showAlert(Alert.AlertType.WARNING, "不是点错了？", "确定要删除好友吗？", false);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(responses -> {
                if (responses == ButtonType.YES) {
                    CompletableFuture<ProtocolMessage<FriendshipListResponse>> friendship_list_ = AppRepository.getInstance().getFriendshipList(AppRepository.getInstance().currentUserProperty().get().getId());
                    friendship_list_.thenAccept(response -> {
                        boolean is_deleted = false;
                       for(var friendship: response.getPayload().friendships()) {
                           if(friendship.getUserOneId().equals(opposite_user_id_) || friendship.getUserTwoId().equals(opposite_user_id_)){
                               AppRepository.getInstance().deleteFriendship(friendship.getId());
                               ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "删除成功");
                               is_deleted = true;
                               current_selected_contact_.getParent().getChildren().remove(current_selected_contact_);
                               break;
                           }
                       }
                       if(!is_deleted){
                           ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "未找到好友关系");
                       }
                    });
                }
            });

        });
    }


    public AnchorPane getRootPane() {
        return root_pane_;
    }
}
