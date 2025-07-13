package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeController;
import cc.nekocc.cyanchatroom.features.setting.SettingPage;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.util.ArrayList;

public class ChatPageViewModel {

    private final ObjectProperty<ChatWindowsController>  temp_chat_window_ = new SimpleObjectProperty<>();
    private final ObjectProperty<ChatWindowsController>  current_chat_window_ = new SimpleObjectProperty<>();
    private final ArrayList<ChatTabController> user_list_ = new ArrayList<>();
    private final BooleanProperty setting_shown = new SimpleBooleanProperty();
    private final BooleanProperty contact_agree_shown = new SimpleBooleanProperty();
    private Stage setting_stage;
    private Stage contact_agree_stage;

    public ChatPageViewModel(){
        initialize();
    }


    private void initialize(){
        loadSetting();
        loadContactAgree();
           ChatTabViewModel copy_info_ = new ChatTabViewModel();
        for(int i =0 ; i< 20; i ++)
            user_list_.add(new ChatTabController(copy_info_));
    }

    public void loadContactAgree(){
        ContactAgreeController contact_agree_controller = (ContactAgreeController) ViewTool.loadFXML("fxml/ContactAgreePage.fxml");
        contact_agree_stage = new Stage();
        contact_agree_stage.setTitle("添加联系人");
        contact_agree_stage.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/contact_agree_page_icon.png")))));
        contact_agree_stage.setScene(new Scene(contact_agree_controller.getRootPane()));
        contact_agree_stage.setOnCloseRequest(e1 -> {
            contact_agree_shown.set(false);
        });
    }

    public void sendMessageFromMe(String message){



        current_chat_window_.get().sendMessageFromMe(message);


    }


    public void loadSetting(){
        SettingPage setting_page = (SettingPage) ViewTool.loadFXML("fxml/setting.fxml");
        setting_stage = new Stage();
        setting_stage.setTitle("设置");
        setting_stage.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/setting_stage_icon.png")))));
        setting_stage.setScene(new Scene(setting_page.getRootPane()));
        setting_stage.setOnCloseRequest(e1 -> {
            setting_shown.set(false);
        });
    }

    public void loadUserList(VBox vBox, AnchorPane anchorPane){
        for(ChatTabController user : user_list_){
            AnchorPane tab = user.getUserTab();
            tab.setOnMouseClicked(_ ->{
                if(!user.isActive()) {
                    current_chat_window_.set(user.getChatWindow());
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

    // 数据同步（预留接口）
    public void synchronizeData()
    {
        //username_title_label_.textProperty().bindBidirectional(view_model_.getUsername_title_property());

    }

    public void showSetting(){
        setting_stage.show();
    }
    public void showContactAgree(){contact_agree_stage.show();}


    private void rewriteUserActive(){
        for(ChatTabController user : user_list_){
            if(user.isActive()) {
                user.setIsActive(false);
                user.getUserTab().setStyle("-fx-background-color: transparent");
            }
        }
    }

    public BooleanProperty getSettingShown(){
        return setting_shown;
    }

    public ArrayList<ChatTabController> getUserList(){
        return user_list_;
    }


    public BooleanProperty getContactAgreeShown() {
        return contact_agree_shown;
    }
    public void loadLastWindow(){
        current_chat_window_.set(temp_chat_window_.get());
    }

    public void setCurrentChatWindowNULL(){
        temp_chat_window_.set(current_chat_window_.get());
        current_chat_window_.set(null);
    }
    public boolean isCurrentChatWindowNULL(){
        return current_chat_window_.get() == null;
    }

    public AnchorPane getCurrentChatWindow()
    {
        return current_chat_window_.get().getRoot_pane_();
    }
}
