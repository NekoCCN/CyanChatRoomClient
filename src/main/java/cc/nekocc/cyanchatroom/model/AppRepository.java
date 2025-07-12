package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.model.callback.ResponseCallback;
import cc.nekocc.cyanchatroom.model.dto.request.*;
import cc.nekocc.cyanchatroom.model.dto.response.*;
import cc.nekocc.cyanchatroom.model.entity.Conversation;
import cc.nekocc.cyanchatroom.model.entity.Message;
import cc.nekocc.cyanchatroom.model.entity.User;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AppRepository
{
    private static final AppRepository INSTANCE = new AppRepository();

    private final NetworkService network_service_;
    private final HttpService http_service_;
    private final LocalPersistenceService persistence_service_;

    private final Map<String, CompletableFuture<String>> upload_permission_futures_ = new ConcurrentHashMap<>();
    private final Map<String, File> pending_uploads_ = new ConcurrentHashMap<>();

    private Map<UUID, CompletableFuture<GetUserDetailsResponse>> get_user_details_permission_futures_ = new ConcurrentHashMap<>();

    public enum ConnectionStatus
    {DISCONNECTED, CONNECTING, CONNECTED, FAILED}

    private final ReadOnlyObjectWrapper<ConnectionStatus> connection_status_ = new ReadOnlyObjectWrapper<>(ConnectionStatus.DISCONNECTED);
    private final ReadOnlyObjectWrapper<User> current_user_ = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyStringWrapper last_error_message_ = new ReadOnlyStringWrapper();
    private final ObservableMap<UUID, ObservableList<Message>> conversation_messages_ = FXCollections.observableMap(new ConcurrentHashMap<>());
    private final ObservableList<Conversation> conversations_ = FXCollections.observableArrayList();

    private ResponseCallback login_success_callback_;
    private ResponseCallback register_success_callback_;
    private ResponseCallback broadcast_message_callback_;
    private ResponseCallback file_upload_response_callback_;
    private ResponseCallback error_response_callback_;


    private AppRepository()
    {
        this.http_service_ = new HttpService();
        this.persistence_service_ = new LocalPersistenceService();
        this.network_service_ = new NetworkService(this::onMessageReceived, this::onConnectionOpened, this::onConnectionClosed);
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

    public void register(String user_name, String password, String nick_name)
    {
        sendRequest("REGISTER_REQUEST", new RegisterRequest(user_name, password, nick_name));
    }

    public void login(String user_name, String password)
    {
        sendRequest("LOGIN_REQUEST", new LoginRequest(user_name, password));
    }

    public void sendMessage(String recipient_type, UUID recipient_id, String content_type, boolean is_encrypted, String content)
    {
        User current_user = current_user_.get();
        if (current_user == null)
        {
            last_error_message_.set("Error: UNVAILD User Session");
            return;
        }

        Message local_message = new Message(recipient_id, current_user.getId(), true, content_type, content);
        persistence_service_.saveMessage(local_message);
        getMessagesForConversation(recipient_id).add(local_message);

        sendRequest("CHAT_MESSAGE", new ChatMessageRequest(recipient_type, recipient_id, content_type, is_encrypted, content));
    }

    public void createGroup(String group_name, List<UUID> member_ids)
    {
        sendRequest("CREATE_GROUP_REQUEST", new CreateGroupRequest(group_name, member_ids));
    }

    public CompletableFuture<GetUserDetailsResponse> getUserDetails(UUID user_id)
    {
        CompletableFuture<GetUserDetailsResponse> future = new CompletableFuture<>();
        UUID request_id = UUID.randomUUID();
        get_user_details_permission_futures_.put(request_id, future);
        sendRequest("GET_USER_DETAILS_REQUEST", new GetUserDetailsRequest(request_id, user_id));
        return future;
    }

    public CompletableFuture<String> uploadFile(File file, Integer expires_in_hours)
    {
        CompletableFuture<String> result_future = new CompletableFuture<>();
        String client_request_id = UUID.randomUUID().toString();
        upload_permission_futures_.put(client_request_id, result_future);
        pending_uploads_.put(client_request_id, file);
        sendRequest("REQUEST_FILE_UPLOAD", new FileUploadRequest(file.getName(), file.length(), expires_in_hours, client_request_id));
        return result_future;
    }

    public void updateProfile(String nick_name, String signature, String avatar_file_id)
    {
        sendRequest("UPDATE_PROFILE_REQUEST", new UpdateProfileRequest(nick_name, signature, avatar_file_id));
    }

    public void changeUsername(String current_password, String new_user_name)
    {
        sendRequest("CHANGE_USERNAME_REQUEST", new ChangeUsernameRequest(new_user_name, current_password));
    }

    public void changePassword(String current_password, String new_password)
    {
        sendRequest("CHANGE_PASSWORD_REQUEST", new ChangePasswordRequest(current_password, new_password));
    }

    public void publishKeys(JsonObject key_bundle)
    {
        sendRequest("PUBLISH_KEYS_REQUEST", new PublishKeysRequest(key_bundle));
    }

    public void fetchKeys(UUID user_id)
    {
        sendRequest("FETCH_KEYS_REQUEST", new FetchKeysRequest(user_id));
    }

    public void joinGroup(UUID group_id, String request_message)
    {
        sendRequest("JOIN_GROUP_REQUEST", new JoinGroupRequest(group_id, request_message));
    }

    public void handleJoinRequest(Long request_id, boolean approved)
    {
        sendRequest("HANDLE_JOIN_REQUEST", new HandleJoinRequest(request_id, approved));
    }

    public void leaveGroup(UUID group_id)
    {
        sendRequest("LEAVE_GROUP_REQUEST", new LeaveGroupRequest(group_id));
    }

    public void removeMember(UUID group_id, UUID target_user_id)
    {
        sendRequest("REMOVE_MEMBER_REQUEST", new RemoveMemberRequest(group_id, target_user_id));
    }

    private <T> void sendRequest(String type, T payload)
    {
        if (connection_status_.get() != ConnectionStatus.CONNECTED && !"LOGIN_REQUEST".equals(type) && !"REGISTER_REQUEST".equals(type))
        {
            last_error_message_.set("错误: 尚未连接到服务器.");
            return;
        }
        ProtocolMessage<T> request = new ProtocolMessage<>(type, payload);
        network_service_.sendMessage(JsonUtil.serialize(request));
    }

    private void onConnectionOpened()
    {
        connection_status_.set(ConnectionStatus.CONNECTED);
    }

    private void onConnectionClosed()
    {
        connection_status_.set(ConnectionStatus.DISCONNECTED);
        current_user_.set(null);
    }

    private void onMessageReceived(String json_message)
    {
        Platform.runLater(() ->
        {
            try
            {
                JsonObject json_object = JsonParser.parseString(json_message).getAsJsonObject();
                String type = json_object.get("type").getAsString();

                switch (type)
                {
                    case "LOGIN_SUCCESS":
                        handleLoginSuccess(json_message);
                        break;
                    case "REGISTER_SUCCESS":
                        handleRegisterSuccess(json_message);
                        break;
                    case "BROADCAST_MESSAGE":
                        handleBroadcastMessage(json_message);
                        break;
                    case "RESPONSE_FILE_UPLOAD":
                        handleFileUploadResponse(json_message);
                        break;
                    case "ERROR_RESPONSE":
                        handleErrorResponse(json_message);
                        break;
                    case "GET_USER_DETAILS_SUCCESS":
                        handleGetUserDetailsSuccess(json_message);
                        break;
                    // TODO: 处理所有其他类型的服务器响应和推送
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                last_error_message_.set("处理服务器消息时出错: " + e.getMessage());
            }
        });
    }

    private void handleLoginSuccess(String json_message)
    {
        ProtocolMessage<UserLoginResponse> response = JsonUtil.deserializeProtocolMessage(json_message, UserLoginResponse.class);
        UserLoginResponse.UserDTO user_dto = response.getPayload().user();
        this.current_user_.set(new User(user_dto));
        System.out.println("登录成功: " + user_dto.nick_name());
        // TODO: 离线消息测试

        if (login_success_callback_ != null)
        {
            login_success_callback_.onResponse(response);
        }
    }

    private void handleRegisterSuccess(String json_message)
    {
        System.out.println("Register success");
        if (register_success_callback_ != null)
        {
            ProtocolMessage<UserLoginResponse.UserDTO> response =
                    JsonUtil.deserializeProtocolMessage(json_message, UserLoginResponse.UserDTO.class);
            register_success_callback_.onResponse(response);
        }
    }

    private void handleGetUserDetailsSuccess(String json_message)
    {
        ProtocolMessage<GetUserDetailsResponse> response = JsonUtil.deserializeProtocolMessage(json_message, GetUserDetailsResponse.class);
        GetUserDetailsResponse payload = response.getPayload();
        UUID request_id = payload.request_id();

        CompletableFuture<GetUserDetailsResponse> future =
                get_user_details_permission_futures_.remove(request_id);

        Platform.runLater(() ->
        {
            future.complete(payload);
        });
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
            conversation_id = is_outgoing ? UUID.fromString((String) payload.get("recipient_id")) : sender_id;
        } else
        {
            conversation_id = UUID.fromString((String) payload.get("group_id"));
        }

        String content_type = (String) payload.get("content_type");
        String content = (String) payload.get("content");
        long timestamp_long = ((Double) payload.get("timestamp")).longValue();
        String iso_time = Instant.ofEpochMilli(timestamp_long).toString();

        Message local_message = new Message(conversation_id, sender_id, is_outgoing, content_type, content, iso_time);

        persistence_service_.saveMessage(local_message);
        getMessagesForConversation(conversation_id).add(local_message);

        if (broadcast_message_callback_ != null)
        {
            broadcast_message_callback_.onResponse(new ProtocolMessage<>("BROADCAST_MESSAGE", local_message));
        }
    }

    private void handleErrorResponse(String json_message)
    {
        ProtocolMessage<ErrorResponse> response = JsonUtil.deserializeProtocolMessage(json_message, ErrorResponse.class);

        if (response.getPayload() == null || response.getPayload().error() == null)
        {
            System.err.println("收到错误响应，但没有错误信息。");
            return;
        }

        String error_info = "请求 [" + response.getPayload().request_type() + "] 失败: " + response.getPayload().error();
        System.err.println(error_info);
        last_error_message_.set(error_info);

        if (error_response_callback_ != null)
        {
            error_response_callback_.onResponse(response);
        }
    }

    private void handleFileUploadResponse(String json_message)
    {
        ProtocolMessage<FileUploadResponse> response = JsonUtil.deserializeProtocolMessage(json_message, FileUploadResponse.class);
        String file_id = response.getPayload().file_id();
        String upload_url = response.getPayload().upload_url();
        String client_request_id = response.getPayload().client_id();

        CompletableFuture<String> future = upload_permission_futures_.remove(client_request_id);
        File file_to_upload = pending_uploads_.remove(client_request_id);

        if (file_to_upload != null && future != null)
        {
            http_service_.uploadFile(upload_url, file_to_upload).thenAccept(success ->
            {
                Platform.runLater(() ->
                {
                    if (success)
                    {
                        future.complete(file_id);
                    } else
                    {
                        future.completeExceptionally(new RuntimeException("HTTP上传失败"));
                    }
                });
            });
        }

        if (file_upload_response_callback_ != null)
        {
            file_upload_response_callback_.onResponse(response);
        }
    }

    public ObservableList<Message> getMessagesForConversation(UUID conversation_id)
    {
        return conversation_messages_.computeIfAbsent(conversation_id, id ->
        {
            List<Message> history = persistence_service_.getMessagesForConversation(id, 50, 0); // 初始加载50条
            return FXCollections.observableArrayList(history);
        });
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

    public void setLoginSuccessCallback(ResponseCallback callback)
    {
        this.login_success_callback_ = callback;
    }
    public void setRegisterSuccessCallback(ResponseCallback callback)
    {
        this.register_success_callback_ = callback;
    }
    public void setBroadcastMessageCallback(ResponseCallback callback)
    {
        this.broadcast_message_callback_ = callback;
    }
    public void setFileUploadResponseCallback(ResponseCallback callback)
    {
        this.file_upload_response_callback_ = callback;
    }
    public void setErrorResponseCallback(ResponseCallback callback)
    {
        this.error_response_callback_ = callback;
    }
}