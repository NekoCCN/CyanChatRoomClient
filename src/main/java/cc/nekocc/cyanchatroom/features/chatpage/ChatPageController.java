package cc.nekocc.cyanchatroom.features.chatpage;

import cc.nekocc.cyanchatroom.domain.chatwindow.ChatTab;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.fxml.FXML;
import atlantafx.base.theme.Styles;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ResourceBundle;
import java.net.URL;


public class ChatPageController implements Initializable {

    @FXML
    private Label username_title_label_;   // 用户名
    @FXML
    private Label username_label_;
    @FXML
    private AnchorPane main_pane;
    @FXML
    private ScrollPane list_scrollPane_;
    @FXML
    private ChoiceBox<Status> user_status_;
    @FXML
    private Rectangle user_background_rectangle_;
    @FXML
    private HBox chat_windows_hbox_;
    @FXML
    private VBox user_list_vbox_;



    private final ChatPageViewModel view_model_ =  new ChatPageViewModel();


    public ChatPageController(){}
    // 初始化
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        setupStyle();
        synchronizeData();
        setupUI();
        loadUserList();


    }

    public void loadUserList()
    {
        for(ChatTab user:view_model_.getUser_list_()){
            String name = user.getUser().getUsername();
            if(name.isEmpty())
                throw new IllegalArgumentException("用户名不能为空");
            Label username_label_title_ = new Label(String.valueOf(name.charAt(0)));
            Label username_label_ = new Label(name);
            Label user_status_label_ = new Label(user.getUser().getStatus_().toString());
            Circle circle = new Circle();
            StackPane tab_circle = new StackPane(circle,username_label_title_);
            VBox tab_data_ = new VBox(username_label_,user_status_label_);
            HBox tab = new HBox(tab_circle,tab_data_);
            tab.setStyle("-fx-border-color: #bbbbbb;-fx-border-width: 2;-fx-border-radius: 5");
            tab_circle.setPrefHeight(100);
            tab_circle.setPrefWidth(80);
            tab_circle.setLayoutX(10);
            tab.setPrefHeight(100);
            tab.setPrefWidth(190);
            circle.setStyle("-fx-fill: #dbdbdb;");
            circle.setCenterX(40);
            circle.setRadius(25);
            circle.setCenterY(35);
            username_label_title_.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,23));
            username_label_title_.setStyle("-fx-text-fill: #57606A");
            username_label_title_.setLayoutX(10);
            username_label_title_.setLayoutY(40);
            username_label_.setFont(Font.font("Microsoft YaHei", 19));
            username_label_.setStyle("-fx-text-fill: black;");
            user_status_label_.setFont(Font.font("Microsoft YaHei", 15));
            user_status_label_.setStyle("-fx-text-fill:"+user.getUser().getStatus_().getColor() );
            tab.getStyleClass().addAll(Styles.ACCENT);
            user_list_vbox_.getChildren().add(tab);
        }

    }

    // 样式设置
    private void setupStyle() {
        user_background_rectangle_.getStyleClass().addAll(Styles.ROUNDED);
        user_status_.getStyleClass().addAll(Styles.ROUNDED,Styles.ACCENT);
        username_title_label_.getStyleClass().addAll(Styles.TEXT_MUTED);
        username_label_.getStyleClass().addAll(Styles.TITLE_1);
        list_scrollPane_.getStyleClass().addAll(Styles.CENTER_PILL);
    }

    // 数据同步
    private void synchronizeData()
    {
        //username_title_label_.textProperty().bindBidirectional(view_model_.getUsername_title_property());

    }

    private void setupUI(){
        user_status_.getItems().addAll(Status.ONLINE, Status.BUSY, Status.DoNotDisturb, Status.INVISIBLE,Status.AWAY,Status.OFFLINE);
        user_status_.setValue(Status.ONLINE);

    }




}
