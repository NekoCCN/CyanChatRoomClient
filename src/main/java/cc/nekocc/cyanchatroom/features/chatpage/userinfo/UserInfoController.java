package cc.nekocc.cyanchatroom.features.chatpage.userinfo;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

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
    
    private final StringProperty nickname_property_ = new SimpleStringProperty("未加载");
    private final StringProperty username_property_ = new SimpleStringProperty("未加载");
    private final StringProperty userwords_property_ = new SimpleStringProperty("未加载");
    

    
    public void initialize(URL url, ResourceBundle resource_bundle) {
        setupStyle();
        setupBinding();
    }

    public void loadUserInfo(String username ) {
           Thread.startVirtualThread(() -> {
            var uuid = AppRepository.getInstance().getUuidByUsername(username).join();
            var details = AppRepository.getInstance().getUserDetails(uuid.getPayload().user_id()).join();
            Platform.runLater(() -> {
                username_property_.set(username);
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
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {

                }
            });

        });
    }


    public AnchorPane getRootPane() {
        return root_pane_;
    }
}
