package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.contact.ContactListController;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import atlantafx.base.theme.Styles;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import java.util.ResourceBundle;
import java.net.URL;


public class ChatPageController implements Initializable {

    @FXML
    public TextArea message_input;
    @FXML
    public ImageView talk_icon_;
    @FXML
    public ImageView contact_icon_;
    @FXML
    public ImageView setting_icon_;
    @FXML
    public AnchorPane scroll_root_pane_;
    @FXML
    public ImageView contact_agreement_icon_;
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
    private AnchorPane chat_windows_pane_;
    @FXML
    private VBox user_list_vbox_;
    @FXML
    private Button enter_button_;

    private final BooleanProperty setting_shown_  = new SimpleBooleanProperty(false);
    private final BooleanProperty contact_agreement_shown_ = new SimpleBooleanProperty(false);
    private Node current_list_node;
    private final DropShadow glow_effect_ = new DropShadow();
    private final ChatPageViewModel view_model_ =  new ChatPageViewModel();
    private ContactListController   contact_list ;


    public ChatPageController(){}
    // 初始化
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        contact_list = (ContactListController)ViewTool.loadFXML("fxml/ContactList.fxml");
        setupAnimation();
        setupStyle();
        synchronizeData();
        setupUI();
        setupEvent();
        setupBind();
        view_model_.loadUserList(user_list_vbox_,chat_windows_pane_);
    }

    // 动画设置
    private void setupAnimation() {
        glow_effect_.setColor(Color.rgb(73,136,240)); // 设置发光颜色
        glow_effect_.setSpread(0.8);       // 控制发光范围
        glow_effect_.setRadius(12);
    }


    public void synchronizeData() {
        view_model_.synchronizeData();
        contact_list.syncContact(view_model_.getUserList());
    }



    // 样式设置
    private void setupStyle() {
        user_background_rectangle_.getStyleClass().addAll(Styles.ROUNDED);
        user_status_.getStyleClass().addAll(Styles.ROUNDED,Styles.ACCENT);
        username_title_label_.getStyleClass().addAll(Styles.TEXT_MUTED);
        username_label_.getStyleClass().addAll(Styles.TITLE_1);
        list_scrollPane_.getStyleClass().addAll(Styles.CENTER_PILL);
        user_tool_pane_.getStyleClass().addAll(Styles.ROUNDED);
        enter_button_.getStyleClass().addAll(Styles.ROUNDED,Styles.ACCENT);



        talk_icon_.setEffect(glow_effect_);
    }




    private void setupUI(){
        user_status_.getItems().addAll(Status.ONLINE, Status.BUSY, Status.DO_NOT_DISTURB, Status.INVISIBLE,Status.AWAY,Status.OFFLINE);
        user_status_.setValue(Status.ONLINE);
        current_list_node = talk_icon_;


    }

    // 事件设置
    private void setupEvent()
    {
        initIconEffect(talk_icon_);
        initIconEffect(contact_icon_);
        talk_icon_.setOnMouseClicked(_ ->{
            if(current_list_node !=  talk_icon_)
            {
                view_model_.loadLastWindow();
                current_list_node.setEffect(null);
                current_list_node = talk_icon_;
                talk_icon_.setEffect(glow_effect_);
                scroll_root_pane_.getChildren().clear();
                scroll_root_pane_.getChildren().add(list_scrollPane_);
            }
        });
        contact_icon_.setOnMouseClicked(_ ->{
            if(current_list_node !=  contact_icon_)
            {
                view_model_.setCurrentChatWindowNULL();
                current_list_node.setEffect(null);
                current_list_node = contact_icon_;
                contact_icon_.setEffect(glow_effect_);
                scroll_root_pane_.getChildren().clear();
                scroll_root_pane_.getChildren().add(contact_list.getRootPane());

            }
        });
        contact_agreement_icon_.setOnMouseEntered(_ -> contact_agreement_icon_.setEffect(new Glow(0.6)));
        contact_agreement_icon_.setOnMouseExited(_ -> contact_agreement_icon_.setEffect(null));
        contact_agreement_icon_.setOnMouseClicked(_ ->{
            if(!contact_agreement_shown_.get())
            {
                contact_agreement_shown_.set(true);
                view_model_.showContactAgree();
            }
        });
        setting_icon_.setOnMouseEntered(_ -> setting_icon_.setEffect(new Glow(0.6)));
        setting_icon_.setOnMouseExited(_ -> setting_icon_.setEffect(null));
        setting_icon_.setOnMouseClicked(_ ->{
            if(!setting_shown_.get())
            {
                setting_shown_.set(true);
                view_model_.showSetting();
            }
        });

        enter_button_.setOnAction(_ ->{
            if(!view_model_.isCurrentChatWindowNULL() && !message_input.getText().isEmpty()){
                view_model_.sendMessageFromMe(message_input.getText());
                message_input.clear();
            }
        });


    }

    // 图标悬停效果初始化
    private void initIconEffect(ImageView icon) {
        icon.setOnMouseEntered(_ ->{
            if(current_list_node !=  icon)
                icon.setEffect(new Glow(0.6));
        });
        icon.setOnMouseExited(_ ->{
            if(current_list_node !=  icon)
                icon.setEffect(null);
        });
    }

    private void setupBind(){
        setting_shown_.bindBidirectional(view_model_.getSettingShown());
        contact_agreement_shown_.bindBidirectional(view_model_.getContactAgreeShown());

    }





}
