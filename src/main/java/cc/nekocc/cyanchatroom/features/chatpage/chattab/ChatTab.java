package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


// 聊天界面中右侧聊天窗口的索引窗
public class ChatTab  {
  private final User user;
    private final ChatTabViewModel view_model = new ChatTabViewModel();


    public ChatTab(){
        this.user = new User("示例用户"+(int)(Math.random()*10), Status.getRandomStatus());
    }
    public  HBox getChatTabPane(){
        String name = user.getUsername();
        if(name.isEmpty())
            throw new IllegalArgumentException("用户名不能为空");
        Label username_label_title_ = new Label(String.valueOf(name.charAt(0)));
        Label username_label_ = new Label(name);
        Label user_status_label_ = new Label(user.getStatus_().toStringshow());
        Circle circle = new Circle();
        StackPane tab_circle = new StackPane(circle,username_label_title_);
        VBox tab_data_ = new VBox(username_label_,user_status_label_);
        HBox tab = new HBox(tab_circle,tab_data_);
        tab_circle.setPrefHeight(60);
        tab_circle.setPrefWidth(60);
        tab.setPrefHeight(160);
        tab.setPrefWidth(194);
        tab.setSpacing(10);
        circle.setStyle("-fx-fill: #dbdbdb;");
        circle.setCenterX(40);
        circle.setRadius(22);
        circle.setCenterY(35);
        username_label_title_.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,20));
        username_label_title_.setStyle("-fx-text-fill: #57606A");
        username_label_title_.setLayoutX(10);
        username_label_title_.setLayoutY(40);
        username_label_.setFont(Font.font("Microsoft YaHei", 18));
        username_label_.setStyle("-fx-text-fill: black;");
        user_status_label_.setFont(Font.font("Microsoft YaHei",FontWeight.BOLD,14));
        user_status_label_.setStyle("-fx-text-fill:"+user.getStatus_().getColor() );
        tab.getStyleClass().addAll(Styles.ACCENT);
        return tab;

    }

    public AnchorPane switchChatPane(){
        return view_model.getChat_window_pane();
    }



    public User getUser() {
        return user;
    }


}
