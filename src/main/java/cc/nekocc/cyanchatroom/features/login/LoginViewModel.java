package cc.nekocc.cyanchatroom.features.login;

import cc.nekocc.cyanchatroom.Navigator;
import cc.nekocc.cyanchatroom.domain.client.AbstractClient;
import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.dto.response.UserOperatorResponse;
import cc.nekocc.cyanchatroom.model.entity.User;
import cc.nekocc.cyanchatroom.model.entity.UserStatus;
import cc.nekocc.cyanchatroom.protocol.ProtocolMessage;
import javafx.beans.property.*;
import javafx.scene.control.Alert;

import java.util.concurrent.CompletableFuture;

public class LoginViewModel
{
    private boolean is_logining_ = false;
    private boolean is_registering_ = false;


    private final IntegerProperty version_slider_property_ = new SimpleIntegerProperty(1);
    private final StringProperty login_username_ = new SimpleStringProperty("");
    private final StringProperty login_password_ = new SimpleStringProperty("");

    private final StringProperty register_username_ = new SimpleStringProperty("");
    private final StringProperty register_password_ = new SimpleStringProperty("");
    private final StringProperty register_nickname_ = new SimpleStringProperty("");
    private final StringProperty register_signature_ = new SimpleStringProperty("");
    private final ObjectProperty<AbstractClient> register_client_type_ = new SimpleObjectProperty<>();

    private final BooleanProperty login_button_disabled_ = new SimpleBooleanProperty(true);
    private final BooleanProperty register_next_button_disabled_ = new SimpleBooleanProperty(true);
    private final BooleanProperty register_button_disabled_ = new SimpleBooleanProperty(true);
    private final BooleanProperty accept_version_ = new SimpleBooleanProperty();

    public LoginViewModel()
    { setupBindings();}



    private void setupBindings()
    {
        login_button_disabled_.bind(
                login_username_.isEmpty().or(login_password_.isEmpty())
        );

        register_next_button_disabled_.bind(
                register_username_.isEmpty().or(register_password_.isEmpty())
        );

        register_button_disabled_.bind(
                register_nickname_.isEmpty()
                        .or(accept_version_.not())
        );
    }

    public void login()
    {
        if (is_logining_)
        {
            return;
        }

        CompletableFuture<ProtocolMessage<UserOperatorResponse>> user_response =
                AppRepository.getInstance().login(login_username_.get(), login_password_.get());

        is_logining_ = true;

        user_response.thenAccept(response ->
        {
            if (response.getPayload().success())
            {
                AppRepository.getInstance().setCurrentUser(new User(response.getPayload().user()));
                Navigator.navigateTo("fxml/turnToChatPage.fxml",800,600,1000);
            }
            else
            {
                showAlert(Alert.AlertType.ERROR, "登录失败", response.getPayload().message());
            }
            is_logining_ = false;
        }).exceptionally(ex ->
        {
            showAlert(Alert.AlertType.ERROR, "登录失败", "发生错误: " + ex.getMessage());
            is_logining_ = false;
            return null;
        });
    }

    public void register()
    {
        if (is_registering_)
        {
            return;
        }

        try
        {
            String username = register_username_.get();
            String password = register_password_.get();
            String nickname = register_nickname_.get();
            String signature;
            if (!register_signature_.isEmpty().get())
            {
                signature = register_signature_.get();
            } else
            {
                signature = "";
            }

            CompletableFuture<ProtocolMessage<UserOperatorResponse>> user_response =
                    AppRepository.getInstance().register(username, password, nickname, signature);

            is_registering_ = true;

            user_response.thenAccept(response ->
            {
                if (response.getPayload().success())
                {
                    showAlert(Alert.AlertType.INFORMATION, "注册成功", response.getPayload().message());
                }
                else
                {
                    showAlert(Alert.AlertType.ERROR, "注册失败", response.getPayload().message());
                }
                is_registering_ = false;
            }).exceptionally(ex ->
            {
                showAlert(Alert.AlertType.ERROR, "注册失败", "发生错误: " + ex.getMessage());
                return null;
            });
        }
        catch (Exception e)
        {
            showAlert(Alert.AlertType.ERROR, "注册失败", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alert_type, String title, String content)
    {
        Alert alert = new Alert(alert_type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public StringProperty loginUsernameProperty()
    {
        return login_username_;
    }

    public StringProperty loginPasswordProperty()
    {
        return login_password_;
    }

    public StringProperty registerUsernameProperty()
    {
        return register_username_;
    }

    public StringProperty registerPasswordProperty()
    {
        return register_password_;
    }

    public StringProperty registerNickNameProperty()
    {
        return register_nickname_;
    }

    public StringProperty registerSignatureProperty()
    {
        return register_signature_;
    }

    public BooleanProperty loginButtonDisabledProperty()
    {
        return login_button_disabled_;
    }

    public BooleanProperty registerNextButtonDisabledProperty()
    {
        return register_next_button_disabled_;
    }

    public BooleanProperty registerButtonDisabledProperty()
    {
        return register_button_disabled_;
    }



    public IntegerProperty version_slider_property() {
        return version_slider_property_;
    }



    public boolean isAccept_version_() {
        return accept_version_.get();
    }

    public BooleanProperty accept_version_Property() {
        return accept_version_;
    }
}