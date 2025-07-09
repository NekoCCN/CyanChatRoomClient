package cc.nekocc.cyanchatroom.features.chatpage;


import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTab;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
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

    public void loadUserList(VBox vBox, AnchorPane anchorPane){
        for(ChatTab user : user_list_){
            HBox tab = user.getChatTabPane();
            vBox.getChildren().add(tab);
            tab.setOnMouseEntered(e->{
                tab.setStyle("-fx-background-color: #E7E7E7");
            });
            tab.setOnMouseExited(e->{
                tab.setStyle("-fx-background-color: transparent");
            });
            tab.setOnMouseClicked(e->{
                tab.setStyle("-fx-background-color: #3574F0");
                anchorPane.getChildren().clear();
                anchorPane.getChildren().add(user.switchChatPane());
                AnchorPane.setTopAnchor(tab, 0.0);    // 距顶部10像素
                AnchorPane.setBottomAnchor(tab, 0.0); // 距底部10像素
                AnchorPane.setLeftAnchor(tab, 0.0);   // 距左侧10像素
                AnchorPane.setRightAnchor(tab, 0.0);
            });

        }
    }
    public StringProperty getUsername_title_property() {
        return username_title_property_;
    }



}
