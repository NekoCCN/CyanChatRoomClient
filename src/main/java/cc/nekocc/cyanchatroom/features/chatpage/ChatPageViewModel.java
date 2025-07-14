package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabViewModel;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.features.chatpage.contactagree.ContactAgreeController;
import cc.nekocc.cyanchatroom.features.setting.SettingPage;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



import java.util.ArrayList;
import java.util.UUID;

public class ChatPageViewModel {

    private final ObjectProperty<ChatWindowsController>  temp_chat_window_ = new SimpleObjectProperty<>();
    private final ObjectProperty<ChatWindowsController>  current_chat_window_ = new SimpleObjectProperty<>();
    private final ArrayList<ChatTabController> user_list_ = new ArrayList<>();
    private final BooleanProperty setting_shown = new SimpleBooleanProperty();
    private final BooleanProperty contact_agree_shown = new SimpleBooleanProperty();
    private final BooleanProperty load_over_ = new SimpleBooleanProperty(false);
    private Stage setting_stage_;
    private Stage contact_agree_stage_;
    private final ObservableList<ChatTabController> observableUserList = FXCollections.observableArrayList(
            controller -> new Observable[]{controller.getIsLoaded()}  // 监听每个 isLoaded
    );


    public ChatPageViewModel(){
        initialize();
    }



    private void initialize(){
        ChatTabViewModel copy_info_ = new ChatTabViewModel();
        user_list_.add(new ChatTabController(copy_info_,UUID.fromString("0197ef89-9434-7056-ba9d-ba56aba677a1")));
        observableUserList.add(user_list_.get(0));
        setupBindings();
        addListener();
    }

    private void addListener(){
        load_over_.addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                loadSetting();
                loadContactAgree();
            }
        });
    }

    public void loadContactAgree(){
        contact_agree_stage_ = new Stage();
        contact_agree_stage_.setTitle("添加联系人");
        contact_agree_stage_.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/contact_agree_page_icon.png")))));
        contact_agree_stage_.setScene(new Scene(ViewTool.loadFXML("fxml/ContactAgreePage.fxml").getRoot()));
        contact_agree_stage_.setOnCloseRequest(_ -> contact_agree_shown.set(false));
    }

    public void sendMessageFromMe(String message)
    {
        current_chat_window_.get().sendMessageFromMe(message);
    }


    public void loadSetting(){
        setting_stage_ = new Stage();
        setting_stage_.setTitle("设置");
        setting_stage_.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/setting_stage_icon.png")))));
        setting_stage_.setScene(new Scene(ViewTool.loadFXML("fxml/Setting.fxml").getRoot()));
        setting_stage_.setOnCloseRequest(_ -> setting_shown.set(false));
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

    public AnchorPane getCurrentChatWindow()
    {
        return current_chat_window_.get().getRoot_pane_();
    }

    public BooleanProperty getLoadOver() {
        return load_over_;
    }



}
