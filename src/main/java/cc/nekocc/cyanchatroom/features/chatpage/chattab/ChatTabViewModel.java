package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.Buffer.Buffer;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.factories.StatusFactory;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.UUID;
import java.util.function.BiConsumer;


// 聊天标签视图模型以及对应的聊天窗的父类节点
public class ChatTabViewModel {


       private ChatWindowsController chat_windows_controller ;
       private UUID opposite_user_id;
       private final StringProperty user_name_property_ = new SimpleStringProperty(" ");
       private final SimpleObjectProperty<Status> status_property_ = new SimpleObjectProperty<>();
       private  BiConsumer<String, Status> sync_data_callback_ = null;


    public ChatTabViewModel() {
        //   this.opposite_user_id = UUID.fromString("0197ef89-9434-7056-ba9d-ba56aba677a1");
           chat_windows_controller = ViewTool.loadFXML("fxml/ChatWindow.fxml").getController();
        if (chat_windows_controller == null) {
            throw new NullPointerException("聊天窗加载失败");
        }
    }

    /**
     *  现在tab的创建会先尝试绑定buffer中的现存的窗口，如果没有就新建
     *  需要优化的点是这个切换的过程有明显的撕裂感
     *  我需要尽快优化这个刷新的过程
     */
    public ChatTabViewModel(ChatTabViewModel copy_, UUID uuid, ChatTabController chattab, SimpleObjectProperty<AnchorPane> callback, BiConsumer<String, Status>  Datacallback){
        this.opposite_user_id = uuid;
        chat_windows_controller = findChatWindowBuffer();
        if(chat_windows_controller == null) {
            chat_windows_controller = copy_.getChatWindowCopy();
            Buffer.chat_window_list_.add(chat_windows_controller);
        }
        if (chat_windows_controller != null) {
            chat_windows_controller.reLoad();
        }else{
            throw new NullPointerException("聊天窗加载失败");
        }
        this.sync_data_callback_ = Datacallback;
        synchronizeData(chattab,callback);
    }

    private ChatWindowsController findChatWindowBuffer(){
        for(ChatWindowsController chat_windows_controller_:Buffer.chat_window_list_)
            if(chat_windows_controller_.getUserID().equals(opposite_user_id)) {
                return chat_windows_controller_;
            }
        return null;
    }


    // 核心操作，刷新数据
    public void synchronizeData(ChatTabController chattab,SimpleObjectProperty<AnchorPane>  callback){
        AppRepository.getInstance().getUserDetails(opposite_user_id).thenAccept(response ->
        {
            user_name_property_.set(response.getPayload().nick_name());
            status_property_.set(StatusFactory.fromUser(response.getPayload()));
            System.out.println("用户数据同步成功,username :"+user_name_property_.get()+" status:"+status_property_.get().toString());
            synchronizeToWindow();
            callback.set(this.getChatTabPane(chattab));
            callback.get().setOnMouseClicked(_ ->{
                Buffer.rewriteUserActive();
                chattab.setIsActive( true);
                callback.get().setStyle("-fx-background-color: #3574F0");
                Buffer.chat_windows_contain_.get().getChildren().clear();
                AnchorPane.setLeftAnchor(chat_windows_controller.getRoot_pane_(), 0.0);
                AnchorPane.setRightAnchor(chat_windows_controller.getRoot_pane_(), 0.0);
                AnchorPane.setTopAnchor(chat_windows_controller.getRoot_pane_(), 0.0);
                AnchorPane.setBottomAnchor(chat_windows_controller.getRoot_pane_(), 0.0);
                Buffer.current_chat_window_.set(chat_windows_controller);
                Buffer.chat_windows_contain_.get().getChildren().add(chat_windows_controller.getRoot_pane_());
            });
            if(Buffer.current_chat_window_.get() == chat_windows_controller ) {
                chattab.setIsActive(true);
                callback.get().setStyle("-fx-background-color: #3574F0");
            }
            sync_data_callback_.accept(user_name_property_.get(),status_property_.get());
            Buffer.user_list_box_.get().getChildren().add(callback.get());
        });

    }

    // 创建聊天标签
    public AnchorPane getChatTabPane(ChatTabController chattab){

        if(user_name_property_.get().isEmpty())
            user_name_property_.set("未加载");
        Label username_label_title_ = new Label(String.valueOf(user_name_property_.get().charAt(0)));
        Label username_label_ = new Label(user_name_property_.get());
        Label user_status_label_ = new Label(status_property_.get().toDisplayString());
        user_status_label_.textProperty().bind(status_property_.asString());
        Circle circle = new Circle();
        StackPane tab_circle = new StackPane(circle,username_label_title_);
        VBox tab_data_ = new VBox(username_label_,user_status_label_);
        tab_circle.setPrefHeight(80);
        tab_circle.setPrefWidth(60);
        circle.setStyle("-fx-fill: #dbdbdb;");
        circle.setCenterX(40);
        circle.setRadius(24);
        circle.setCenterY(45);
        username_label_title_.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,20));
        username_label_title_.setStyle("-fx-text-fill: #57606A");

        username_label_.setFont(Font.font("Microsoft YaHei", 20));
        username_label_.setStyle("-fx-text-fill: black;");
        user_status_label_.setFont(Font.font("Microsoft YaHei",FontWeight.BOLD,16));
        user_status_label_.setStyle("-fx-text-fill:"+status_property_.get().getColor() );
        AnchorPane tab = new AnchorPane(tab_circle,tab_data_);
        tab.getStyleClass().addAll(Styles.ACCENT);
        tab.setOnMouseEntered(_ ->{
            if(!chattab.isActive())
                tab.setStyle("-fx-background-color: #E7E7E7");
        });
        tab.setOnMouseExited(_ ->{
            if(!chattab.isActive())
                tab.setStyle("-fx-background-color: transparent");
        });
        AnchorPane.setTopAnchor(tab_circle,5.0);
        AnchorPane.setLeftAnchor(tab_circle,7.0);
        AnchorPane.setBottomAnchor(tab_circle,5.0);
        AnchorPane.setTopAnchor(tab_data_,20.0);
        AnchorPane.setLeftAnchor(tab_data_,70.0);
        tab.setPrefHeight(Control.USE_COMPUTED_SIZE);
        tab.setPrefWidth(Control.USE_COMPUTED_SIZE);
        return  tab;
    }

    public void synchronizeToWindow() {
        chat_windows_controller.syncUserData(user_name_property_.get(),opposite_user_id);
    }

    public UUID getOppositeID() {
        return opposite_user_id;
    }

    public Status getOppositeStatus() {
        return status_property_.get();
    }

    public String getOppositeUserName() {
        return user_name_property_.get();
    }

    public ChatWindowsController getChatWindowCopy(){
        try {
            return chat_windows_controller.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    public ChatWindowsController getChatWindow(){return chat_windows_controller;}
}

