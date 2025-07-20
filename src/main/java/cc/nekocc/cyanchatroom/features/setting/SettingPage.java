package cc.nekocc.cyanchatroom.features.setting;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingPage implements Initializable {

    @FXML
    public AnchorPane root_pane_;
    public Button set_key_button_;
    public Button refresh_key_button_;
    public Label situation_label_;
    public PasswordTextField old_password_input_;
    public TextField new_password_input_;
    public Label old_password_warning_;
    public Label new_password_warning_;
    public Button password_fresh_;
    public CheckBox black_color_plan_;
    public CheckBox default_color_plan_;
    public CheckBox turn_false_;
    public CheckBox turn_on_;
    public Button clear_chat_data_;
    public TextField new_username_input_;
    public Button new_username_button_;
    public Button new_nickname_button_;
    public TextField new_nickname_input_;
    public TextArea new_userwords_input_;
    public Button new_words_button_;
    public Label new_username_warning_;
    public Label new_nickname_warning_;

    public SettingPage() {}
    public void initialize(URL url, ResourceBundle resource_bundle) {
        setupEvent();
        refreshKey();
        setupStyler();



    }
    private void setupEvent() {
        new_username_input_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if ((newText.matches("^[a-zA-Z0-9]+$") || newText.length()>= 13) || newText.isEmpty()) {
                new_username_warning_.setVisible(false);
                new_username_input_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                return change;
            }
            else{
                new_username_warning_.setVisible(true);
                new_username_input_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return null;  // 拒绝修改
            }
        }));

        new_username_button_.setOnAction(_-> {
            Alert alert = ViewTool.showAlert(Alert.AlertType.WARNING, "警告", "用户名是账号登录的重要部分？\n是否确认修改",false);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response->{
                if(response == ButtonType.YES){
                    Alert password_alert = ViewTool.showAlert(Alert.AlertType.WARNING, "警告:请输入密码", "",false);
                    PasswordTextField password_input = new PasswordTextField();
                    password_input.setPromptText("请输入密码");
                    password_alert.getDialogPane().setContent(password_input);
                    password_alert.getButtonTypes().setAll(ButtonType.YES);
                    password_alert.showAndWait().ifPresent(responses -> {
                        AppRepository.getInstance().changeUsername(password_input.getPassword().trim(),new_username_input_.getText().trim()).thenAccept(responsees->{
                            if(responsees.getPayload().status()) {
                                ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "已修改用户名\n程序即将关闭",false).showAndWait().ifPresent(res->{
                                    Platform.exit();
                                    AppRepository.getInstance().onClose();
                                    System.exit(0);
                                });
                            }
                            else{
                                ViewTool.showAlert(Alert.AlertType.ERROR, "错误", responsees.getPayload().message());
                            }
                        });
                    });
                }
            });

        });


        new_nickname_input_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if ((newText.matches("^[a-zA-Z0-9]+$") && newText.length() <= 12) || newText.isEmpty()) {
                new_nickname_warning_.setVisible(false);
                new_nickname_input_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                return change;
            }
            else{
                new_nickname_warning_.setVisible(true);
                new_nickname_input_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return null;  // 拒绝修改
            }
        }));




        turn_false_.setOnAction(_->{
            turn_on_.setSelected(false);
            turn_false_.setDisable(true);
            turn_on_.setDisable(false);
        });
        turn_on_.setOnAction(_->{
            turn_false_.setSelected(false);
            turn_on_.setDisable(true);
            turn_false_.setDisable(false);
        });
        black_color_plan_.setOnAction(_->{
            default_color_plan_.setSelected(false);
        });
        default_color_plan_.setOnAction(_->{
            black_color_plan_.setSelected(false);
        });
        clear_chat_data_.setOnAction(_->{
            Alert alert = ViewTool.showAlert(Alert.AlertType.WARNING, "警告：该过程不可逆", "确定要清除所有聊天数据吗？\n该过程不可逆",false);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    AppRepository.getInstance().deleteAllConversations();
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "已清除所有聊天数据\n重启生效");
                }
            });

        });

        new_password_input_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if (newText.matches("^[a-zA-Z0-9!.,@#$%^&*]+$") || newText.isEmpty()) {
                new_password_warning_.setVisible(false);
                new_password_input_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                return change;
            }
            else{
                new_password_warning_.setVisible(true);
                new_password_input_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return null;  // 拒绝修改
            }
        }));
        password_fresh_.setOnAction(_->{
           AppRepository.getInstance().changePassword(old_password_input_.getPassword(),new_password_input_.getText()).thenAccept(response -> {
               if(response.getPayload().status())
                   ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "修改密码成功");
               else
                   ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "修改密码失败：" +response.getPayload().message());
           });
        });




        set_key_button_.setOnAction(_ -> {
            AppRepository.getInstance().generateAndRegisterKeys().thenAccept(response -> {
                if(response.getPayload().status()){
                    ViewTool.showAlert(Alert.AlertType.INFORMATION, "成功", "已生成并注册密钥");
                    refreshKey();
                }
                else{
                    ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "发布密钥失败：" +response.getPayload().message());
                }
            }).exceptionally(e ->{
                ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "生成错误: " + e.getMessage());
                return null;
            });

        });
        PauseTransition refresh_delay_ = new PauseTransition(Duration.millis(500));
        refresh_delay_.setOnFinished(event -> {
            refreshKey();
            refresh_key_button_.setDisable(false);
        });
        refresh_key_button_.setOnAction(_ -> {
            situation_label_.setText("正在获取密钥...");
            refresh_key_button_.setDisable(true);
            refresh_delay_.play();
        });
    }
    private void setupStyler() {


    }
    private void refreshKey(){
        AppRepository.getInstance().fetchKeys(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response -> {
                situation_label_.setText(( response.getPayload().succeed() ? "已获取到 ！" : "获取失败"));

        });
    }


}
