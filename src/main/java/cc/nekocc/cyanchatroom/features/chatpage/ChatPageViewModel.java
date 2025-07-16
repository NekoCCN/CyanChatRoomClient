package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.Buffer.Buffer;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.contact.ContactListController;
import cc.nekocc.cyanchatroom.features.chatpage.contact.SyncDataCallBack;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeController;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.util.UUID;

import static cc.nekocc.cyanchatroom.Buffer.Buffer.current_chat_window_;

public class ChatPageViewModel implements SyncDataCallBack {


    // 设置这个窗口是否在显示
    private final BooleanProperty setting_shown = new SimpleBooleanProperty();
    // 好友申请是否在显示
    private final BooleanProperty contact_agree_shown = new SimpleBooleanProperty();
    // 这个是设置窗口
    private Stage setting_stage_;
    // 这个是好友申请窗口
    private Stage contact_agree_stage_;
    // 这个是好友申请窗口控制器
    private ContactAgreeController contact_agree_controller_ ;
    // 这个是ChatWindow那个源本，后续的chatwindow都是从这个深拷贝的
    private final ChatTabViewModel copy_info_ = new ChatTabViewModel();
    // 好友列表的控制器
    private ContactListController contact_list ;

    public ChatPageViewModel(){
        initialize();
    }

    private void initialize(){
        initUserList();
    }

    /**
     * 这个方法用于刷新用户的列表，动画表现上就是先删除用户然后突然出现
     * 目前这个函数的调用只会在好友申请中完成了添加，删除好友，和关闭窗口的时候实现
     * 然后有一点没有优化的是，我的chatwindow目前还在chattab里面没有分开，意味着刷新的时候你当前的聊天窗口也会直接消失
     * 我目前打算将所有chatwindow存到缓存池当中，然后这样列表的刷新不影响当天聊天窗口的存在
     *刷新用户列表操作**/
    public void refreshUserList(){
        Buffer.user_tab_list_.clear();
        Buffer.user_list_box_.get().getChildren().clear();
        initUserList();
        System.out.println("刷新用户列表");
    }

    // 初始化用户列表
    private void initUserList(){
        if(contact_list != null)
            contact_list.clearContactList();
        AppRepository.getInstance().getActiveFriendshipList(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response -> {
            UUID current_uuid = AppRepository.getInstance().currentUserProperty().get().getId();
            for (var user_info : response.getPayload().friendships())
            {
                UUID temp =  user_info.getUserOneId().equals(current_uuid) ? user_info.getUserTwoId() : user_info.getUserOneId();
                Buffer.user_tab_list_.add(new ChatTabController(copy_info_,temp, this::syncContact));
                System.out.println("添加用户: " + temp);
            }
        });
    }

    // 这个是给好友列表添加好友的回调函数，使用的BiConsumer<String, Status>
    @Override
    public void syncContact(String  username, Status status){
        contact_list.addContact(username,status);
    }

    // 前端Controller喊viewmodel初始化一些舞台的函数
    public void synchronizeStage(){
        loadSetting();
        loadContactAgree();
        loadContactList();
    }

    private void loadContactList(){
        contact_list = ViewTool.loadFXML("fxml/ContactList.fxml").getController();

    }

    public AnchorPane getContactListPane(){
        return contact_list.getRootPane();
    }


    public void loadContactAgree(){

        FXMLLoader loader = ViewTool.loadFXML("fxml/ContactAgreePage.fxml");
        contact_agree_controller_ = loader.getController();
        contact_agree_controller_.setRefreshCallback(this::refreshUserList);
        contact_agree_stage_ = new Stage();
        contact_agree_stage_.setTitle("添加联系人");
        contact_agree_stage_.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/contact_agree_page_icon.png")))));
        contact_agree_stage_.setScene(new Scene(loader.getRoot()));
        contact_agree_stage_.setOnCloseRequest(_ -> {
            contact_agree_shown.set(false);
            refreshUserList();
        });
    }

    public void sendMessageFromMe(String message)
    {
        AppRepository.getInstance().sendMessage("USER",current_chat_window_.get().getUserID(), "TEXT", false, message);
        Buffer.current_chat_window_.get().sendMessageFromMe(message);
    }


    public void loadSetting(){
        setting_stage_ = new Stage();
        setting_stage_.setTitle("设置");
        setting_stage_.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/setting_stage_icon.png")))));
        setting_stage_.setScene(new Scene(ViewTool.loadFXML("fxml/Setting.fxml").getRoot()));
        setting_stage_.setOnCloseRequest(_ -> setting_shown.set(false));
    }

    public void showSetting(){
        setting_stage_.show();
    }
    public void showContactAgree(){
        contact_agree_controller_.refreshUserRequest();
        contact_agree_stage_.show();}




    public BooleanProperty getSettingShown(){
        return setting_shown;
    }




    public BooleanProperty getContactAgreeShown() {
        return contact_agree_shown;
    }



}
