package cc.nekocc.cyanchatroom.features.chatpage.chatwindow;

import cc.nekocc.cyanchatroom.domain.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatWindowsController implements Initializable,Cloneable{

    @FXML
    private ImageView personal_icon_;
    @FXML
    private  AnchorPane root_pane_;
    @FXML
    private Label username_label_;

    private final ChatWindowsViewModel view_model_ = new ChatWindowsViewModel();
    public ChatWindowsController() {}

    public ChatWindowsController(ImageView personal_icon, AnchorPane root_pane){
        this.personal_icon_ = personal_icon;
        this.root_pane_ = root_pane;
    }

    public void setPersonalIcon(ImageView personal_icon){

    }

    public void setRootPane(AnchorPane root_pane){

    }

    // 注入FXML初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupUI();
    }

    // 设置UI
    public void setupUI() {
        personal_icon_.setFitHeight(45);
        personal_icon_.setFitWidth(45);
        personal_icon_.setPreserveRatio(true);

    }

    // 同步User数据
    public void syncUserData(User user) {
        username_label_.setText(user.getUsername());
        System.out.println("username_label_: " + username_label_.getText());

    }



    public AnchorPane getRoot_pane_() {
        return root_pane_;
    }

    // 深拷贝
    @Override
    public ChatWindowsController clone() throws CloneNotSupportedException {
        ChatWindowsController clone  = new ChatWindowsController();


        return  clone;
    }


}
