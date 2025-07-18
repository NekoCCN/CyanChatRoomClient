package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.model.callback.ResponseCallback;
import cc.nekocc.cyanchatroom.model.dto.MessageType;
import cc.nekocc.cyanchatroom.model.dto.request.*;
import cc.nekocc.cyanchatroom.model.dto.request.e2ee.FetchKeysRequest;
import cc.nekocc.cyanchatroom.model.dto.request.e2ee.PublishKeysRequest;
import cc.nekocc.cyanchatroom.model.dto.request.friendship.*;
import cc.nekocc.cyanchatroom.model.dto.request.group.*;
import cc.nekocc.cyanchatroom.model.dto.request.user.*;
import cc.nekocc.cyanchatroom.model.dto.response.*;
import cc.nekocc.cyanchatroom.model.entity.*;
import cc.nekocc.cyanchatroom.model.service.HttpService;
import cc.nekocc.cyanchatroom.model.service.LocalKeyStorageService;
import cc.nekocc.cyanchatroom.model.service.LocalPersistenceService;
import cc.nekocc.cyanchatroom.model.service.NetworkService;
import cc.nekocc.cyanchatroom.model.util.E2EEHelper;
import javax.crypto.SecretKey;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.security.KeyPair;
import java.security.PublicKey;
import cc.nekocc.cyanchatroom.model.util.JsonUtil;
import cc.nekocc.cyanchatroom.protocol.ProtocolMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import java.time.Instant;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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

    private final LocalKeyStorageService key_storage_service_;
        private final Map<UUID, SecretKey> session_keys_ = new ConcurrentHashMap<>();

    private String server_address_;

    private final Map<UUID, ResponseFuture> response_futures_ = new ConcurrentHashMap<>();

    public enum ConnectionStatus
    {DISCONNECTED, CONNECTING, CONNECTED, FAILED, RECONNECTING}

    private final ReadOnlyObjectWrapper<ConnectionStatus> connection_status_ = new ReadOnlyObjectWrapper<>(ConnectionStatus.DISCONNECTED);
    private final ReadOnlyObjectWrapper<User> current_user_ = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyStringWrapper last_error_message_ = new ReadOnlyStringWrapper();
    private final ObservableMap<UUID, ObservableList<Message>> conversation_messages_ = FXCollections.observableMap(new ConcurrentHashMap<>());

    private ResponseCallback message_recrived_callback_ = null;

    private final List<Consumer<Message>> message_listeners_ = new CopyOnWriteArrayList<>();

    private final Set<Conversation> conversations_backing_ = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final ObservableSet<Conversation> conversations_ = FXCollections.observableSet(conversations_backing_);

    private AppRepository()
    {
        this.http_service_ = new HttpService();
        this.persistence_service_ = new LocalPersistenceService();
        this.network_service_ = new NetworkService(this::onMessageReceived, this::onConnectionOpened,
                this::onConnectionClosed, this::onReconnecting, this::onReconnectionFailed);
        this.key_storage_service_ = new LocalKeyStorageService();
    }

    public static AppRepository getInstance()
    {
        return INSTANCE;
    }

    /*
     * 初始化部分
     */
    /**
     * 连接到指定的服务器地址。
     * @param server_address 服务器地址 不含协议前缀和后缀
     */
    public void connectToServer(String server_address)
    {
        if (connection_status_.get() == ConnectionStatus.CONNECTING || connection_status_.get() == ConnectionStatus.CONNECTED)
            return;
        connection_status_.set(ConnectionStatus.CONNECTING);
        server_address_ = server_address;
        String ws_url = "ws://" + server_address + "/ws";
        String http_url = "http://" + server_address;
        http_service_.setBaseUrl(http_url);
        network_service_.connect(ws_url);
    }
    /**
     * 断开与服务器的连接。
     */
    public void disconnect()
    {
        network_service_.disconnect();
    }
    /**
     * 手动设置当前用户，这是在登录或注册成功后调用的。
     * @param user 当前用户对象
     */
    public void setCurrentUser(User user)
    {
        if (user == null)
        {
            current_user_.set(null);
            return;
        }
        current_user_.set(user);
    }

    /*
     * 用户操作相关
     */
    /**
     * 注册新用户。
     * @param user_name 用户名称
     * @param password 用户密码
     * @param nick_name 用户昵称
     * @param signature 用户签名
     * @return 一个CompletableFuture，完成时包含用户操作响应 可能永不完成
     */
    public CompletableFuture<ProtocolMessage<UserOperatorResponse>> register(String user_name, String password,
                                                                             String nick_name, String signature)
    {
        UUID client_request_id = UUID.randomUUID();
        RegisterRequest payload = new RegisterRequest(client_request_id, user_name, password, nick_name, signature);
        return sendRequestWithFuture(MessageType.REGISTER_REQUEST, payload, client_request_id, UserOperatorResponse.class);
    }
    /**
     * 用户登录。
     * @param user_name 用户名称
     * @param password 用户密码
     * @return 一个CompletableFuture，完成时包含用户操作响应 可能永不完成
     */
    public CompletableFuture<ProtocolMessage<UserOperatorResponse>> login(String user_name, String password)
    {
        UUID client_request_id = UUID.randomUUID();
        LoginRequest payload = new LoginRequest(client_request_id, user_name, password);

        return sendRequestWithFuture(MessageType.LOGIN_REQUEST, payload, client_request_id, UserOperatorResponse.class);
    }
    public void loadHistoryConversion()
    {
        if (server_address_ == null || server_address_.isEmpty() || current_user_.get() == null)
        {
            throw new IllegalStateException("INVALID_STATE: 服务器地址或当前用户未设置。");
        }
        conversations_.addAll(persistence_service_.getAllConversations(server_address_, current_user_.get().getId()));
    }

    /*
     * 发送消息相关
     */
    public ObservableList<Message> getObservableMessagesForConversation(UUID conversation_id)
    {
        User current_user = current_user_.get();
        if (current_user == null)
        {
            return FXCollections.observableArrayList();
        }
        return conversation_messages_.computeIfAbsent(conversation_id, id ->
        {
            System.out.println("缓存未命中，正在为对话 " + id + " 从数据库加载历史消息...");
            List<Message> history = persistence_service_.getAllMessagesForConversation(server_address_, current_user.getId(), id);
            System.out.println("为对话 " + id + " 加载了 " + history.size() + " 条历史消息。");
            return FXCollections.observableArrayList(history);
        });
    }
    /**
     * 异步发送聊天消息请求。
     * @param recipient_type 接收者类型，可以是 "USER" 或 "GROUP"
     * @param recipient_id 接收者的唯一标识符
     * @param content_type 内容类型，例如 "TEXT" "FILE" 或 "IMAGE"
     * @param is_encrypted 是否加密消息
     * @param content 消息内容
     * @return 一个 CompletableFuture，完成时包含发送状态的响应
     */
    private CompletableFuture<ProtocolMessage<StatusResponse>> sendChatMessageRequestAsync(String recipient_type, UUID recipient_id, String content_type, boolean is_encrypted, String content)
    {
        User current_user = current_user_.get();
        if (current_user == null)
        {
            return CompletableFuture.failedFuture(new IllegalStateException("用户未登录，无法发送消息。"));
        }

        UUID client_request_id = UUID.randomUUID();
        ChatMessageRequest payload = new ChatMessageRequest(client_request_id, recipient_type, recipient_id, content_type, is_encrypted, content);

        return sendRequestWithFuture(MessageType.CHAT_MESSAGE, payload, client_request_id, StatusResponse.class);
    }
    /**
     * 发送普通（非加密）消息到指定的接收者。
     * @param recipient_type 接收者类型，可以是 "USER" 或 "GROUP"
     * @param recipient_id 接收者的唯一标识符
     * @param content_type 内容类型，例如 "TEXT" "FILE" 或 "IMAGE"
     * @param is_encrypted 是否加密消息
     * @param content 消息内容
     * @return 一个 CompletableFuture，完成时包含发送状态的响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> sendMessage(String recipient_type, UUID recipient_id, String content_type,
                                                                          boolean is_encrypted, String content)
    {
        User current_user = current_user_.get();
        Message local_message = new Message(recipient_id, current_user.getId(), true, "TEXT", content);
        persistence_service_.saveMessage(server_address_, current_user.getId(), local_message);
        return sendChatMessageRequestAsync(recipient_type, recipient_id, content_type, is_encrypted, content);
    }

    /**
     * 发送加密的文本消息到指定的接收者。
     * @param recipient_id 接收者的唯一标识符
     * @param plain_text 明文内容
     * @return 一个 CompletableFuture，完成时包含发送状态的响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> sendEncryptedTextMessage(UUID recipient_id, String plain_text)
    {
        User current_user = current_user_.get();
        if (current_user == null)
        {
            last_error_message_.set("用户未登录");
            return CompletableFuture.failedFuture(new IllegalStateException("用户未登录"));
        }

        AtomicReference<CompletableFuture<ProtocolMessage<StatusResponse>>> res = new AtomicReference<>();

        SecretKey session_key = session_keys_.get(recipient_id);

        Message local_message = new Message(recipient_id, current_user.getId(), true,
                "TEXT", plain_text);
        persistence_service_.saveMessage(server_address_, current_user.getId(), local_message);

        AppRepository.getInstance().getUserDetails(recipient_id)
                .thenAccept(response ->
                {
                    if (session_keys_.containsKey(recipient_id))
                    {
                        if (!response.getPayload().is_online())
                        {
                            session_keys_.remove(recipient_id);
                        }
                    }

                    if (session_key != null)
                    {
                        try
                        {
                            String encrypted_content = E2EEHelper.encrypt(plain_text, session_key);
                            res.set(sendChatMessageRequestAsync("USER",
                                    recipient_id, "TEXT", true, encrypted_content));
                        } catch (Exception e)
                        {
                            last_error_message_.set("加密失败: " + e.getMessage());
                            res.set(CompletableFuture.failedFuture(e));
                        }
                    }

                    System.out.println("没有找到会话密钥, 开始密钥交换...");
                    res.set(fetchKeys(recipient_id).thenComposeAsync(responseMsg ->
                    {
                        FetchKeysResponse payload = responseMsg.getPayload();

                        if (!payload.succeed())
                        {
                            String error_msg = "无法获取对方密钥，无法发送加密消息。";
                            Platform.runLater(() -> last_error_message_.set(error_msg));
                            return CompletableFuture.failedFuture(new RuntimeException(error_msg));
                        }

                        try
                        {
                            KeyPair ephemeral_key_pair = E2EEHelper.generateKeyPair();
                            JsonObject key_bundle = JsonParser.parseString(payload.public_key_bundle()).getAsJsonObject();
                            String identity_key_base64 = key_bundle.get("identityKey").getAsString();
                            PublicKey recipient_identity_key = E2EEHelper.publicKeyFromBase64(identity_key_base64);

                            SecretKey new_session_key = E2EEHelper.deriveSharedSecret(ephemeral_key_pair.getPrivate(), recipient_identity_key);
                            session_keys_.put(recipient_id, new_session_key);

                            String encrypted_content = E2EEHelper.encrypt(plain_text, new_session_key);

                            JsonObject init_payload = new JsonObject();
                            init_payload.addProperty("ephemeral_pub_key", Base64.getEncoder().encodeToString(ephemeral_key_pair.getPublic().getEncoded()));
                            init_payload.addProperty("ciphertext", encrypted_content);

                            return sendChatMessageRequestAsync("USER", recipient_id, "E2EE_INIT", true, init_payload.toString());

                        } catch (Exception e)
                        {
                            String error_msg = "密钥协商失败: " + e.getMessage();
                            Platform.runLater(() -> last_error_message_.set(error_msg));
                            return CompletableFuture.failedFuture(new RuntimeException(error_msg, e));
                        }
                    }));

                });
        return res.get();
    }
    /**
     * 生成并注册新的密钥对，并将其发布到服务器，私钥存储到本地。
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> generateAndRegisterKeys()
    {
        User current_user = current_user_.get();
        if (current_user == null)
            return CompletableFuture.failedFuture(new IllegalStateException("用户未登录"));

        try
        {
            KeyPair key_pair = E2EEHelper.generateKeyPair();

            key_storage_service_.saveKeyPair(server_address_, current_user.getId(), key_pair);
            System.out.println("密钥已生成并存储在本地。");

            String public_key_base64 = Base64.getEncoder().encodeToString(key_pair.getPublic().getEncoded());
            JsonObject key_bundle = new JsonObject();
            key_bundle.addProperty("identityKey", public_key_base64);

            return publishKeys(key_bundle);

        } catch (Exception e)
        {
            return CompletableFuture.failedFuture(e);
        }
    }
    /**
     * 从服务器获取指定用户的公钥。
     * @param user_id 要获取公钥的用户ID
     * @return 一个CompletableFuture，完成时包含FetchKeysResponse
     */
    public CompletableFuture<ProtocolMessage<FetchKeysResponse>> fetchKeys(UUID user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.FETCH_KEYS_REQUEST,
                new FetchKeysRequest(client_request_id, user_id), client_request_id, FetchKeysResponse.class);
    }
    /**
     * 发布密钥到服务器。
     * @param key_bundle 包含公钥的JSON对象
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> publishKeys(JsonObject key_bundle)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.PUBLISH_KEYS_REQUEST,
                new PublishKeysRequest(client_request_id, key_bundle), client_request_id, StatusResponse.class);
    }

    /*
     * 文件相关
     */

    /**
     * 上传文件到服务器。
     * @param file 要上传的文件，一个路径对象。
     * @param expires_in_hours 过期时间，单位为小时，具体区间取决于服务器设置。
     * @return 一个CompletableFuture，完成时包含上传后的文件ID
     */
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
    /**
     * 下载文件到指定位置。
     * @param file_id 文件的唯一标识符
     * @param save_location 保存文件的本地路径
     * @return 一个CompletableFuture，完成时包含下载是否成功的布尔值
     */
    public CompletableFuture<Boolean> downloadFile(String file_id, File save_location)
    {
        return http_service_.downloadFile(file_id, save_location);
    }

    /*
     * 用户资料相关
     */
    /**
     * 根据用户名获取用户的 UUID。
     * @param username 要查询的用户名
     * @return 一个 CompletableFuture，完成时包含查询结果的响应
     */
    public CompletableFuture<ProtocolMessage<GetUuidByUsernameResponse>> getUuidByUsername(String username)
    {
        UUID client_request_id = UUID.randomUUID();
        GetUuidByUsernameRequest payload = new GetUuidByUsernameRequest(client_request_id, username);
        return sendRequestWithFuture(MessageType.GET_UUID_BY_USERNAME_REQUEST, payload, client_request_id, GetUuidByUsernameResponse.class);
    }
    /**
     * 更新用户资料。
     * @param nick_name 用户昵称
     * @param signature 用户签名
     * @param avatar_file_id 头像文件的唯一标识符（或URI）
     * @param status 用户状态
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> updateProfile(String nick_name, String signature,
                                                                            String avatar_file_id, UserStatus status)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.UPDATE_PROFILE_REQUEST,
                new UpdateProfileRequest(client_request_id, nick_name, signature, avatar_file_id, status),
                client_request_id, StatusResponse.class);
    }
    /**
     * 修改用户名。
     * @param current_password 当前密码
     * @param new_user_name 新用户名
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> changeUsername(String current_password, String new_user_name)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.CHANGE_USERNAME_REQUEST,
                new ChangeUsernameRequest(client_request_id, new_user_name, current_password), client_request_id,
                StatusResponse.class);
    }
    /**
     * 修改用户密码。
     * @param current_password 当前密码
     * @param new_password 新密码
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> changePassword(String current_password, String new_password)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.CHANGE_PASSWORD_REQUEST,
                new ChangePasswordRequest(client_request_id, current_password, new_password), client_request_id,
                StatusResponse.class);
    }
    /**
     * 获取用户详细信息。
     * @param user_id 要获取信息的用户ID
     * @return 一个CompletableFuture，完成时包含用户详细信息响应
     */
    public CompletableFuture<ProtocolMessage<GetUserDetailsResponse>> getUserDetails(UUID user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.GET_USER_DETAILS_REQUEST,
                new GetUserDetailsRequest(client_request_id, user_id), client_request_id, GetUserDetailsResponse.class);
    }

    /*
     * 群组相关
     */
    /**
     * 创建一个新的群组。
     * @param group_name 群组名称
     * @param member_ids 初始群组成员的唯一标识符列表
     * @return 一个CompletableFuture，完成时包含群组创建响应
     */
    public CompletableFuture<ProtocolMessage<GroupResponse>> createGroup(String group_name, List<UUID> member_ids)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.CREATE_GROUP_REQUEST,
                new CreateGroupRequest(client_request_id, group_name, member_ids), client_request_id, GroupResponse.class);
    }

    /**
     * 请求加入一个群组。
     * @param group_id 群组的唯一标识符
     * @param request_message 请求加入群组时的消息内容
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> joinGroup(UUID group_id, String request_message)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.JOIN_GROUP_REQUEST,
                new JoinGroupRequest(client_request_id, group_id, request_message), client_request_id,
                StatusResponse.class);
    }
    /**
     * 处理加入群组的请求。
     * @param group_id 群组的唯一标识符
     * @param request_id 加入请求的唯一标识符
     * @param approved 是否批准加入请求
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> handleJoinRequest(UUID group_id, UUID request_id,
                                                                                boolean approved)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.HANDLE_JOIN_REQUEST,
                new HandleJoinRequest(client_request_id, group_id, request_id, approved), client_request_id,
                StatusResponse.class);
    }

    /**
     * 离开一个群组。
     * @param group_id 群组的唯一标识符
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> leaveGroup(UUID group_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.LEAVE_GROUP_REQUEST,
                new LeaveGroupRequest(client_request_id, group_id), client_request_id, StatusResponse.class);
    }
    /**
     * 从群组中移除成员。
     * @param group_id 群组的唯一标识符
     * @param target_user_id 要移除的成员的唯一标识符
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> removeMember(UUID group_id, UUID target_user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.REMOVE_MEMBER_REQUEST,
                new RemoveMemberRequest(client_request_id, group_id, target_user_id), client_request_id,
                StatusResponse.class);
    }
    /**
     * 更改群组成员的角色。
     * @param group_id 群组的唯一标识符
     * @param target_user_id 要更改角色的成员的唯一标识符
     * @param new_role 新的角色枚举值
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> setGroupMemberRole(UUID group_id, UUID target_user_id,
                                                                                 GroupMemberRole new_role)
    {
        UUID client_request_id = UUID.randomUUID();
        return sendRequestWithFuture(MessageType.SET_MEMBER_ROLE_REQUEST,
                new SetMemberRoleRequest(client_request_id, group_id, target_user_id, new_role.name()), client_request_id,
                StatusResponse.class);
    }

    /*
     * 好友相关
     */
    /**
     * 发送好友请求。
     * @param receiver_id 接收者用户的唯一标识符
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> sendFriendshipRequest(UUID receiver_id)
    {
        User current_user = current_user_.get();
        if (current_user == null)
        {
            return CompletableFuture.failedFuture(new IllegalStateException("用户未登录"));
        }

        UUID client_request_id = UUID.randomUUID();
        SendFriendshipRequest payload = new SendFriendshipRequest(client_request_id, current_user.getId(), receiver_id);

        return sendRequestWithFuture(MessageType.SEND_FRIENDSHIP_REQUEST, payload, client_request_id, StatusResponse.class);
    }

    /**
     * 接受好友请求。
     * @param request_id 好友请求的唯一标识符
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> acceptFriendshipRequest(UUID request_id)
    {
        UUID client_request_id = UUID.randomUUID();
        AcceptFriendshipRequest payload = new AcceptFriendshipRequest(client_request_id, request_id);
        return sendRequestWithFuture(MessageType.ACCEPT_FRIENDSHIP_REQUEST, payload, client_request_id, StatusResponse.class);
    }

    /**
     * 拒绝好友请求。
     * @param request_id 好友请求的唯一标识符
     * @return 一个CompletableFuture，完成时包含状态响应
     */
    public CompletableFuture<ProtocolMessage<StatusResponse>> rejectFriendshipRequest(UUID request_id)
    {
        UUID client_request_id = UUID.randomUUID();
        RejectFriendshipRequest payload = new RejectFriendshipRequest(client_request_id, request_id);
        return sendRequestWithFuture(MessageType.REJECT_FRIENDSHIP_REQUEST, payload, client_request_id, StatusResponse.class);
    }

    /**
     * 获取指定用户的所有好友关系列表（包括待处理的）。
     * @param user_id 用户的唯一标识符
     * @return 一个CompletableFuture，完成时包含好友列表响应
     */
    public CompletableFuture<ProtocolMessage<FriendshipListResponse>> getFriendshipList(UUID user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        GetFriendshipListRequest payload = new GetFriendshipListRequest(client_request_id, user_id);
        return sendRequestWithFuture(MessageType.GET_FRIENDSHIP_LIST_REQUEST, payload, client_request_id, FriendshipListResponse.class);
    }

    /**
     * 获取指定用户的已激活的好友列表。
     * @param user_id 用户的唯一标识符
     * @return 一个CompletableFuture，完成时包含好友列表响应
     */
    public CompletableFuture<ProtocolMessage<FriendshipListResponse>> getActiveFriendshipList(UUID user_id)
    {
        UUID client_request_id = UUID.randomUUID();
        GetFriendshipListRequest payload = new GetFriendshipListRequest(client_request_id, user_id);
        return sendRequestWithFuture(MessageType.GET_ACTIVE_FRIENDSHIP_LIST_REQUEST, payload, client_request_id, FriendshipListResponse.class);
    }

    /**
     * 检查两个用户之间是否存在好友关系。
     * @param user_id1 用户1的ID
     * @param user_id2 用户2的ID
     * @return 一个CompletableFuture，完成时包含检查结果的响应
     */
    public CompletableFuture<ProtocolMessage<CheckFriendshipExistsResponse>> checkFriendshipExists(UUID user_id1, UUID user_id2)
    {
        UUID client_request_id = UUID.randomUUID();
        CheckFriendshipExistsRequest payload = new CheckFriendshipExistsRequest(client_request_id, user_id1, user_id2);
        return sendRequestWithFuture(MessageType.CHECK_FRIENDSHIP_EXISTS_REQUEST, payload, client_request_id, CheckFriendshipExistsResponse.class);
    }

    /*
     * 请求通用方法
     */
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

    /*
     * 回调相关
     */
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
    private void onReconnectionFailed()
    {
        Platform.runLater(() -> connection_status_.set(ConnectionStatus.FAILED));
    }
    private void onReconnecting()
    {
        Platform.runLater(() -> connection_status_.set(ConnectionStatus.RECONNECTING));
    }

    /*
     * 服务器消息处理
     */
    private void onMessageReceived(String json_message)
    {
        try {
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
                case "GET_USER_DETAILS_RESPONSE":
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, GetUserDetailsResponse.class));
                    break;
                case MessageType.GET_FRIENDSHIP_LIST_RESPONSE:
                case MessageType.GET_ACTIVE_FRIENDSHIP_LIST_RESPONSE:
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, FriendshipListResponse.class));
                    break;
                case MessageType.GET_UUID_BY_USERNAME_RESPONSE:
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, GetUuidByUsernameResponse.class));
                    break;
                case MessageType.CHECK_FRIENDSHIP_EXISTS_RESPONSE:
                    future.complete(JsonUtil.deserializeProtocolMessage(json_message, CheckFriendshipExistsResponse.class));
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
        UUID recipient_id = UUID.fromString((String) payload.get("receiver_id"));

        if ("USER".equals(recipient_type))
        {
            conversation_id = is_outgoing ? recipient_id : sender_id;
        } else
        {
            conversation_id = UUID.fromString((String) payload.get("group_id"));
        }

        String content_type = (String) payload.get("content_type");
        String content = (String) payload.get("content");

        long timestamp_long = ((Double) payload.get("timestamp")).longValue();
        String iso_time = Instant.ofEpochMilli(timestamp_long).toString();

        boolean is_encrypted = (boolean)payload.get("is_encrypted");

        if (is_encrypted)
        {
            try
            {
                SecretKey session_key = session_keys_.get(sender_id);
                if (session_key == null)
                {
                    if ("E2EE_INIT".equals(content_type))
                    {
                        System.out.println("收到E2EE初始化消息, 开始计算会话密钥...");

                        JsonObject init_payload = JsonParser.parseString(content).getAsJsonObject();

                        String ephemeral_pub_key_base64 = init_payload.get("ephemeral_pub_key").getAsString();
                        String ciphertext = init_payload.get("ciphertext").getAsString();

                        PublicKey sender_ephemeral_key = E2EEHelper.publicKeyFromBase64(ephemeral_pub_key_base64);
                        KeyPair my_key_pair = key_storage_service_.loadKeyPair(server_address_, current_user.getId());
                        if (my_key_pair == null)
                            throw new Exception("找不到本地私钥!");

                        SecretKey new_session_key = E2EEHelper.deriveSharedSecret(my_key_pair.getPrivate(), sender_ephemeral_key);
                        session_keys_.put(sender_id, new_session_key);

                        content = E2EEHelper.decrypt(ciphertext, new_session_key);
                        content_type = "TEXT";
                    } else
                    {
                        throw new Exception("收到加密消息, 但没有找到会话密钥。");
                    }
                } else
                {
                    content = E2EEHelper.decrypt(content, session_key);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                content = "[无法解密的消息]";
            }
        }

        Message local_message = new Message(conversation_id, sender_id, is_outgoing, content_type, content);

        persistence_service_.saveMessage(server_address_, current_user.getId(), local_message);

        final UUID final_conversation_id = conversation_id;
        Platform.runLater(() -> {
            ObservableList<Message> messageList = getObservableMessagesForConversation(final_conversation_id);
            messageList.add(local_message);
        });

        for (Consumer<Message> listener : message_listeners_)
        {
            Platform.runLater(() -> listener.accept(local_message));
        }

        conversations_.add(new Conversation(
                conversation_id,
                sender_id,
                ConversationType.valueOf(recipient_type),
                OffsetDateTime.parse(iso_time).toString()));

        persistence_service_.upsertConversation(
                server_address_,
                current_user.getId(),
                conversation_id,
                ConversationType.valueOf(recipient_type),
                recipient_id,
                OffsetDateTime.parse(iso_time)
        );
    }

    /*
     * Property 相关
     */
    public void addMessageListener(Consumer<Message> listener)
    {
        message_listeners_.add(listener);
    }
    public void removeMessageListener(Consumer<Message> listener)
    {
        message_listeners_.remove(listener);
    }
    public ReadOnlyObjectProperty<ConnectionStatus> connectionStatusProperty()
    {
        return connection_status_.getReadOnlyProperty();
    }
    public ReadOnlyObjectProperty<User> currentUserProperty()
    {
        return current_user_.getReadOnlyProperty();
    }
    public ObservableSet<Conversation> getConversations()
    {
        return FXCollections.unmodifiableObservableSet(conversations_);
    }
    public ReadOnlyStringProperty lastErrorMessageProperty()
    {
        return last_error_message_.getReadOnlyProperty();
    }
    public ObservableList<Message> getClipedMessagesForConversation(UUID conversation_id, int block)
    {
        return conversation_messages_.computeIfAbsent(conversation_id, id ->
        {
            List<Message> history = persistence_service_.getClipedMessagesForConversation(server_address_, current_user_.get().getId(),
                    id, 50, block * 50);
            return FXCollections.observableArrayList(history);
        });
    }
    public ObservableList<Message> getAllMessagesForConversation(UUID conversation_id, UUID block)
    {
        return conversation_messages_.computeIfAbsent(conversation_id, id ->
        {
            List<Message> history = persistence_service_.getAllMessagesForConversation(server_address_, current_user_.get().getId(),
                    id);
            return FXCollections.observableArrayList(history);
        });
    }
    public int countMessagesInConversation(UUID conversation_id)
    {
        return persistence_service_.countMessagesInConversation(server_address_, current_user_.get().getId(), conversation_id);
    }

}