package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.util.UUID;


// 聊天界面中右侧聊天窗口的索引窗
public class ChatTabController {
    private boolean is_active = false;


    // 后端工具,包含聊天窗
    private final ChatTabViewModel view_model;

    // 用户标签
    private AnchorPane tab_ = new AnchorPane();

    public ChatTabController(ChatTabViewModel copy,UUID uuid){
        view_model = new ChatTabViewModel(copy,uuid);
    }

    public ObjectProperty<Parent> getSwitchChatPane(){
        return view_model.getChatWindowPane();
    }



    public AnchorPane getUserTab(){
        tab_ =view_model.getChatTabPane(this);
        view_model.synchronizeToWindow();
        return tab_;}



    public UUID getUser() {
        return view_model.getOppositeID();
    }

    public boolean isActive() {return is_active;}

    public void setIsActive(boolean is_active) {this.is_active = is_active;}

    public Status getStatus() {
        return view_model.getOppositeStatus();
    }

    public String getUserName() {
        return view_model.getOppositeUserName();
    }

    public BooleanProperty getIsLoaded() {
        return view_model.getLoadingProperty();
    }


    public ChatWindowsController getChatWindow(){
        return view_model.getChatWindow();
    }

    public boolean isLoaded() {
        return view_model.getLoadingProperty().get();
    }
}
