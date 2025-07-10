package cc.nekocc.cyanchatroom.features.chatpage.chatwindow;

import cc.nekocc.cyanchatroom.domain.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatWindowsController implements Initializable,Cloneable{


    @FXML
    public ScrollPane scroll_pane_;
    @FXML
    public AnchorPane container_pane__;
    @FXML
    public AnchorPane user_label_pane_;
    @FXML
    private ImageView personal_icon_;
    @FXML
    private  AnchorPane root_pane_;
    @FXML
    private Label username_label_;

    private final ChatWindowsViewModel view_model_ = new ChatWindowsViewModel();
    public ChatWindowsController() {}


    public void setPersonalIcon(ImageView personal_icon){
        this.personal_icon_ = personal_icon;
    }

    public void setRootPane(AnchorPane root_pane){
        this.root_pane_ = root_pane;
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

        // 深拷贝container_pane__ (AnchorPane)
        if (this.container_pane__ != null) {
            clone.container_pane__ = new AnchorPane();
            clone.container_pane__.setPrefHeight(this.container_pane__.getPrefHeight());

            if (clone.user_label_pane_ != null) {
                AnchorPane.setLeftAnchor(clone.user_label_pane_, AnchorPane.getLeftAnchor(this.user_label_pane_));
                AnchorPane.setRightAnchor(clone.user_label_pane_, AnchorPane.getRightAnchor(this.user_label_pane_));
                AnchorPane.setTopAnchor(clone.user_label_pane_, AnchorPane.getTopAnchor(this.user_label_pane_));
                clone.container_pane__.getChildren().add(clone.user_label_pane_);
            }
        }

        // 深拷贝scroll_pane_ (ScrollPane)
        if (this.scroll_pane_ != null) {
            clone.scroll_pane_ = new ScrollPane();
            clone.scroll_pane_.setFitToHeight(this.scroll_pane_.isFitToHeight());
            clone.scroll_pane_.setFitToWidth(this.scroll_pane_.isFitToWidth());
            clone.scroll_pane_.setHbarPolicy(this.scroll_pane_.getHbarPolicy());
            clone.scroll_pane_.setMaxHeight(this.scroll_pane_.getMaxHeight());
            clone.scroll_pane_.setMaxWidth(this.scroll_pane_.getMaxWidth());

            if (clone.container_pane__ != null) {
                clone.scroll_pane_.setContent(clone.container_pane__);
            }
        }

        // 深拷贝root_pane_ (AnchorPane)
        if (this.root_pane_ != null) {
            clone.root_pane_ = new AnchorPane();
            clone.root_pane_.setMaxHeight(this.root_pane_.getMaxHeight());
            clone.root_pane_.setMaxWidth(this.root_pane_.getMaxWidth());

            if (clone.scroll_pane_ != null) {
                AnchorPane.setBottomAnchor(clone.scroll_pane_, AnchorPane.getBottomAnchor(this.scroll_pane_));
                AnchorPane.setLeftAnchor(clone.scroll_pane_, AnchorPane.getLeftAnchor(this.scroll_pane_));
                AnchorPane.setRightAnchor(clone.scroll_pane_, AnchorPane.getRightAnchor(this.scroll_pane_));
                AnchorPane.setTopAnchor(clone.scroll_pane_, AnchorPane.getTopAnchor(this.scroll_pane_));
                clone.root_pane_.getChildren().add(clone.scroll_pane_);
            }
        }

        return clone;
    }
}
