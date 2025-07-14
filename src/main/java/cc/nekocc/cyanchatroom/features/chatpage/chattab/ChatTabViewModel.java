package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.factories.StatusFactory;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.*;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.UUID;


// 聊天标签视图模型以及对应的聊天窗的父类节点
public class ChatTabViewModel {


       private final ChatWindowsController chat_windows_controller ;
       private final ObjectProperty<Parent> chat_windows_property_ = new SimpleObjectProperty<>();
       private UUID opposite_user_id;
       private final StringProperty user_name_property_ = new SimpleStringProperty(" ");
       private final SimpleObjectProperty<Status> status_property_ = new SimpleObjectProperty<>();
       private final BooleanProperty is_loaded_property_ = new SimpleBooleanProperty();



    public ChatTabViewModel() {
        //   this.opposite_user_id = UUID.fromString("0197ef89-9434-7056-ba9d-ba56aba677a1");
           chat_windows_controller = (ChatWindowsController)ViewTool.loadFXML("fxml/ChatWindow.fxml");
        if (chat_windows_controller != null) {
            chat_windows_property_.set(chat_windows_controller.getRoot_pane_());
        }else{
            throw new NullPointerException("聊天窗加载失败");
        }
    }

    public ChatTabViewModel(ChatTabViewModel copy_,UUID uuid){
        this.opposite_user_id = uuid;
        chat_windows_controller = copy_.getChatWindowCopy();
        if (chat_windows_controller != null) {
            chat_windows_controller.reLoad();
            chat_windows_property_.set(chat_windows_controller.getRoot_pane_());
        }else{
            throw new NullPointerException("聊天窗加载失败");
        }
        synchronizeData();
    }

    public void synchronizeData(){
        AppRepository.getInstance().getUserDetails(opposite_user_id).thenAccept(response ->
        {
            user_name_property_.set(response.getPayload().username());
            status_property_.set(StatusFactory.fromUser(response.getPayload()));
            is_loaded_property_.bind(user_name_property_.isNotEqualTo("未加载").and(status_property_.isNotNull().and(user_name_property_.isNotEmpty())));
            System.out.println("用户数据同步成功,username :"+user_name_property_.get()+" status:"+status_property_.get().toString());
        });

    }

    // 创建聊天标签
    public AnchorPane getChatTabPane(ChatTabController chattab){

        if(user_name_property_.get().isEmpty())
            user_name_property_.set("未加载");
        Label username_label_title_ = new Label(String.valueOf(user_name_property_.get().charAt(0)));
        Label username_label_ = new Label(user_name_property_.get());
        Label user_status_label_ = new Label(status_property_.get().toDisplayString());
        Circle circle = new Circle();
        StackPane tab_circle = new StackPane(circle,username_label_title_);
        VBox tab_data_ = new VBox(username_label_,user_status_label_);
        tab_circle.setPrefHeight(80);
        tab_circle.setPrefWidth(60);
        circle.setStyle("-fx-fill: #dbdbdb;");
        circle.setCenterX(40);
        circle.setRadius(22);
        circle.setCenterY(45);
        username_label_title_.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,20));
        username_label_title_.setStyle("-fx-text-fill: #57606A");
        username_label_title_.setLayoutX(10);
        username_label_title_.setLayoutY(40);
        username_label_.setFont(Font.font("Microsoft YaHei", 18));
        username_label_.setStyle("-fx-text-fill: black;");
        user_status_label_.setFont(Font.font("Microsoft YaHei",FontWeight.BOLD,14));
        user_status_label_.setStyle("-fx-text-fill:"+status_property_.get().getColor() );
        AnchorPane tab = new AnchorPane(tab_circle,tab_data_);
        tab.setPrefHeight(80);
        tab.setPrefWidth(194);
        tab.getStyleClass().addAll(Styles.ACCENT);
        tab.setOnMouseEntered(e->{
            if(!chattab.isActive())
                tab.setStyle("-fx-background-color: #E7E7E7");
        });
        tab.setOnMouseExited(e->{
            if(!chattab.isActive())
                tab.setStyle("-fx-background-color: transparent");
        });
        AnchorPane.setTopAnchor(tab_circle,6.0);
        AnchorPane.setLeftAnchor(tab_circle,7.0);
        AnchorPane.setBottomAnchor(tab_circle,6.0);
        AnchorPane.setRightAnchor(tab_data_,30.0);
        AnchorPane.setBottomAnchor(tab_data_,6.0);
        AnchorPane.setTopAnchor(tab_data_,6.0);
        return  tab;
    }

    public void synchronizeToWindow() {
        chat_windows_controller.syncUserData(user_name_property_.get());
    }

    public ObjectProperty<Parent> getChatWindowPane() {
        return chat_windows_property_;
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
    public BooleanProperty getLoadingProperty(){return is_loaded_property_;}
}
