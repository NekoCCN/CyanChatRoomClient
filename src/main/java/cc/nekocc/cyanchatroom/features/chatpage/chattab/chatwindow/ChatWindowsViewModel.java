package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;


import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.scene.layout.StackPane;

public class ChatWindowsViewModel {

    private User user_;
    public ChatWindowsViewModel( ){};

    public void setUser(User user_) {
        this.user_ = user_;
    }

    public String getUserName() {
        return user_.getUsername();
    }

    public User getUser() {
        return user_;
    }

    public StackPane getUSerAvatar() {
        return ViewTool.getDefaultAvatar(user_.getUsername());

    }


}
