package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;


// 聊天界面中右侧聊天窗口的索引窗
public class ChatTabController {
    private boolean is_active = false;


    // 后端工具,包含聊天窗
    private final ChatTabViewModel view_model;

    // 用户标签
    private final AnchorPane tab_;

    public ChatTabController(){
        view_model = new ChatTabViewModel();
        tab_ = view_model.getChatTabPane(this);
    }

    public ChatTabController(ChatTabViewModel copy){
        view_model = new ChatTabViewModel(copy);
        tab_ = view_model.getChatTabPane(this);
    }

    public ObjectProperty<Parent> getSwitchChatPane(){
        syncUserData(getUser());
        return view_model.getChatWindowPane();
    }


    public AnchorPane getUserTab(){return tab_;}



    public User getUser() {
        return view_model.getUser();
    }

    public boolean isActive() {return is_active;}

    public void setIsActive(boolean is_active) {this.is_active = is_active;}

    public void syncUserData(User user){
        view_model.syncUserData(user);
    }

    public ChatWindowsController getChatWindow(){
        return view_model.getChatWindow();
    }
}
