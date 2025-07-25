package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.Message;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.stage.DirectoryChooser;
import javafx.scene.image.Image;
import java.io.File;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

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

    private Runnable personal_icon_action;

    private ChatWindowViewModel view_model_;
    private ListChangeListener<Message> message_listener_;
    private final DateTimeFormatter time_formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        container_pane_.heightProperty().addListener((obs, oldVal, newVal) -> scroll_pane_.setVvalue(1.0));

        personal_icon_.setOnMouseClicked(event ->
        {
            if (personal_icon_action != null)
            {
                personal_icon_action.run();
            }
        });
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
        if ("FILE".equals(message.getType()))
        {
            createFileContentNode(message);
        } else
        {
            createTextContentNode(message);
        }

    }
    private void createTextContentNode(Message message)
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

        Text username_text = new Text();
        username_text.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));
        StackPane avatar = new StackPane();

        if (is_outgoing)
        {
            String my_nickname = AppRepository.getInstance().currentUserProperty().get().getNickname();
            username_text.setText(my_nickname);
            avatar.getChildren().setAll(ViewTool.getDefaultAvatar(my_nickname));
        } else
        {
            username_text.setText("...");
            UUID senderId = message.getSenderId();
            AppRepository.getInstance().getNicknameForUser(senderId).thenAccept(nickname ->
            {
                Platform.runLater(() ->
                {
                    username_text.setText(nickname);
                    avatar.getChildren().setAll(ViewTool.getDefaultAvatar(nickname));
                });
            });
        }

        Text time_text = new Text(OffsetDateTime.parse(message.getTime()).format(time_formatter_));
        time_text.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 10));

        VBox message_bubble = new VBox(username_text, message_label, time_text);

        if (is_outgoing)
        {
            message_box.getChildren().addAll(message_bubble, avatar);
        } else
        {
            message_box.getChildren().addAll(avatar, message_bubble);
        }
        message_container.getChildren().add(message_box);
        message_label.setContextMenu(setupMessageMenu(message_label));
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

    private void createFileContentNode(Message message)
    {
        boolean is_outgoing = message.isOutgoing();
        VBox message_container = new VBox();
        HBox message_box = new HBox(10);

        Text username_text = new Text();
        username_text.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));
        StackPane avatar = new StackPane();

        if (is_outgoing)
        {
            String my_nickname = AppRepository.getInstance().currentUserProperty().get().getNickname();
            username_text.setText(my_nickname);
            avatar.getChildren().setAll(ViewTool.getDefaultAvatar(my_nickname));
        } else
        {
            username_text.setText("...");
            UUID senderId = message.getSenderId();
            AppRepository.getInstance().getNicknameForUser(senderId).thenAccept(nickname -> {
                Platform.runLater(() ->
                {
                    username_text.setText(nickname);
                    avatar.getChildren().setAll(ViewTool.getDefaultAvatar(nickname));
                });
            });
        }

        Text time_text = new Text(OffsetDateTime.parse(message.getTime()).format(time_formatter_));
        time_text.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 10));



        String content = message.getVal();
        String[] parts = content.split("\\|");
        String fileName = parts.length > 0 ? parts[0] : "未知文件";
        String fileId = parts.length > 1 ? parts[1] : "";

        ImageView fileIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/file_icon.png"))));
        fileIcon.setFitHeight(40);
        fileIcon.setFitWidth(40);

        Label file_name_label = new Label(fileName);
        file_name_label.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 15));
        file_name_label.setStyle("-fx-text-fill: #333;");

        Button download_button = new Button("下载");
        download_button.setStyle(
                "-fx-background-color: #2196F3;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 4 12;"
        );

        VBox file_info_box = new VBox(file_name_label, download_button);
        file_info_box.setSpacing(6);

        HBox file_box = new HBox(12, fileIcon, file_info_box);
        file_box.setAlignment(Pos.CENTER_LEFT);
        file_box.setStyle(
                "-fx-background-color: #F0F0F0;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 6, 0, 1, 2);"
        );

        download_button.setOnAction(e ->
        {
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("选择保存位置");
            File selectedDirectory = directory_chooser.showDialog(null);
            if (selectedDirectory != null)
            {
                File saveLocation = new File(selectedDirectory, fileName);
                AppRepository.getInstance().downloadFile(fileId, saveLocation)
                        .thenAccept(success -> Platform.runLater(() ->
                        {
                            if (success)
                            {
                                ViewTool.showAlert(Alert.AlertType.INFORMATION, "下载成功", "文件已保存至: " + saveLocation.getAbsolutePath());
                            } else
                            {
                                ViewTool.showAlert(Alert.AlertType.ERROR, "下载失败", "无法下载文件，请检查网络或文件状态。");
                            }
                        }));
            }
        });
        VBox message_bubble = new VBox(username_text, file_box, time_text);

        if (is_outgoing)
        {
            message_box.getChildren().addAll(message_bubble, avatar);
        } else
        {
            message_box.getChildren().addAll(avatar, message_bubble);
        }
        message_container.getChildren().add(message_box);
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
        message_bubble.setPrefHeight(Control.USE_COMPUTED_SIZE);
        message_bubble.setPrefWidth(Control.USE_COMPUTED_SIZE);
        message_bubble.setMaxWidth(Double.MAX_VALUE);
        message_bubble.setMaxHeight(Double.MAX_VALUE);
        message_bubble.setSpacing(2);
        message_bubble.setAlignment(is_outgoing ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        Platform.runLater(() ->{
            container_pane_.getChildren().add(message_container);});





    }

    private ContextMenu setupMessageMenu(Label label)
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
        delete_item.setOnAction(_ ->
        {
            if (label.getParent() != null && label.getParent().getParent() != null && label.getParent().getParent().getParent() != null)
            {
                Node messageContainer = label.getParent().getParent().getParent(); // VBox -> HBox -> VBox
                container_pane_.getChildren().remove(messageContainer);
            }
        });

        menu.getItems().addAll(copy_item, new SeparatorMenuItem(), delete_item);
        return menu;
    }

    public void setPersonalIconAction(Runnable action)
    {
        this.personal_icon_action = action;
    }

    public AnchorPane getRootPane()
    {
        return root_pane_;
    }
}