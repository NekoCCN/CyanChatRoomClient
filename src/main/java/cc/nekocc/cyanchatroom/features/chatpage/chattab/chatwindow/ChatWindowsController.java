package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.Message;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ChatWindowsController implements Initializable
{

    @FXML
    private AnchorPane root_pane_;
    @FXML
    private ScrollPane scroll_pane_;
    @FXML
    private VBox container_pane_;
    @FXML
    private ImageView personal_icon_;
    @FXML
    private Label username_label_;

    private ChatWindowViewModel view_model_;
    private ListChangeListener<Message> message_listener_;
    private final DateTimeFormatter time_formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        container_pane_.heightProperty().addListener((obs, oldVal, newVal) -> scroll_pane_.setVvalue(1.0));
    }

    public void setViewModel(ChatWindowViewModel newViewModel)
    {
        if (this.view_model_ != null && this.message_listener_ != null)
        {
            this.view_model_.getMessages().removeListener(message_listener_);
        }

        this.view_model_ = newViewModel;
        username_label_.textProperty().bind(view_model_.oppositeUsernameProperty());

        container_pane_.getChildren().clear();
        view_model_.getMessages().forEach(this::addMessageNode);

        this.message_listener_ = c ->
        {
            while (c.next())
            {

                if (c.wasAdded())
                {
                    c.getAddedSubList().forEach(this::addMessageNode);
                }
            }
        };
        view_model_.getMessages().addListener(this.message_listener_);
    }

    private void addMessageNode(Message message)
    {


        boolean is_outgoing = message.isOutgoing();



        VBox message_container = new VBox();


        HBox message_box = new HBox(10);



        Label message_label = new Label(message.getVal());
        message_label.setWrapText(true);


        if (is_outgoing)
        {
            message_label.setStyle("-fx-padding: 8; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 12 12 0 12;");
        } else
        {
            message_label.setStyle("-fx-padding: 8; -fx-background-color: #FFFFFF; -fx-text-fill: black; -fx-background-radius: 12 12 12 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 1, 2);");
        }



        String sender_nickname = is_outgoing
                ? AppRepository.getInstance().currentUserProperty().get().getNickname()
                : view_model_.oppositeUsernameProperty().get();

        Text username_text = new Text(sender_nickname);
        username_text.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));

        Text time_text = new Text(OffsetDateTime.parse(message.getTime()).format(time_formatter_));
        time_text.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 10));

        VBox message_bubble = new VBox(username_text, message_label, time_text);


        StackPane avatar = ViewTool.getDefaultAvatar(sender_nickname);


        if (is_outgoing)
        {
            message_box.getChildren().addAll(message_bubble, avatar);
        } else
        {
            message_box.getChildren().addAll(avatar, message_bubble);
        }

        message_container.getChildren().add(message_box);
        message_label.setContextMenu(setupMessageMenu(message_label, message_container, is_outgoing));



        message_container.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_container.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_container.setMaxWidth(Double.MAX_VALUE);
        message_container.setMaxHeight(Double.MAX_VALUE);

        message_container.setAlignment(is_outgoing ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        message_box.setAlignment(is_outgoing ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        message_box.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_box.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_box.setMaxWidth(Double.MAX_VALUE);
        message_box.setMaxHeight(Double.MAX_VALUE);

        message_label.maxWidthProperty().bind(container_pane_.widthProperty().multiply(0.6));
        message_label.setMaxHeight(Double.MAX_VALUE);
        message_label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_label.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 18));

        message_bubble.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_bubble.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_bubble.setMaxWidth(Double.MAX_VALUE);
        message_bubble.setMaxHeight(Double.MAX_VALUE);
        message_bubble.setSpacing(2);
        message_bubble.setAlignment(is_outgoing ? Pos.TOP_RIGHT : Pos.TOP_LEFT);


        Platform.runLater(() ->{



            container_pane_.getChildren().add(message_container);});
    }

    private ContextMenu setupMessageMenu(Label label, VBox container, boolean can_withdraw)
    {
        ContextMenu menu = new ContextMenu();
        MenuItem copy_item = new MenuItem("复制");
        copy_item.setOnAction(_ ->
        {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(label.getText());
            clipboard.setContent(content);
        });

        MenuItem delete_item = new MenuItem("删除");
        delete_item.setOnAction(_ -> container_pane_.getChildren().remove(container));

        menu.getItems().addAll(copy_item, new SeparatorMenuItem());
        if (can_withdraw)
        {
            MenuItem withdraw_item = new MenuItem("撤回");
            withdraw_item.setDisable(true);
            menu.getItems().add(withdraw_item);
        }
        menu.getItems().add(delete_item);
        return menu;
    }

    public AnchorPane getRootPane()
    {
        return root_pane_;
    }
}