package cc.nekocc.cyanchatroom.features.setting;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingPage implements Initializable {

    @FXML
    public AnchorPane root_pane_;

    public SettingPage() {}
    public void initialize(URL url, ResourceBundle resource_bundle) {}
    public AnchorPane getRootPane() {
        return root_pane_;
    }
}
