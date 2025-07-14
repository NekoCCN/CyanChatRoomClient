package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;


import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.StackPane;

public class ChatWindowsViewModel {
    private String opposite_username = " ";
    public ChatWindowsViewModel( ){};
    public String getUserName() {
        return opposite_username;
    }
    public void setUserName(String username_) {
        this.opposite_username = username_;
    }


}
