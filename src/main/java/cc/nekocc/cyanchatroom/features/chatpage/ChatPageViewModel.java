package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.util.ArrayList;

public class ChatPageViewModel {



    private final ArrayList<ChatTabController> user_list_ = new ArrayList<>();


    public ChatPageViewModel(){
        initialize();
    }


    private void initialize(){
        for(int i = 0;  i <10 ;i++)
            user_list_.add(new ChatTabController());

    }

    public void loadUserList(VBox vBox, AnchorPane anchorPane){
        for(ChatTabController user : user_list_){
            HBox tab = user.getUserTab();
            tab.setOnMouseClicked(_ ->{
                if(!user.isActive()) {
                    tab.setStyle("-fx-background-color: #3574F0");
                    anchorPane.getChildren().clear();
                    rewriteUserActive();
                    user.setIsActive(true);
                    var pane = user.getSwitchChatPane().get();
                    anchorPane.getChildren().add(pane);
                    AnchorPane.setTopAnchor(pane, 0.0);
                    AnchorPane.setBottomAnchor(pane, 0.0);
                    AnchorPane.setLeftAnchor(pane, 0.0);
                    AnchorPane.setRightAnchor(pane, 0.0);
                }});
            vBox.getChildren().add(tab);
        }
    }



    private void rewriteUserActive(){
        for(ChatTabController user : user_list_){
            if(user.isActive()) {
                user.setIsActive(false);
                user.getUserTab().setStyle("-fx-background-color: transparent");
            }
        }
    }


}
