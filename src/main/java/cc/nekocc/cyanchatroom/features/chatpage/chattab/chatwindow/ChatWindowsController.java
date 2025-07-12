package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;

import cc.nekocc.cyanchatroom.domain.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatWindowsController implements Initializable,Cloneable{

    @FXML
    private AnchorPane root_pane_;
    @FXML
    private ScrollPane scroll_pane_;
    @FXML
    private VBox container_pane_;
    @FXML
    private AnchorPane user_label_pane_;
    @FXML
    private ImageView personal_icon_;
    @FXML
    private Label username_label_;

    private final ChatWindowsViewModel view_model_ = new ChatWindowsViewModel();
    private final PauseTransition delay = new PauseTransition(Duration.millis(20));

    public ChatWindowsController() {
        setupAnimation();
    }
    public void setPersonalIcon(ImageView personal_icon){
        this.personal_icon_ = personal_icon;
    }


    // 注入FXML初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupUI();
    }
    private void setupAnimation() {
        delay.setOnFinished(event ->
                scroll_pane_.setVvalue(1.0));

    }



    // 设置UI
    private void setupUI() {
        personal_icon_.setFitHeight(45);
        personal_icon_.setFitWidth(45);
        personal_icon_.setPreserveRatio(true);
    }

    public void sendMessageFromMe(String text) {
        VBox message_container = new VBox();
        message_container.setAlignment(Pos.CENTER_RIGHT);
        message_container.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_container.setPrefWidth(Control.USE_COMPUTED_SIZE);
        HBox message_box = new HBox();
        message_box.setAlignment(Pos.CENTER_RIGHT);
        message_box.setPrefWidth(400);
        Label message_label = new Label(text);
        message_label.setMaxWidth(300);
        message_label.setStyle("-fx-padding : 5;-fx-background-color: green ;" +
                "-fx-text-fill:white;"+
                "-fx-wrap-text: true");
        message_label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        Label white = new Label();
        white.setPrefHeight(15);
        white.setVisible(false);
        message_box.getChildren().addAll(message_label);
        message_container.getChildren().addAll(message_box,white);
        message_box.setPrefHeight(Control.USE_COMPUTED_SIZE);
        container_pane_.getChildren().add(message_container);
        delay.play();



    }



    // 同步User数据
    public void syncUserData(User user) {
        username_label_.setText(user.getUsername());
    }



    public AnchorPane getRoot_pane_() {
        return root_pane_;
    }

    // 深拷贝
    @Override
    public ChatWindowsController clone() throws CloneNotSupportedException {
        ChatWindowsController clone = new ChatWindowsController();

        // 深拷贝ImageView (personal_icon_)
        clone.personal_icon_ = new ImageView();
        if (this.personal_icon_ != null) {
            clone.personal_icon_.setFitHeight(this.personal_icon_.getFitHeight()); // 45.0
            clone.personal_icon_.setFitWidth(this.personal_icon_.getFitWidth());   // 45.0
            clone.personal_icon_.setLayoutX(this.personal_icon_.getLayoutX());   // 520.0
            clone.personal_icon_.setPickOnBounds(this.personal_icon_.isPickOnBounds()); // true
            clone.personal_icon_.setPreserveRatio(this.personal_icon_.isPreserveRatio()); // true

            if (this.personal_icon_.getImage() != null) {
                // 从类路径加载图片资源
                clone.personal_icon_.setImage(this.personal_icon_.getImage());
            }
        }

        // 深拷贝Label (username_label_)
        clone.username_label_ = new Label();
        if (this.username_label_ != null) {
            clone.username_label_.setText(this.username_label_.getText()); // "Username"
            clone.username_label_.setLayoutX(this.username_label_.getLayoutX()); // 15.333
            clone.username_label_.setMinHeight(this.username_label_.getMinHeight()); // 46.0
            clone.username_label_.setTextFill(this.username_label_.getTextFill()); // #1e1f22

            if (this.username_label_.getFont() != null) {
                Font originalFont = this.username_label_.getFont();
                clone.username_label_.setFont(new Font(
                        "Microsoft YaHei UI", // 字体名称
                        20.0 // 字体大小
                ));
            }
        }

        // 深拷贝user_label_pane_ (AnchorPane)
        clone.user_label_pane_ = new AnchorPane();
        if (this.user_label_pane_ != null) {
            clone.user_label_pane_.setPrefHeight(this.user_label_pane_.getPrefHeight()); // 46.0
            clone.user_label_pane_.setPrefWidth(this.user_label_pane_.getPrefWidth()); // 600.0
            clone.user_label_pane_.setStyle(this.user_label_pane_.getStyle()); // -fx-background-color: #E0E0E0;

            // 添加子节点
            if (clone.personal_icon_ != null) {
                AnchorPane.setBottomAnchor(clone.personal_icon_, 1.0);
                AnchorPane.setRightAnchor(clone.personal_icon_, 5.0);
                AnchorPane.setTopAnchor(clone.personal_icon_, 0.0);
                clone.user_label_pane_.getChildren().add(clone.personal_icon_);
            }

            if (clone.username_label_ != null) {
                AnchorPane.setBottomAnchor(clone.username_label_, 0.0);
                AnchorPane.setLeftAnchor(clone.username_label_, 15.0);
                AnchorPane.setTopAnchor(clone.username_label_, 0.0);
                clone.user_label_pane_.getChildren().add(clone.username_label_);
            }
        }

        // 深拷贝container_pane_ (VBox)
        clone.container_pane_ = new VBox();
        if (this.container_pane_ != null) {
            clone.container_pane_.setStyle(this.container_pane_.getStyle()); // -fx-background-color: red;
        }

        // 深拷贝scroll_pane_ (ScrollPane)
        clone.scroll_pane_ = new ScrollPane();
        if (this.scroll_pane_ != null) {
            clone.scroll_pane_.setFitToHeight(this.scroll_pane_.isFitToHeight()); // true
            clone.scroll_pane_.setFitToWidth(this.scroll_pane_.isFitToWidth()); // true
            clone.scroll_pane_.setHbarPolicy(this.scroll_pane_.getHbarPolicy()); // NEVER
            clone.scroll_pane_.setMaxHeight(Double.MAX_VALUE); // 1.7976931348623157E308
            clone.scroll_pane_.setMaxWidth(Double.MAX_VALUE); // 1.7976931348623157E308
            clone.scroll_pane_.setPrefHeight(354.0);

            if (clone.container_pane_ != null) {
                clone.scroll_pane_.setContent(clone.container_pane_);
            }
        }

        // 重建根布局
        clone.root_pane_ = new AnchorPane();
        clone.root_pane_.setMaxHeight(Double.MAX_VALUE);
        clone.root_pane_.setMaxWidth(Double.MAX_VALUE);

        // 添加子节点并设置约束
        if (clone.user_label_pane_ != null) {
            AnchorPane.setLeftAnchor(clone.user_label_pane_, 0.0);
            AnchorPane.setRightAnchor(clone.user_label_pane_, 0.0);
            AnchorPane.setTopAnchor(clone.user_label_pane_, 0.0);
            clone.root_pane_.getChildren().add(clone.user_label_pane_);
        }

        if (clone.scroll_pane_ != null) {
            AnchorPane.setBottomAnchor(clone.scroll_pane_, 0.0);
            AnchorPane.setLeftAnchor(clone.scroll_pane_, 0.0);
            AnchorPane.setRightAnchor(clone.scroll_pane_, 0.0);
            AnchorPane.setTopAnchor(clone.scroll_pane_, 46.0);
            clone.root_pane_.getChildren().add(clone.scroll_pane_);
        }




        return clone;
    }






}
