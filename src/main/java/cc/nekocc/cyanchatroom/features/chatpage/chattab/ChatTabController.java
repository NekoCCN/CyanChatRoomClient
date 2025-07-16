package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;

import java.util.UUID;
import java.util.function.BiConsumer;


// 聊天界面中右侧聊天窗口的索引窗
public class ChatTabController {
    private boolean is_active = false;


    // 后端工具,包含聊天窗
    private final ChatTabViewModel view_model;

    // 用户标签
    private final SimpleObjectProperty<AnchorPane> tab_ = new SimpleObjectProperty<>();

    public ChatTabController(ChatTabViewModel copy, UUID uuid,  BiConsumer<String, Status> callback){
        view_model = new ChatTabViewModel(copy,uuid,this,tab_,callback);
    }





    public AnchorPane getUserTab(){

        return tab_.get();}



    public UUID getUserID() {
        return view_model.getOppositeID();
    }

    public boolean isActive() {return is_active;}

    public void setIsActive(boolean is_active) {this.is_active = is_active;}

    public Status getStatus() {
        return view_model.getOppositeStatus();
    }



}
