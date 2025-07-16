package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeController;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.animation.PauseTransition;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.UUID;

public class ChatPageViewModel {
    private final ObjectProperty<AnchorPane>  current_chat_window_pane_ = new SimpleObjectProperty<>();
    private final ObjectProperty<VBox> user_list_box_ = new SimpleObjectProperty<>();
    private final ObjectProperty<ChatWindowsController>  temp_chat_window_ = new SimpleObjectProperty<>();
    private final ObjectProperty<ChatWindowsController>  current_chat_window_ = new SimpleObjectProperty<>();
    private final ObjectProperty<ChatTabController>  current_chat_tab_ = new SimpleObjectProperty<>();
    private final ArrayList<ChatTabController> user_list_ = new ArrayList<>();
    private final BooleanProperty setting_shown = new SimpleBooleanProperty();
    private final BooleanProperty contact_agree_shown = new SimpleBooleanProperty();
    private final BooleanProperty load_over_ = new SimpleBooleanProperty(false);
    private Stage setting_stage_;
    private Stage contact_agree_stage_;
    private ContactAgreeController contact_agree_controller_ ;
    private final ObservableList<ChatTabController> observableUserList = FXCollections.observableArrayList(
            controller -> new Observable[]{controller.getIsLoaded()}  // 监听每个 isLoaded
    );


    public ChatPageViewModel(){
        initialize();
    }



    private void initialize(){
        setupBindings();


    }

    public void refreshUserList(Boolean need_fresh_user_list){
        if(need_fresh_user_list) {
            user_list_.clear();
            observableUserList.clear();
            user_list_box_.get().getChildren().clear();
            initUserList();
        }
        else{
            loadUserList();
        }
        System.out.println("刷新用户列表");
    }


    private void initUserList(){
        ChatTabViewModel copy_info_ = new ChatTabViewModel();
        AppRepository.getInstance().getFriendshipList(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response -> {
            UUID current_uuid = AppRepository.getInstance().currentUserProperty().get().getId();

            for (var user_info : response.getPayload().friendships())
            {
                UUID temp =  user_info.getUserOneId() == current_uuid ? user_info.getUserTwoId() : user_info.getUserOneId();
                user_list_.add(new ChatTabController(copy_info_,temp));
                System.out.println("添加用户: " + temp);
            }
            observableUserList.addAll(user_list_);
            loadUserList();
        });
    }
    //刷新用户列表测试
    private void ceshi(){
        ChatTabViewModel copy_info_ = new ChatTabViewModel();
        user_list_.add(new ChatTabController(copy_info_,UUID.fromString("01980960-c7ee-78b0-8e45-4ef4e3cf4bc1")));
        user_list_.add(new ChatTabController(copy_info_,UUID.fromString("01980961-8437-7b11-a014-fe5c82f2b708")));
        observableUserList.addAll(user_list_);
    }

    public void synchronizeStage(){
        loadSetting();
        loadContactAgree();
    }



    public void loadContactAgree(){

        FXMLLoader loader = ViewTool.loadFXML("fxml/ContactAgreePage.fxml");
        contact_agree_controller_ = loader.getController();
        contact_agree_controller_.setRefreshCallback(() -> refreshUserList(true));
        contact_agree_stage_ = new Stage();
        contact_agree_stage_.setTitle("添加联系人");
        contact_agree_stage_.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/contact_agree_page_icon.png")))));
        contact_agree_stage_.setScene(new Scene(loader.getRoot()));
        contact_agree_stage_.setOnCloseRequest(_ -> contact_agree_shown.set(false));
    }

    public void sendMessageFromMe(String message)
    {
        AppRepository.getInstance().sendMessage("USER",current_chat_tab_.get().getUserID(), "TEXT", false, message);
        current_chat_window_.get().sendMessageFromMe(message);
    }


    public void loadSetting(){
        setting_stage_ = new Stage();
        setting_stage_.setTitle("设置");
        setting_stage_.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/setting_stage_icon.png")))));
        setting_stage_.setScene(new Scene(ViewTool.loadFXML("fxml/Setting.fxml").getRoot()));
        setting_stage_.setOnCloseRequest(_ -> setting_shown.set(false));
    }

    private void loadUserList(){

        for(ChatTabController user : user_list_){
            AnchorPane tab = user.getFirstUserTab();
            tab.setOnMouseClicked(_ ->{
                if(!user.isActive()) {
                    current_chat_window_.set(user.getChatWindow());
                    current_chat_tab_.set(user);
                    tab.setStyle("-fx-background-color: #3574F0");
                    current_chat_window_pane_.get().getChildren().clear();
                    rewriteUserActive();
                    user.setIsActive(true);
                    var pane = user.getSwitchChatPane().get();
                    current_chat_window_pane_.get().getChildren().add(pane);
                    AnchorPane.setTopAnchor(pane, 0.0);
                    AnchorPane.setBottomAnchor(pane, 0.0);
                    AnchorPane.setLeftAnchor(pane, 0.0);
                    AnchorPane.setRightAnchor(pane, 0.0);
                }});

            user_list_box_.get().getChildren().add(tab);
        }
    }





    private void setupBindings() {
        BooleanBinding allLoaded = Bindings.createBooleanBinding(
                () -> observableUserList.stream().allMatch(ChatTabController::isLoaded),
                observableUserList
        );
        if (observableUserList.stream().allMatch(ChatTabController::isLoaded)) {
            load_over_.set(true);
        }
        else{
            load_over_.bind(allLoaded);
        }

        System.out.println("绑定同步");
    }

    public void showSetting(){
        setting_stage_.show();
    }
    public void showContactAgree(){
        contact_agree_controller_.refreshUserRequest();
        contact_agree_stage_.show();}


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

    public ObjectProperty<ChatWindowsController> getCurrentChatWindowProperty()
    {
        return current_chat_window_;
    }

    public AnchorPane getCurrentChatWindow()
    {
        return current_chat_window_.get().getRoot_pane_();
    }

    public BooleanProperty getLoadOver() {
        return load_over_;
    }




    public ObjectProperty<VBox> user_list_box_Property() {return user_list_box_;}
    public ObjectProperty<AnchorPane> current_chat_window_pane_Property() {return current_chat_window_pane_;}
}
