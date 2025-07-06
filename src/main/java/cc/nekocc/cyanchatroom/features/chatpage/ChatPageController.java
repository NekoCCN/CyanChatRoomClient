package cc.nekocc.cyanchatroom.features.chatpage;

import javafx.fxml.FXML;
import atlantafx.base.theme.Styles;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.util.ResourceBundle;
import java.net.URL;


public class ChatPageController implements Initializable {
    @FXML
    private AnchorPane main_pane;
    @FXML
    private ScrollPane list_scrollPane_;

    public ChatPageController(){}

    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        setupUI();

    }

    private void setupUI(){
        list_scrollPane_.getStyleClass().addAll(Styles.CENTER_PILL);


    }



}
