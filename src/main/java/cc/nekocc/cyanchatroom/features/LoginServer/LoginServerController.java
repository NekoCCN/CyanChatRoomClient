package cc.nekocc.cyanchatroom.features.LoginServer;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.Navigator;
import cc.nekocc.cyanchatroom.model.AppRepository;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginServerController implements Initializable {

    @FXML
    public TextField server_ip_input_;
    @FXML
    public Button login_button_;
    @FXML
    public Label connection_status_;


    private final Timeline connecting_animation_ = new Timeline();
    private final Timeline reconnecting_animation_ = new Timeline();


    @Override
    public void initialize(URL url, ResourceBundle resources) {
        setupStyle();
        setupAnimation();
        setupEvent();

    }

    private void setupStyle()
    {
        server_ip_input_.getStyleClass().addAll(Styles.TEXT_NORMAL);
        login_button_.getStyleClass().addAll(Styles.ACCENT);
    }


    private void setupAnimation(){
        connecting_animation_.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.3), _ -> connection_status_.setText("正在链接服务器.")),
                new KeyFrame(Duration.seconds(0.6), _ -> connection_status_.setText("正在链接服务器. .")),
                new KeyFrame(Duration.seconds(0.9), _ -> connection_status_.setText("正在链接服务器. . ."))
        );
        reconnecting_animation_.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.3), _ -> connection_status_.setText("正在重连服务器.")),
                new KeyFrame(Duration.seconds(0.6), _ -> connection_status_.setText("正在重连服务器. .")),
                new KeyFrame(Duration.seconds(0.9), _ -> connection_status_.setText("正在重连服务器. . ."))
        );
        reconnecting_animation_.setCycleCount(Timeline.INDEFINITE);
        connecting_animation_.setCycleCount(Timeline.INDEFINITE);
    }
    private void setupEvent(){
        login_button_.setOnAction(_ ->{

            AppRepository.getInstance().connectToServer(server_ip_input_.getText().trim());
            AppRepository.getInstance().connectionStatusProperty().addListener(((_, _, t1) -> {
                if(t1 == AppRepository.ConnectionStatus.CONNECTING) {
                    connecting_animation_.playFromStart();
                    login_button_.setDisable(true);
                    server_ip_input_.setEditable(false);
                }
                else if(t1 == AppRepository.ConnectionStatus.CONNECTED){
                    connecting_animation_.stop();
                    reconnecting_animation_.stop();
                    login_button_.setDisable(false);
                    server_ip_input_.setEditable(true);
                    connection_status_.setText("已链接服务器");
                    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                    pause.setOnFinished(_ -> Navigator.navigateTo("fxml/Login.fxml", Navigator.AnimationType.JACK_IN_THE_BOX));
                    pause.playFromStart();
                }
                else if(t1 == AppRepository.ConnectionStatus.DISCONNECTED)
                {
                    reconnecting_animation_.stop();
                    connecting_animation_.stop();
                    connection_status_.setText("服务器链接已断开，正在重试 ");
                }
                else if(t1 == AppRepository.ConnectionStatus.FAILED)
                {
                    reconnecting_animation_.stop();
                    connecting_animation_.stop();
                    connection_status_.setText("服务器链接失败，请检查网络，或者检查IP地址是否正确");
                    login_button_.setDisable(false);
                    server_ip_input_.setEditable(true);
                }
                else if(t1 == AppRepository.ConnectionStatus.RECONNECTING)
                {
                    login_button_.setDisable(true);
                    server_ip_input_.setEditable(false);
                    connecting_animation_.stop();
                    reconnecting_animation_.playFromStart();
                }

            }));
         });


    }


}
