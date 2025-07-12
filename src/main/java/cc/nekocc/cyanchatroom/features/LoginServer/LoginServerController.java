package cc.nekocc.cyanchatroom.features.LoginServer;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.Navigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginServerController implements Initializable {

    @FXML
    public TextField server_ip_input_;
    @FXML
    public Button login_button_;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        setupStyle();
        setupEvent();
    }

    private void setupStyle()
    {
        server_ip_input_.getStyleClass().addAll(Styles.TEXT_NORMAL);
        login_button_.getStyleClass().addAll(Styles.ACCENT);
    }
    private void setupEvent(){
        login_button_.setOnAction(_ ->{
            Navigator.navigateTo("fxml/Login.fxml", Navigator.AnimationType.JACK_IN_THE_BOX);
        });


    }


}
