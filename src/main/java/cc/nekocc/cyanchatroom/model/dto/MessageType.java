package cc.nekocc.cyanchatroom.model.dto;

public final class MessageType
{
    private MessageType()
    {  }

    public static final String REGISTER_REQUEST = "REGISTER_REQUEST";
    public static final String REGISTER_RESPONSE = "REGISTER_RESPONSE";

    public static final String LOGIN_REQUEST = "LOGIN_REQUEST";

    public static final String UPDATE_PROFILE_REQUEST = "UPDATE_PROFILE_REQUEST";
    public static final String CHANGE_USERNAME_REQUEST = "CHANGE_USERNAME_REQUEST";
    public static final String CHANGE_PASSWORD_REQUEST = "CHANGE_PASSWORD_REQUEST";

    public static final String CHAT_MESSAGE = "CHAT_MESSAGE";

    public static final String GET_USER_DETAILS_REQUEST = "GET_USER_DETAILS_REQUEST";
    public static final String GET_USER_DETAILS_RESPONSE = "GET_USER_DETAILS_RESPONSE";

    public static final String CREATE_GROUP_REQUEST = "CREATE_GROUP_REQUEST";
    public static final String CREATE_GROUP_RESPONSE = "CREATE_GROUP_RESPONSE";

    public static final String JOIN_GROUP_REQUEST = "JOIN_GROUP_REQUEST";
    public static final String HANDLE_JOIN_REQUEST = "HANDLE_JOIN_REQUEST";
    public static final String LEAVE_GROUP_REQUEST = "LEAVE_GROUP_REQUEST";
    public static final String REMOVE_MEMBER_REQUEST = "REMOVE_MEMBER_REQUEST";
    public static final String SET_MEMBER_ROLE_REQUEST = "SET_MEMBER_ROLE_REQUEST";
    public static final String CHANGE_GROUP_JOIN_MODE_REQUEST = "CHANGE_GROUP_JOIN_MODE_REQUEST";

    public static final String GET_UUID_BY_USERNAME_REQUEST = "GET_UUID_BY_USERNAME_REQUEST";
    public static final String GET_UUID_BY_USERNAME_RESPONSE = "GET_UUID_BY_USERNAME_RESPONSE";

    public static final String REQUEST_FILE_UPLOAD = "REQUEST_FILE_UPLOAD";

    public static final String PUBLISH_KEYS_REQUEST = "PUBLISH_KEYS_REQUEST";
    public static final String FETCH_KEYS_REQUEST = "FETCH_KEYS_REQUEST";

    public static final String ACCEPT_FRIENDSHIP_REQUEST = "ACCEPT_FRIENDSHIP_REQUEST";
    public static final String REJECT_FRIENDSHIP_REQUEST = "REJECT_FRIENDSHIP_REQUEST";
    public static final String SEND_FRIENDSHIP_REQUEST = "SEND_FRIENDSHIP_REQUEST";
    public static final String GET_FRIENDSHIP_LIST_REQUEST = "GET_FRIENDSHIP_LIST_REQUEST";
    public static final String GET_FRIENDSHIP_LIST_RESPONSE = "GET_FRIENDSHIP_LIST_RESPONSE";
    public static final String GET_ACTIVE_FRIENDSHIP_LIST_REQUEST = "GET_ACTIVE_FRIENDSHIP_LIST_REQUEST";
    public static final String GET_ACTIVE_FRIENDSHIP_LIST_RESPONSE = "GET_ACTIVE_FRIENDSHIP_LIST_RESPONSE";

    public static final String CHECK_FRIENDSHIP_EXISTS_REQUEST = "CHECK_FRIENDSHIP_EXISTS_REQUEST";
    public static final String CHECK_FRIENDSHIP_EXISTS_RESPONSE = "CHECK_FRIENDSHIP_EXISTS_RESPONSE";
}