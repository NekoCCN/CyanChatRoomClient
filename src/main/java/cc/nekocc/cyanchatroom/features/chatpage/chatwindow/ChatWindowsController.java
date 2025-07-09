package cc.nekocc.cyanchatroom.features.chatpage.chatwindow;

import cc.nekocc.cyanchatroom.Navigator;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatWindowsController implements Initializable {

    @FXML
    private ImageView personal_icon_;
    @FXML
    private  AnchorPane root_pane_;


    public ChatWindowsController() {
         System.out.println("ChatWindowsController created");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupUI();
        System.out.println("ChatWindowsController initialized");
    }


    public void setupUI() {
        personal_icon_.setFitHeight(45);
        personal_icon_.setFitWidth(45);
        personal_icon_.setPreserveRatio(true);
    }


    public AnchorPane getRoot_pane_() {
        return root_pane_;
    }


}
