package cc.nekocc.cyanchatroom.domain.chatwindow;

import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;



// 聊天界面中右侧聊天窗口的索引窗
public class ChatTab {
    private final ObjectProperty<Parent> chat_window_pane_ = new SimpleObjectProperty<>();
    private User user;


    public ChatTab(){
        this.user = new User("示例用户"+(int)(Math.random()*10), Status.getRandomStatus());
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
