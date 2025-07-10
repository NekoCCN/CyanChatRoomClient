package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


// 聊天标签视图模型以及对应的聊天窗的父类节点
public class ChatTabViewModel {


       private final ChatWindowsController chat_windows_controller ;
       private final ObjectProperty<Parent> chat_windows_property_ = new SimpleObjectProperty<>();
       private final User user;


    public ChatTabViewModel() {
        this.user = new User("示例用户"+(int)(Math.random()*10), Status.getRandomStatus());
        chat_windows_controller = (ChatWindowsController)ViewTool.loadFXML("fxml/ChatWindow.fxml");
        if (chat_windows_controller != null) {
            chat_windows_property_.set(chat_windows_controller.getRoot_pane_());
        }else{
            throw new NullPointerException("聊天窗加载失败");
        }
    }

    public ChatTabViewModel(ChatTabViewModel copy_){
        this.user = new User("示例用户"+(int)(Math.random()*10), Status.getRandomStatus());
        chat_windows_controller = copy_.getChatWindowCopy();
        if (chat_windows_controller != null) {
            chat_windows_property_.set(chat_windows_controller.getRoot_pane_());
        }else{
            throw new NullPointerException("聊天窗加载失败");
        }
    }

    // 创建聊天标签
    public AnchorPane getChatTabPane(ChatTabController chattab){
        String name = chattab.getUser().getUsername();
        if(name.isEmpty())
            throw new IllegalArgumentException("用户名不能为空");
        Label username_label_title_ = new Label(String.valueOf(name.charAt(0)));
        Label username_label_ = new Label(name);
        Label user_status_label_ = new Label(chattab.getUser().getStatus_().toStringshow());
        Circle circle = new Circle();
        StackPane tab_circle = new StackPane(circle,username_label_title_);
        VBox tab_data_ = new VBox(username_label_,user_status_label_);
        tab_circle.setPrefHeight(100);
        tab_circle.setPrefWidth(60);
        circle.setStyle("-fx-fill: #dbdbdb;");
        circle.setCenterX(40);
        circle.setRadius(22);
        circle.setCenterY(45);
        username_label_title_.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,20));
        username_label_title_.setStyle("-fx-text-fill: #57606A");
        username_label_title_.setLayoutX(10);
        username_label_title_.setLayoutY(40);
        username_label_.setFont(Font.font("Microsoft YaHei", 18));
        username_label_.setStyle("-fx-text-fill: black;");
        user_status_label_.setFont(Font.font("Microsoft YaHei",FontWeight.BOLD,14));
        user_status_label_.setStyle("-fx-text-fill:"+chattab.getUser().getStatus_().getColor() );
        AnchorPane tab = new AnchorPane(tab_circle,tab_data_);
        tab.setPrefHeight(80);
        tab.setPrefWidth(194);
        tab.getStyleClass().addAll(Styles.ACCENT);
        tab.setOnMouseEntered(e->{
            if(!chattab.isActive())
                tab.setStyle("-fx-background-color: #E7E7E7");
        });
        tab.setOnMouseExited(e->{
            if(!chattab.isActive())
                tab.setStyle("-fx-background-color: transparent");
        });
        AnchorPane.setTopAnchor(tab_circle,6.0);
        AnchorPane.setLeftAnchor(tab_circle,7.0);
        AnchorPane.setBottomAnchor(tab_circle,6.0);
        AnchorPane.setRightAnchor(tab_data_,30.0);
        AnchorPane.setBottomAnchor(tab_data_,6.0);
        AnchorPane.setTopAnchor(tab_data_,6.0);
        return  tab;
    }

    public ObjectProperty<Parent> getChatWindowPane() {
        return chat_windows_property_;
    }


    public void syncUserData(User user) {
        chat_windows_controller.syncUserData(user);
    }

    public User getUser() {
        return user;
    }

    public ChatWindowsController getChatWindowCopy(){
        try {
            return chat_windows_controller.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
