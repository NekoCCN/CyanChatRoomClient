package cc.nekocc.cyanchatroom.features.chatpage;

import javafx.fxml.FXML;
import atlantafx.base.theme.Styles;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.util.ResourceBundle;
import java.net.URL;


public class ChatPageController implements Initializable {

    @FXML
    public Label username_title_label_;   // 用户名
    @FXML
    private AnchorPane main_pane;
    @FXML
    private ScrollPane list_scrollPane_;
    @FXML
    private ChoiceBox user_status_;


    private final ChatPageViewModel view_model_ =  new ChatPageViewModel();

    public ChatPageController(){}
    // 初始化
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        synchronizeData();
        setupUI();

    }

    // 数据同步
    private void synchronizeData()
    {
        username_title_label_.textProperty().bindBidirectional(view_model_.getUsername_title_property());

    }

    private void setupUI(){
        list_scrollPane_.getStyleClass().addAll(Styles.CENTER_PILL);


    }



}
