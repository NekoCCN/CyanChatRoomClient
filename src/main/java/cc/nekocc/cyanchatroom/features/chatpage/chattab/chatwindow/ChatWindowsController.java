package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

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
    private final ArrayList<Label> message_labels = new ArrayList<>();

    public ChatWindowsController() {
    }
    // 注入FXML初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupUI();
        setupAnimation();
    }
    private void setupAnimation() {
        delay.setOnFinished(_ ->
                scroll_pane_.setVvalue(1.0));

    }
    public UUID getUserID() {
        return view_model_.getOppositeID();
    }
    public void reLoad(){
        setupUI();
        setupAnimation();
    }
    private ContextMenu setupMessageMenu(Label label, VBox  container,Boolean enabledWithDraw) {

        ContextMenu message_menu_  = new ContextMenu();
        MenuItem copyItem = new MenuItem("复制");
        copyItem.setStyle("-fx-text-font: 'Microsoft YaHei';-fx-font-size: 12px");
        MenuItem repostItem = new MenuItem("转发");
        repostItem.setStyle("-fx-text-font: 'Microsoft YaHei';-fx-font-size: 12px");
        MenuItem  withdrawItem= new MenuItem("撤回");
        withdrawItem.setStyle("-fx-text-font: 'Microsoft YaHei';-fx-font-size: 12px");
        MenuItem deleteItem = new MenuItem("删除");
        deleteItem.setStyle("-fx-text-font: 'Microsoft YaHei';-fx-font-size: 12px");
        copyItem.setOnAction(_ -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(label.getText());
            clipboard.setContent(content);
        });
        withdrawItem.setOnAction(_ -> container_pane_.getChildren().remove(container));
        deleteItem.setOnAction(_ -> container_pane_.getChildren().remove(container));
        message_menu_.getItems().addAll(copyItem,repostItem);
        if(enabledWithDraw)
            message_menu_.getItems().add(withdrawItem);
        message_menu_.getItems().add(deleteItem);
        return message_menu_;
    }




    // 设置UI
    private void setupUI() {
        personal_icon_.setFitHeight(45);
        personal_icon_.setFitWidth(45);
        personal_icon_.setPreserveRatio(true);
    }

    public void sendMessageFromMe(String text) {
        Label None = new Label();
        None.setPrefWidth(7);
        None.setVisible(false);
        VBox message_container = new VBox();
        message_container.setAlignment(Pos.CENTER_RIGHT);
        message_container.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_container.setPrefWidth(Control.USE_COMPUTED_SIZE);
        HBox message_box = new HBox();
        message_box.setAlignment(Pos.TOP_RIGHT);
        message_box.setPrefWidth(400);
        message_box.setPrefWidth(400);
        Label message_label = new Label(text);
        message_label.setStyle("-fx-padding : 4;-fx-background-color: green ;" +
                "-fx-text-fill:white;"+
                "-fx-wrap-text: true;"+
                "-fx-background-radius: 8");
        message_label.maxWidthProperty().bind(container_pane_.widthProperty().multiply(0.6));
        message_label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_label.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL,18));
        Label white = new Label();
        white.setPrefHeight(15);
        white.setVisible(false);
        message_box.setSpacing(10);
        Text username = new Text(AppRepository.getInstance().currentUserProperty().get().getNickname());
        username.setStyle("-fx-fill: black;");
        username.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,14));
        Text time = new Text(LocalDate.now()+" "+ LocalTime.now().toString().substring(0, 5));
        time.setStyle("-fx-fill: #B0B0B0");
        time.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL,10));
        VBox message_box_with_name = new VBox(username,message_label,time);
        message_box_with_name.setSpacing(2);
        message_box_with_name.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_box_with_name.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_box_with_name.setAlignment(Pos.TOP_RIGHT);
        message_box.getChildren().addAll(message_box_with_name, ViewTool.getDefaultAvatar(username.getText()),None);
        message_container.getChildren().addAll(message_box,white);
        message_box.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_label.setContextMenu(setupMessageMenu(message_label,message_container, true));
        container_pane_.getChildren().add(message_container);
        message_labels.add(message_label);
        delay.play();

    }


    public void sendMessageFromOther(String text) {
        Label None = new Label();
        None.setPrefWidth(7);
        None.setVisible(false);
        VBox message_container = new VBox();
        message_container.setAlignment(Pos.CENTER_LEFT);
        message_container.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_container.setPrefWidth(Control.USE_COMPUTED_SIZE);
        HBox message_box = new HBox();
        message_box.setAlignment(Pos.TOP_LEFT);
        message_box.setPrefWidth(400);
        message_box.setPrefWidth(400);
        Label message_label = new Label(text);
        message_label.setStyle("-fx-padding : 4;-fx-background-color: #FDF8FF ;" +
                "-fx-text-fill:black;"+
                "-fx-wrap-text: true;"+
                "-fx-background-radius: 8;"+
                "-fx-border-width: 1;"+
                "-fx-border-radius: 8;"+
                "-fx-border-color: #909090");
        message_label.maxWidthProperty().bind(container_pane_.widthProperty().multiply(0.6));
        message_label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_label.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL,18));
        Label white = new Label();
        white.setPrefHeight(15);
        white.setVisible(false);
        message_box.setSpacing(10);
        Text username = new Text(view_model_.getUserName());
        username.setStyle("-fx-fill: black;");
        username.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,14));
        Text time = new Text(LocalDate.now() +" "+ LocalTime.now().toString().substring(0, 5));
        time.setStyle("-fx-fill: #B0B0B0");
        time.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL,10));
        VBox message_box_with_name = new VBox(username,message_label,time);
        message_box_with_name.setSpacing(2);
        message_box_with_name.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_box_with_name.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_box_with_name.setAlignment(Pos.TOP_LEFT);
        message_label.setContextMenu(setupMessageMenu(message_label,message_container, false));
        message_box.getChildren().addAll(None,ViewTool.getDefaultAvatar(username.getText()),message_box_with_name);
        message_container.getChildren().addAll(message_box,white);
        message_box.setPrefHeight(Control.USE_COMPUTED_SIZE);

        container_pane_.getChildren().add(message_container);
        message_labels.add(message_label);
        delay.play();
    }




    // 同步User数据
    public void syncUserData(String username, UUID opposite_id) {
        username_label_.setText(username);
        view_model_.setUserName(username);
        view_model_.setOppositeID(opposite_id);
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
