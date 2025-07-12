package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.model.dto.MessageType;
import cc.nekocc.cyanchatroom.model.dto.request.*;
import cc.nekocc.cyanchatroom.model.dto.response.*;
import cc.nekocc.cyanchatroom.model.entity.*;
import cc.nekocc.cyanchatroom.model.service.HttpService;
import cc.nekocc.cyanchatroom.model.service.NetworkService;
import cc.nekocc.cyanchatroom.model.util.JsonUtil;
import cc.nekocc.cyanchatroom.protocol.ProtocolMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用程序级别的存储库，负责管理应用状态、网络连接和数据交互。
 * TODO: 把这个构思的巨型类拆分为多个模块 让这个类型成为一个注册表
 * TODO: 极度欠缺测试
 * TODO: 应该可以利用反射书写一个类型书面值从而使得类型安全性更高一点
 */
public class AppRepository
{
    public static class ResponseFuture<R>
    {
        public final CompletableFuture<ProtocolMessage<R>> future;
        public final Class<R> responseClass;

        public ResponseFuture(CompletableFuture<ProtocolMessage<R>> future, Class<R> responseClass)
        {
            this.future = future;
            this.responseClass = responseClass;
        }
    }

    private static final AppRepository INSTANCE = new AppRepository();

    private final NetworkService network_service_;
    private final HttpService http_service_;
    private final LocalPersistenceService persistence_service_;

    private final Map<UUID, ResponseFuture> response_futures_ = new ConcurrentHashMap<>();

    public enum ConnectionStatus
    {DISCONNECTED, CONNECTING, CONNECTED, FAILED, RECONNECTING}

    private final ReadOnlyObjectWrapper<ConnectionStatus> connection_status_ = new ReadOnlyObjectWrapper<>(ConnectionStatus.DISCONNECTED);
    private final ReadOnlyObjectWrapper<User> current_user_ = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyStringWrapper last_error_message_ = new ReadOnlyStringWrapper();
    private final ObservableMap<UUID, ObservableList<Message>> conversation_messages_ = FXCollections.observableMap(new ConcurrentHashMap<>());
    private final ObservableList<Conversation> conversations_ = FXCollections.observableArrayList();

    private AppRepository()
    {
        this.http_service_ = new HttpService();
        this.persistence_service_ = new LocalPersistenceService();
        this.network_service_ = new NetworkService(this::onMessageReceived, this::onConnectionOpened, this::onConnectionClosed, this::onReconnecting);
    }

    public static AppRepository getInstance()
    {
        return INSTANCE;
    }

    public void connectToServer(String server_address)
    {
        if (connection_status_.get() == ConnectionStatus.CONNECTING || connection_status_.get() == ConnectionStatus.CONNECTED)
            return;
        connection_status_.set(ConnectionStatus.CONNECTING);
        String ws_url = "ws://" + server_address + "/ws";
        String http_url = "http://" + server_address;
        http_service_.setBaseUrl(http_url);
        network_service_.connect(ws_url);
    }

    public void disconnect()
    {
        network_service_.disconnect();
    }

    public CompletableFuture<ProtocolMessage<UserOperatorResponse>> register(String user_name, String password, String nick_name)
    {
        UUID client_request_id = UUID.randomUUID();
        RegisterRequest payload = new RegisterRequest(client_request_id, user_name, password, nick_name);
        return sendRequestWithFuture(MessageType.REGISTER_REQUEST, payload, client_request_id, UserOperatorResponse.class);
    }

    public CompletableFuture<ProtocolMessage<UserOperatorResponse>> login(String user_name, String password)
    {
        UUID client_request_id = UUID.randomUUID();
        LoginRequest payload = new LoginRequest(client_request_id, user_name, password);

        return sendRequestWithFuture(MessageType.LOGIN_REQUEST, payload, client_request_id, UserOperatorResponse.class);
    }

    public void setCurrentUser(User user)
    {
        if (user == null)
        {
            current_user_.set(null);
            return;
        }
        current_user_.set(user);
    }

    public void sendMessage(String recipient_type, UUID recipient_id, String content_type, boolean is_encrypted, String content)
    {
        User current_user = current_user_.get();
        if (current_user == null)
        {
            last_error_message_.set("错误: 发送消息前必须登录。");
            return;
        }
        Message local_message = new Message(recipient_id, current_user.getId(), true, content_type, content);
        persistence_service_.saveMessage(local_message);
        getMessagesForConversation(recipient_id).add(local_message);
        sendRequest(MessageType.CHAT_MESSAGE, new ChatMessageRequest(UUID.randomUUID(), recipient_type,
                recipient_id, content_type, is_encrypted, content));
    }

    public CompletableFuture<ProtocolMessage<GroupResponse>> createGroup(String group_name, List<UUID> member_ids)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.CREATE_GROUP_REQUEST,
                new CreateGroupRequest(client_request_id, group_name, member_ids), client_request_id, GroupResponse.class);
    }

    public CompletableFuture<String> uploadFile(File file, Integer expires_in_hours)
    {
        CompletableFuture<String> final_file_id_future = new CompletableFuture<>();
        UUID client_request_id = UUID.randomUUID();
        FileUploadRequest request_payload = new FileUploadRequest(file.getName(), file.length(),
                expires_in_hours, client_request_id.toString());

        sendRequestWithFuture(MessageType.REQUEST_FILE_UPLOAD, request_payload, client_request_id, FileUploadResponse.class)
                .thenAccept(responseMsg ->
                {
                    FileUploadResponse upload_response = (FileUploadResponse) responseMsg.getPayload();
                    http_service_.uploadFile(upload_response.upload_url(), file)
                            .thenAccept(success -> Platform.runLater(() ->
                            {
                                if (success)
                                {
                                    final_file_id_future.complete(String.valueOf(upload_response.file_id()));
                                } else
                                {
                                    final_file_id_future.completeExceptionally(new RuntimeException("HTTP文件上传失败"));
                                }
                            }));
                })
                .exceptionally(ex ->
                {
                    final_file_id_future.completeExceptionally(ex);
                    return null;
                });

        return final_file_id_future;
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> updateProfile(String nick_name, String signature,
                                                                            String avatar_file_id, UserStatus status)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.UPDATE_PROFILE_REQUEST,
                new UpdateProfileRequest(client_request_id, nick_name, signature, avatar_file_id, status),
                client_request_id, StatusResponse.class);
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> changeUsername(String current_password, String new_user_name)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.CHANGE_USERNAME_REQUEST,
                new ChangeUsernameRequest(client_request_id, new_user_name, current_password), client_request_id,
                StatusResponse.class);
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> changePassword(String current_password, String new_password)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.CHANGE_PASSWORD_REQUEST,
                new ChangePasswordRequest(client_request_id, current_password, new_password), client_request_id,
                StatusResponse.class);
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> publishKeys(JsonObject key_bundle)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.PUBLISH_KEYS_REQUEST,
                new PublishKeysRequest(client_request_id, key_bundle), client_request_id, StatusResponse.class);
    }

    public CompletableFuture<ProtocolMessage<FetchKeysResponse>> fetchKeys(UUID user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.FETCH_KEYS_REQUEST,
                new FetchKeysRequest(client_request_id, user_id), client_request_id, FetchKeysResponse.class);
    }

    public CompletableFuture<ProtocolMessage<GetUserDetailsResponse>> getUserDetails(UUID user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.GET_USER_DETAILS_REQUEST,
                new GetUserDetailsRequest(client_request_id, user_id), client_request_id, GetUserDetailsResponse.class);
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> joinGroup(UUID group_id, String request_message)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.JOIN_GROUP_REQUEST,
                new JoinGroupRequest(client_request_id, group_id, request_message), client_request_id,
                StatusResponse.class);
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> handleJoinRequest(UUID group_id, UUID request_id,
                                                                                boolean approved)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.HANDLE_JOIN_REQUEST,
                new HandleJoinRequest(client_request_id, group_id, request_id, approved), client_request_id,
                StatusResponse.class);
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> leaveGroup(UUID group_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.LEAVE_GROUP_REQUEST,
                new LeaveGroupRequest(client_request_id, group_id), client_request_id, StatusResponse.class);
    }

    public CompletableFuture<ProtocolMessage<StatusResponse>> removeMember(UUID group_id, UUID target_user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.REMOVE_MEMBER_REQUEST,
                new RemoveMemberRequest(client_request_id, group_id, target_user_id), client_request_id,
                StatusResponse.class);
    }

    private <T> void sendRequest(String type, T payload)
    {
        if (!checkConnection(type)) return;
        ProtocolMessage<T> request = new ProtocolMessage<>(type, payload);
        network_service_.sendMessage(JsonUtil.serialize(request));
    }

    private <T, R> CompletableFuture<ProtocolMessage<R>> sendRequestWithFuture(
            String type, T payload, UUID client_request_id, Class<R> responseClass)
    {
        if (!checkConnection(type))
            return CompletableFuture.failedFuture(new IllegalStateException("尚未连接到服务器。"));

        CompletableFuture<ProtocolMessage<R>> future = new CompletableFuture<>();
        response_futures_.put(client_request_id, new ResponseFuture<>(future, responseClass));

        ProtocolMessage<T> request = new ProtocolMessage<>(type, payload);
        network_service_.sendMessage(JsonUtil.serialize(request));
        return future;
    }

    private boolean checkConnection(String type)
    {
        if (connection_status_.get() != ConnectionStatus.CONNECTED)
        {
            if (!("LOGIN_REQUEST".equals(type) || "REGISTER_REQUEST".equals(type)))
            {
                last_error_message_.set("错误: 尚未连接到服务器。");
                return false;
            }
        }
        return true;
    }

    private void onConnectionOpened()
    {
        Platform.runLater(() -> connection_status_.set(ConnectionStatus.CONNECTED));
    }

    private void onConnectionClosed()
    {
        Platform.runLater(() ->
        {
            if (connection_status_.get() != ConnectionStatus.RECONNECTING)
            {
                connection_status_.set(ConnectionStatus.DISCONNECTED);
                current_user_.set(null);
            }
        });
    }

    private void onReconnecting()
    {
        Platform.runLater(() -> connection_status_.set(ConnectionStatus.RECONNECTING));
    }

    private void onMessageReceived(String json_message) {
        try {
            // --- This part is safe to run on a background thread ---
            JsonObject json_object = JsonParser.parseString(json_message).getAsJsonObject();
            String type = json_object.get("type").getAsString();
            JsonObject payload_object = json_object.getAsJsonObject("payload");

            Platform.runLater(() ->
            {
                try
                {
                    if ("BROADCAST_MESSAGE".equals(type)) {
                        handleBroadcastMessage(json_message);
                        return;
                    }

                    if (payload_object != null && payload_object.has("client_request_id"))
                    {
                        UUID client_request_id = UUID.fromString(payload_object.get("client_request_id").getAsString());
                        ResponseFuture<?> future = response_futures_.remove(client_request_id);
                        if (future != null)
                        {
                            resolveFuture(future.future, type, json_message);
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    last_error_message_.set("Error processing server message on UI thread: " + e.getMessage());
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();
            Platform.runLater(() -> last_error_message_.set("Error parsing server message: " + e.getMessage()));
        }
    }

    private void resolveFuture(CompletableFuture future, String type, String json_message)
    {
        try
        {
            switch (type)
            {
                case MessageType.REGISTER_RESPONSE:
                case "LOGIN_SUCCESS":
                case "LOGIN_FAILED":
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, UserOperatorResponse.class));
                    break;
                case MessageType.CREATE_GROUP_RESPONSE:
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, GroupResponse.class));
                    break;
                case "RESPONSE_FILE_UPLOAD":
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, FileUploadResponse.class));
                    break;
                case "FETCH_KEYS_RESPONSE":
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, FetchKeysResponse.class));
                    break;
                case "GET_USER_DETAILS_SUCCESS":
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, GetUserDetailsResponse.class));
                    break;
                case "ERROR_RESPONSE":
                    ErrorResponse error_payload = JsonUtil.deserializeProtocolMessage(json_message, ErrorResponse.class).getPayload();
                    future.completeExceptionally(new RuntimeException(error_payload.error()));
                    break;
                default:
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, StatusResponse.class));
                    break;
            }
        } catch (Exception e)
        {
            future.completeExceptionally(e);
        }
    }

    private void handleBroadcastMessage(String json_message)
    {
        User current_user = current_user_.get();
        if (current_user == null) return;

        Type type = new TypeToken<ProtocolMessage<Map<String, Object>>>()
        {
        }.getType();
        ProtocolMessage<Map<String, Object>> msg = JsonUtil.getGson().fromJson(json_message, type);
        Map<String, Object> payload = msg.getPayload();

        UUID sender_id = UUID.fromString((String) payload.get("sender_id"));
        boolean is_outgoing = sender_id.equals(current_user.getId());

        String recipient_type = (String) payload.get("recipient_type");
        UUID conversation_id;
        if ("USER".equals(recipient_type))
        {
            UUID recipient_id = UUID.fromString((String) payload.get("recipient_id"));
            conversation_id = is_outgoing ? recipient_id : sender_id;
        } else
        {
            conversation_id = UUID.fromString((String) payload.get("group_id"));
        }

        String content_type = (String) payload.get("content_type");
        String content = (String) payload.get("content");

        Message local_message = new Message(conversation_id, sender_id, is_outgoing, content_type, content);

        persistence_service_.saveMessage(local_message);
        getMessagesForConversation(conversation_id).add(local_message);
    }

    public ReadOnlyObjectProperty<ConnectionStatus> connectionStatusProperty()
    {
        return connection_status_.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<User> currentUserProperty()
    {
        return current_user_.getReadOnlyProperty();
    }

    public ObservableList<Conversation> getConversations()
    {
        return FXCollections.unmodifiableObservableList(conversations_);
    }

    public ReadOnlyStringProperty lastErrorMessageProperty()
    {
        return last_error_message_.getReadOnlyProperty();
    }

    public ObservableList<Message> getMessagesForConversation(UUID conversation_id)
    {
        return conversation_messages_.computeIfAbsent(conversation_id, id ->
        {
            List<Message> history = persistence_service_.getMessagesForConversation(id, 50, 0);
            return FXCollections.observableArrayList(history);
        });
    }
}