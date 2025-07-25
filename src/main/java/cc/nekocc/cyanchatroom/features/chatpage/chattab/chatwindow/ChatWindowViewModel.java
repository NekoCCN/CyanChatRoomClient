package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.Message;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.UUID;

public class ChatWindowViewModel
{
    private final UUID opposite_id_;
    private final StringProperty opposite_username_ = new SimpleStringProperty();
    private ObservableList<Message> messages_ = FXCollections.observableArrayList();
    private final StringProperty message_input_text_ = new SimpleStringProperty("");

    public ChatWindowViewModel(UUID oppositeId)
    {
        this.opposite_id_ = oppositeId;
        // loadMessageHistory();
        this.messages_ = AppRepository.getInstance().getObservableMessagesForConversation(opposite_id_);
    }

    /**
     * 遗留测试方法
     */
    private void loadMessageHistory()
    {
        var history = AppRepository.getInstance().getAllMessagesForConversation(
                AppRepository.getInstance().currentUserProperty().get().getId(),
                opposite_id_
        );
        Platform.runLater(() -> messages_.setAll(history));
    }

    public void sendMessage()
    {
        String text = message_input_text_.get();
        if (text == null || text.trim().isEmpty())
        {
            return;
        }

        AppRepository.getInstance().sendMessage("USER", opposite_id_, "TEXT", false, text.trim()).exceptionally(e ->
        {
            Platform.runLater(() ->
            {
                ViewTool.showAlert(
                        javafx.scene.control.Alert.AlertType.ERROR,
                        "消息发送失败",
                        "无法发送消息: " + e.getMessage());
            }
            );

            return null;
        });

        message_input_text_.set("");
    }

    public void sendE2EEMessage()
    {
        String text = message_input_text_.get();
        if (text == null || text.trim().isEmpty())
        {
            return;
        }

        AppRepository.getInstance().sendEncryptedTextMessage(opposite_id_, text.trim()).thenAccept(future -> {
           future.exceptionally(e ->
           {
               Platform.runLater(() -> ViewTool.showAlert(
                       javafx.scene.control.Alert.AlertType.ERROR,
                       "E2EE 失败",
                       "E2EE 消息发送失败: " + e.getMessage())
               );

               return null;
           });
        });
        message_input_text_.set("");
    }

    public void sendMessage(ConversationType type)
    {
        String text = message_input_text_.get();
        if (text == null || text.trim().isEmpty())
        {
            return;
        }

        String recipientType = type.name();

        AppRepository.getInstance().sendMessage(recipientType, opposite_id_, "TEXT", false, text.trim()).exceptionally(e ->
        {
            Platform.runLater(() ->
                    {
                        ViewTool.showAlert(
                                javafx.scene.control.Alert.AlertType.ERROR,
                                "消息发送失败",
                                "无法发送消息: " + e.getMessage());
                    }
            );
            return null;
        });

        message_input_text_.set("");
    }

    public void sendFile(ConversationType type)
    {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("选择要发送的文件");
        File selected_file = file_chooser.showOpenDialog(null);

        if (selected_file != null)
        {
            String recipientType = type.name();
            AppRepository.getInstance().uploadFile(selected_file, 24)
                    .thenAccept(id ->
                    {
                        String content = selected_file.getName() + "|" + id;
                        AppRepository.getInstance().sendMessage(recipientType,
                                opposite_id_, "FILE", false, content);
                    })
                    .exceptionally(e ->
                    {
                        Platform.runLater(() -> ViewTool.showAlert(
                                javafx.scene.control.Alert.AlertType.ERROR,
                                "文件上传失败",
                                "无法上传文件: " + e.getMessage())
                        );
                        return null;
                    });
        }
    }

    public void receiveMessage(Message message)
    {
        // Platform.runLater(() -> messages_.add(message));
    }

    public ObservableList<Message> getMessages()
    {
        return messages_;
    }

    public StringProperty messageInputTextProperty()
    {
        return message_input_text_;
    }

    public StringProperty oppositeUsernameProperty()
    {
        return opposite_username_;
    }

    public void setOppositeUsername(String name)
    {
        this.opposite_username_.set(name);
    }

    public UUID getOppositeID()
    {
        return opposite_id_;
    }
}