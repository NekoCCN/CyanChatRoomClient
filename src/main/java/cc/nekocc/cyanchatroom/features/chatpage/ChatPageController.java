package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.fxml.FXML;
import atlantafx.base.theme.Styles;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;


import java.util.ResourceBundle;
import java.net.URL;


public class ChatPageController implements Initializable {

    @FXML
    public TextArea message_input;
    @FXML
    private Label username_title_label_;   // 用户名
    @FXML
    private Label username_label_;
    @FXML
    private ScrollPane list_scrollPane_;
    @FXML
    private ChoiceBox<Status> user_status_;
    @FXML
    private Rectangle user_background_rectangle_;
    @FXML
    private HBox user_tool_pane_;
    @FXML
    private Pane chat_window_pane_;
    @FXML
    private VBox user_list_vbox_;
    @FXML
    private Button enter_button_;


    private final ChatPageViewModel view_model_ =  new ChatPageViewModel();


    public ChatPageController(){}
    // 初始化
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        setupStyle();
        synchronizeData();
        setupUI();
        view_model_.loadUserList(user_list_vbox_);


    }



    // 样式设置
    private void setupStyle() {
        user_background_rectangle_.getStyleClass().addAll(Styles.ROUNDED);
        user_status_.getStyleClass().addAll(Styles.ROUNDED,Styles.ACCENT);
        username_title_label_.getStyleClass().addAll(Styles.TEXT_MUTED);
        username_label_.getStyleClass().addAll(Styles.TITLE_1);
        list_scrollPane_.getStyleClass().addAll(Styles.CENTER_PILL);
        user_tool_pane_.getStyleClass().addAll(Styles.ROUNDED);
    }

    // 数据同步
    private void synchronizeData()
    {
        //username_title_label_.textProperty().bindBidirectional(view_model_.getUsername_title_property());

    }

    private void setupUI(){
        user_status_.getItems().addAll(Status.ONLINE, Status.BUSY, Status.DO_NOT_DISTURB, Status.INVISIBLE,Status.AWAY,Status.OFFLINE);
        user_status_.setValue(Status.ONLINE);
        enter_button_.getStyleClass().addAll(Styles.ROUNDED,Styles.ACCENT);

    }




}
