package cc.nekocc.cyanchatroom.features.setting;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

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

    public SettingPage() {}
    public void initialize(URL url, ResourceBundle resource_bundle) {
        setupEvent();
        refreshKey();
        setupStyler();



    }
    private void setupEvent() {
        clear_chat_data_.setOnAction(_->{
            Alert alert = ViewTool.showAlert(Alert.AlertType.WARNING, "警告：该过程不可逆", "确定要清除所有聊天数据吗？\n该过程不可逆",false);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    AppRepository.getInstance().deleteAllConversations();
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
        refresh_key_button_.setOnAction(_ -> {
            refreshKey();
        });
    }
    private void setupStyler() {

    }
    private void refreshKey(){
        AppRepository.getInstance().fetchKeys(AppRepository.getInstance().currentUserProperty().get().getId()).thenAccept(response -> {
                situation_label_.setText(( response.getPayload().succeed() ? "已获取到 ！" : "获取失败"));

        });
    }


    public AnchorPane getRootPane() {
        return root_pane_;
    }
}
