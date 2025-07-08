package cc.nekocc.cyanchatroom.features.chatpage;


import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTab;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


import java.util.ArrayList;

public class ChatPageViewModel {



    private ArrayList<ChatTab> user_list_ = new ArrayList<>();
    private final StringProperty username_title_property_ = new SimpleStringProperty();







    public ChatPageViewModel(){
        initialize();
    }


    private void initialize(){
        for(int i = 0;  i <10 ;i++)
            user_list_.add(new ChatTab());

    }

    public void loadUserList(VBox vBox){
        for(ChatTab user : user_list_){
            String name = user.getUser().getUsername();
            if(name.isEmpty())
                throw new IllegalArgumentException("用户名不能为空");
            Label username_label_title_ = new Label(String.valueOf(name.charAt(0)));
            Label username_label_ = new Label(name);
            Label user_status_label_ = new Label(user.getUser().getStatus_().toStringshow());
            Circle circle = new Circle();
            StackPane tab_circle = new StackPane(circle,username_label_title_);
            VBox tab_data_ = new VBox(username_label_,user_status_label_);
            HBox tab = new HBox(tab_circle,tab_data_);
            tab.setStyle("-fx-border-color: #bbbbbb;-fx-border-width: 2;-fx-border-radius: 5");
            tab_circle.setPrefHeight(100);
            tab_circle.setPrefWidth(60);
            tab.setPrefHeight(100);
            tab.setPrefWidth(190);
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
            user_status_label_.setFont(Font.font("Microsoft YaHei", 13));
            user_status_label_.setStyle("-fx-text-fill:"+user.getUser().getStatus_().getColor() );
            tab.getStyleClass().addAll(Styles.ACCENT);
            vBox.getChildren().add(tab);
        }
    }
    public StringProperty getUsername_title_property() {
        return username_title_property_;
    }


}
