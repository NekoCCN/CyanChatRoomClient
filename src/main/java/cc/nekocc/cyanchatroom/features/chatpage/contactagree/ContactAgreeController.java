package cc.nekocc.cyanchatroom.features.chatpage.contactagree;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.Friendship;
import cc.nekocc.cyanchatroom.model.entity.FriendshipRequestStatus;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.UUID;

public class ContactAgreeController implements Initializable {
    @FXML
    public TextField userid_input_;
    @FXML
    public AnchorPane user_info_send_requset_;
    @FXML
    public Label user_nickname_;
    @FXML
    public StackPane user_avator_pane_;
    @FXML
    public Button search_button_;
    @FXML
    public VBox user_info_vbox_;
    @FXML
    public Label username_label_;
    @FXML
    public Label user_words_label;
    @FXML
    public Button send_request_button_;
    @FXML
    public Button refresh_button_;
    @FXML
    public VBox scroll_contain_pane;

    private UUID current_uuid ;
    private RefreshCallback refresh_callback_ = null;


    @Override
    public void initialize(java.net.URL url, java.util.ResourceBundle resourceBundle) {
        current_uuid  = AppRepository.getInstance().currentUserProperty().get().getId();
        refreshUserRequest();
        setupStyle();
        setupEvent();
        refresh_button_.setOnAction(_-> {
            if (refresh_callback_ != null)
                refresh_callback_.refresh();
        });
    }


    private void addUserRequest(Friendship firendship,String usename, String avatar_url){

        AnchorPane user_request_box = new AnchorPane();
        StackPane default_avatar = ViewTool.getDefaultAvatar(usename);
        AnchorPane.setTopAnchor(default_avatar, 10.0);
        AnchorPane.setLeftAnchor(default_avatar, 10.0);
        AnchorPane.setBottomAnchor(default_avatar, 10.0);
        Label username_label = new Label(usename);
        AnchorPane.setTopAnchor(username_label, 20.0);
        AnchorPane.setLeftAnchor(username_label, 80.0);
        username_label.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));
        if(firendship.getStatus() == FriendshipRequestStatus.PENDING){
            if(firendship.getActionUserId() == current_uuid){
                Label status_label = new Label("等待对方接受");
                AnchorPane.setTopAnchor(status_label, 50.0);
                AnchorPane.setLeftAnchor(status_label, 80.0);
                status_label.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 14));
                user_request_box.getChildren().addAll(default_avatar,username_label,status_label);
            }
            else{
                Button accept_button = new Button("接受");
                accept_button.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 14));
                Button refuse_button = new Button("拒绝");
                refuse_button.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 14));
                accept_button.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
                refuse_button.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
                accept_button.setOnAction(_->{
                    AppRepository.getInstance().acceptFriendshipRequest(firendship.getId()).thenAccept(response -> {
                        if(response.getPayload().status()) {
                            ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "已接受好友请求");
                            if (refresh_callback_ != null)
                                refresh_callback_.refresh();
                        }else{
                            ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "接受好友请求失败");
                        }
                        refreshUserRequest();
                    });

                });
                AnchorPane.setTopAnchor(accept_button, 50.0);
                AnchorPane.setLeftAnchor(accept_button, 80.0);
                AnchorPane.setTopAnchor(refuse_button, 50.0);
                AnchorPane.setLeftAnchor(refuse_button, 120.0);
                user_request_box.getChildren().addAll(default_avatar,username_label,accept_button,refuse_button);

            }
        }
        else if(firendship.getStatus() == FriendshipRequestStatus.ACCEPTED)

        user_request_box.setPrefHeight(Control.USE_COMPUTED_SIZE);
        user_request_box.setPrefWidth(Control.USE_COMPUTED_SIZE);
        scroll_contain_pane.getChildren().add(user_request_box);



    }



    public void refreshUserRequest() {

        AppRepository.getInstance().getFriendshipList(current_uuid).thenAccept(response -> {
            if(!response.getPayload().status() ){
                ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "无法获取好友列表");
            }
            else{
                for(  Friendship friendship :response.getPayload().friendships()){
                    UUID opposite_id = friendship.getUserOneId().equals(current_uuid)? friendship.getUserTwoId():friendship.getUserOneId();
                    AppRepository.getInstance().getUserDetails(opposite_id).thenAccept(response2 -> {
                        if(!response2.getPayload().request_status())
                            ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "无法获取用户信息");
                        else{
                            addUserRequest(friendship,response2.getPayload().username(),response2.getPayload().avatar_url());
                        }
                    });
                }
            }


        });





    }

    public void setRefreshCallback(RefreshCallback callback) {refresh_callback_ = callback;}


    private void setupStyle(){
        search_button_.getStyleClass().addAll(Styles.BOTTOM);


    }

    private void setupEvent(){
        search_button_.setOnAction(e->{
            AppRepository.getInstance().getUuidByUsername(userid_input_.getText().trim()).thenAccept(response->{
                if (!response.getPayload().request_status())
                {
                    ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "用户不存在");
                }
                else {
                    AppRepository.getInstance().getUserDetails(response.getPayload().user_id()).thenAccept(response2 -> {
                        if (!response2.getPayload().request_status())
                        {
                            ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "无法获取用户信息");
                        }
                        else{

                            username_label_.setText("用户名："+response2.getPayload().username());
                            user_info_vbox_.getChildren().set(0,ViewTool.getDefaultAvatar(response2.getPayload().nick_name()));
                            user_nickname_.setText("昵称："+response2.getPayload().nick_name());
                            user_words_label.setText("个性签名："+response2.getPayload().signature());
                            user_info_send_requset_.setVisible( true);
                            send_request_button_.setOnAction(_->{
                                AppRepository.getInstance().sendFriendshipRequest(response.getPayload().user_id()).thenAccept(response3 ->{
                                   if(response3.getPayload().status()) {
                                       ViewTool.showAlert(Alert.AlertType.INFORMATION, "发送好友请求", "已发送好友请求");
                                       user_info_send_requset_.setVisible(false);
                                       userid_input_.clear();
                                       if (refresh_callback_ != null)
                                           refresh_callback_.refresh();
                                   }
                                   else{
                                       ViewTool.showAlert(Alert.AlertType.ERROR, "发送好友请求失败", "请检查网络");
                                   }
                                });
                            });
                        }
                    });
                }
            });

        });




    }





}
