package cc.nekocc.cyanchatroom.features.setting;

import atlantafx.base.controls.PasswordTextField;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    public SettingPage() {}
    public void initialize(URL url, ResourceBundle resource_bundle) {
        setupEvent();
        refreshKey();
        setupStyler();



    }
    private void setupEvent() {
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
