package cc.nekocc.cyanchatroom.features.login;

import cc.nekocc.cyanchatroom.Navigator;
import cc.nekocc.cyanchatroom.domain.client.AbstractClient;
import cc.nekocc.cyanchatroom.domain.client.IndividualClient;
import cc.nekocc.cyanchatroom.model.UserRepository;
import cc.nekocc.cyanchatroom.model.UserSession;
import javafx.beans.property.*;
import javafx.scene.control.Alert;

public class LoginViewModel
{
    private final UserRepository user_repository_;


    private final IntegerProperty version_slider_property_ = new SimpleIntegerProperty(1);
    private final StringProperty login_username_ = new SimpleStringProperty("");
    private final StringProperty login_password_ = new SimpleStringProperty("");

    private final StringProperty register_username_ = new SimpleStringProperty("");
    private final StringProperty register_password_ = new SimpleStringProperty("");
    private final StringProperty register_phone_ = new SimpleStringProperty("");
    private final StringProperty register_address_ = new SimpleStringProperty("");
    private final ObjectProperty<AbstractClient> register_client_type_ = new SimpleObjectProperty<>();

    private final BooleanProperty login_button_disabled_ = new SimpleBooleanProperty(true);
    private final BooleanProperty register_next_button_disabled_ = new SimpleBooleanProperty(true);
    private final BooleanProperty register_button_disabled_ = new SimpleBooleanProperty(true);

    public LoginViewModel()
    {
        this(new UserRepository());
    }

    public LoginViewModel(UserRepository user_repository)
    {
        user_repository_ = user_repository;
        setupBindings();
    }

    private void setupBindings()
    {
        login_button_disabled_.bind(
                login_username_.isEmpty().or(login_password_.isEmpty())
        );

        register_next_button_disabled_.bind(
                register_username_.isEmpty().or(register_password_.isEmpty())
        );

        register_button_disabled_.bind(
                register_phone_.isEmpty()
                        .or(register_address_.isEmpty())
                        .or(version_slider_property_.isNotEqualTo( 200) )
        );
    }

    public void login()
    {
        boolean is_success = user_repository_.login(login_username_.get(), login_password_.get());
        if (is_success)
        {
            UserSession.getInstance().loginUser(user_repository_.findByUsername(login_username_.get()).get());
            Navigator.navigateTo("fxml/turnToChatPage.fxml",840,600,1000);
        }
        else
        {
            showAlert(Alert.AlertType.ERROR, "登录失败", "用户名或密码错误。");
        }
    }

    public void register()
    {
        try
        {
            String username = register_username_.get();
            String password = register_password_.get();
            String phone = register_phone_.get();
            String address = register_address_.get();

//            AbstractClient client_prototype = register_client_type_.get();  因为废除了choicebox故这里要简单修改一下
            AbstractClient client_data =new IndividualClient(username, phone, address);

  /*          if (client_prototype instanceof IndividualClient)
            {
                client_data = new IndividualClient(username, phone, address);
            }
            else if (client_prototype instanceof CorporationClient)
            {
                client_data = new CorporationClient(username, phone, address);
            }
            else
            {
                throw new IllegalStateException("未知的客户类型");
            }

   */
            user_repository_.registerUser(username, password, client_data);
            showAlert(Alert.AlertType.INFORMATION, "注册成功", "用户 " + username + " 已成功注册！");
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

    public StringProperty registerPhoneProperty()
    {
        return register_phone_;
    }

    public StringProperty registerAddressProperty()
    {
        return register_address_;
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
}