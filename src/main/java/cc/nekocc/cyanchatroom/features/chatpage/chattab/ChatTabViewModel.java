package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowViewModel;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import cc.nekocc.cyanchatroom.model.factories.StatusFactory;
import javafx.application.Platform;
import javafx.beans.property.*;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ChatTabViewModel
{
    private final ConversationType conversation_type_;
    private final UUID opposite_user_id_;
    private final StringProperty opposite_user_name_ = new SimpleStringProperty("加载中...");
    private final ObjectProperty<Status> opposite_status_ = new SimpleObjectProperty<>(Status.OFFLINE);
    private final ChatWindowViewModel chat_window_view_model_;
    private final BiConsumer<String, Status> on_data_ready_callback_;

    public ChatTabViewModel(UUID opposite_user_id, ConversationType type, BiConsumer<String, Status> on_data_ready_callback)
    {
        this.opposite_user_id_ = opposite_user_id;
        this.conversation_type_ = type;
        this.chat_window_view_model_ = new ChatWindowViewModel(opposite_user_id);
        this.on_data_ready_callback_ = on_data_ready_callback;
        loadDetails();
    }

    private void loadDetails()
    {
        AppRepository.getInstance().getUserDetails(opposite_user_id_).thenAccept(response ->
        {
            Platform.runLater(() ->
            {
                if (response != null && response.getPayload().request_status())
                {
                    String nickname = response.getPayload().nick_name();
                    Status status = StatusFactory.fromUser(response.getPayload());

                    opposite_user_name_.set(nickname);
                    opposite_status_.set(status);
                    chat_window_view_model_.setOppositeUsername(nickname);

                    if (on_data_ready_callback_ != null)
                    {
                        on_data_ready_callback_.accept(nickname, status);
                    }
                } else
                {
                    opposite_user_name_.set("未知用户");
                }
            });
        });
    }

    public String getOppositeUserName()
    {
        return opposite_user_name_.get();
    }

    public StringProperty oppositeUserNameProperty()
    {
        return opposite_user_name_;
    }

    public ObjectProperty<Status> oppositeStatusProperty()
    {
        return opposite_status_;
    }

    public ChatWindowViewModel getChatWindowViewModel()
    {
        return chat_window_view_model_;
    }

    public UUID getOppositeUserId()
    {
        return opposite_user_id_;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChatTabViewModel that = (ChatTabViewModel) o;
        return Objects.equals(opposite_user_id_, that.opposite_user_id_);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(opposite_user_id_);
    }

    public ConversationType getConversationType()
    {
        return conversation_type_;
    }
}