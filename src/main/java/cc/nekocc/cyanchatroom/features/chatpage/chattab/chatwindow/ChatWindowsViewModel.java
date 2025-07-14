package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;


import cc.nekocc.cyanchatroom.util.ViewTool;
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

    public StackPane getUSerAvatar() {
        return ViewTool.getDefaultAvatar((opposite_username.isEmpty() ? "NULL": opposite_username));

    }


}
