package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;

import cc.nekocc.cyanchatroom.Navigator;
import cc.nekocc.cyanchatroom.domain.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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
    public ChatWindowsController() {}
    private Double last_height_ ;


    public void setPersonalIcon(ImageView personal_icon){
        this.personal_icon_ = personal_icon;
    }


    // 注入FXML初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        last_height_ = 30.0;
        setupUI();
    }

    // 设置UI
    public void setupUI() {
        personal_icon_.setFitHeight(45);
        personal_icon_.setFitWidth(45);
        personal_icon_.setPreserveRatio(true);
    }

    public void sendMessageFromMe(String text) {
        HBox message_box = new HBox();
        message_box.setAlignment(Pos.CENTER_RIGHT);
        message_box.setPrefWidth(400);
        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(300);
        messageLabel.setStyle("-fx-background-color: #4CAF50; -fx-padding: 8px;");
        message_box.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_box.getChildren().add(messageLabel);
        container_pane_.getChildren().add(message_box);
        System.out.println("Width:"+container_pane_.widthProperty().get());

        // 自动滚动
        Platform.runLater(() -> scroll_pane_.setVvalue(1.0));
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
        // 首先调用super.clone()获取浅拷贝对象
        ChatWindowsController clone = (ChatWindowsController) super.clone();

        // 深拷贝ImageView (personal_icon_)
        if (this.personal_icon_ != null) {
            Image originalImage = this.personal_icon_.getImage();
            clone.personal_icon_ = new ImageView();
            if (originalImage != null) {
                clone.personal_icon_.setImage(new Image(originalImage.getUrl()));
            }
            clone.personal_icon_.setFitHeight(this.personal_icon_.getFitHeight());
            clone.personal_icon_.setFitWidth(this.personal_icon_.getFitWidth());
            clone.personal_icon_.setLayoutX(this.personal_icon_.getLayoutX());
            clone.personal_icon_.setPickOnBounds(this.personal_icon_.isPickOnBounds());
            clone.personal_icon_.setPreserveRatio(this.personal_icon_.isPreserveRatio());
        }

        // 深拷贝Label (username_label_)
        if (this.username_label_ != null) {
            clone.username_label_ = new Label(this.username_label_.getText());
            clone.username_label_.setLayoutX(this.username_label_.getLayoutX());
            clone.username_label_.setMinHeight(this.username_label_.getMinHeight());
            clone.username_label_.setTextFill(this.username_label_.getTextFill());
            if (this.username_label_.getFont() != null) {
                Font originalFont = this.username_label_.getFont();
                clone.username_label_.setFont(Font.font(
                        originalFont.getFamily(),
                        originalFont.getSize()
                ));
            }
        }

        // 深拷贝user_label_pane_ (AnchorPane)
        if (this.user_label_pane_ != null) {
            clone.user_label_pane_ = new AnchorPane();
            clone.user_label_pane_.setPrefWidth(this.user_label_pane_.getPrefWidth());
            clone.user_label_pane_.setStyle(this.user_label_pane_.getStyle());

            // 添加拷贝的子节点
            if (clone.personal_icon_ != null) {
                AnchorPane.setBottomAnchor(clone.personal_icon_, AnchorPane.getBottomAnchor(this.personal_icon_));
                AnchorPane.setRightAnchor(clone.personal_icon_, AnchorPane.getRightAnchor(this.personal_icon_));
                AnchorPane.setTopAnchor(clone.personal_icon_, AnchorPane.getTopAnchor(this.personal_icon_));
                clone.user_label_pane_.getChildren().add(clone.personal_icon_);
            }

            if (clone.username_label_ != null) {
                AnchorPane.setBottomAnchor(clone.username_label_, AnchorPane.getBottomAnchor(this.username_label_));
                AnchorPane.setLeftAnchor(clone.username_label_, AnchorPane.getLeftAnchor(this.username_label_));
                AnchorPane.setTopAnchor(clone.username_label_, AnchorPane.getTopAnchor(this.username_label_));
                clone.user_label_pane_.getChildren().add(clone.username_label_);
            }
        }

        // 深拷贝container_pane__ (VBox)
        if (this.container_pane_ != null) {
            clone.container_pane_ = new VBox();
            clone.container_pane_.setPrefHeight(this.container_pane_.getPrefHeight());
        }

        // 深拷贝scroll_pane_ (ScrollPane)
        if (this.scroll_pane_ != null) {
            clone.scroll_pane_ = new ScrollPane();
            clone.scroll_pane_.setFitToHeight(this.scroll_pane_.isFitToHeight());
            clone.scroll_pane_.setFitToWidth(this.scroll_pane_.isFitToWidth());
            clone.scroll_pane_.setHbarPolicy(this.scroll_pane_.getHbarPolicy());
            clone.scroll_pane_.setMaxHeight(this.scroll_pane_.getMaxHeight());
            clone.scroll_pane_.setMaxWidth(this.scroll_pane_.getMaxWidth());

            if (clone.container_pane_ != null) {
                clone.scroll_pane_.setContent(clone.container_pane_);
            }
        }


        AnchorPane.setLeftAnchor(clone.user_label_pane_, AnchorPane.getLeftAnchor(this.user_label_pane_));
        AnchorPane.setRightAnchor(clone.user_label_pane_, AnchorPane.getRightAnchor(this.user_label_pane_));
        AnchorPane.setTopAnchor(clone.user_label_pane_, AnchorPane.getTopAnchor(this.user_label_pane_));
        AnchorPane.setTopAnchor(clone.scroll_pane_, AnchorPane.getTopAnchor(this.scroll_pane_));
        AnchorPane.setBottomAnchor(clone.scroll_pane_, AnchorPane.getBottomAnchor(this.scroll_pane_));
        AnchorPane.setRightAnchor(clone.scroll_pane_, AnchorPane.getRightAnchor(this.scroll_pane_));
        AnchorPane.setLeftAnchor(clone.scroll_pane_, AnchorPane.getLeftAnchor(this.scroll_pane_));
        AnchorPane root_pane = new AnchorPane();
        root_pane.getChildren().addAll(clone.user_label_pane_, clone.scroll_pane_);

        return clone;
    }
}
