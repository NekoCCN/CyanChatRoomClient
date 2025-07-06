package cc.nekocc.cyanchatroom.features.login;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import cc.nekocc.cyanchatroom.domain.client.AbstractClient;
import cc.nekocc.cyanchatroom.domain.client.CorporationClient;
import cc.nekocc.cyanchatroom.domain.client.IndividualClient;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable
{
    @FXML
    private Label username_warning_message_;
    @FXML
    private Label password_warning_message_;
    @FXML
    private Tab login_tab_;
    @FXML
    private Tab register_tab_;
    @FXML
    private TextField login_username_;
    @FXML
    private PasswordTextField login_password_;
    @FXML
    private Label username_register_error_;
    @FXML
    private Label password_register_error_;
    @FXML
    private Button login_button_;
    @FXML
    private Button login_clear_button_;
    @FXML
    private StackPane register_stack_pane_;
    @FXML
    private VBox register_user_box_;
    @FXML
    private TextField register_username_;
    @FXML
    private PasswordTextField register_password_;
    @FXML
    private Button register_username_next_button_;
    @FXML
    private Button register_username_clear_button_;
    @FXML
    private VBox register_next_box_;
    @FXML
    private TextField register_phone_;
    @FXML
    private TextField register_address_;
    @FXML
    private ChoiceBox<AbstractClient> register_client_type_;
    @FXML
    private Button register_client_register_button_;
    @FXML
    private Button register_client_back_button_;
    @FXML
    private Label phonenumber_error_;

    private LoginViewModel view_model_;

    @Override
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        view_model_ = new LoginViewModel();
        bindControlsToViewModel();
        setupUi();
        setupWarning();
    }



    private void setupWarning(){  // 检测用户名和密码的输入格式检错
        login_username_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if (newText.matches("^[a-zA-Z0-9]+$") || newText.isEmpty()) {
                username_warning_message_.setVisible(false);
                login_username_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                return change;
            }
            else{
            username_warning_message_.setVisible(true);
            login_username_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            return null;  // 拒绝修改
                }
        }));
        register_username_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if (newText.matches("^[a-zA-Z0-9]+$") || newText.isEmpty()) {
                username_register_error_.setVisible(false);
                register_username_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                return change;
            }
            else{
                username_register_error_.setVisible(true);
                register_username_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return null;  // 拒绝修改
            }
        }));

        register_phone_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if (newText.matches("^[0-9]+$") || newText.isEmpty()) {
                phonenumber_error_.setVisible(false);
                register_phone_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                return change;
            }
            else{
                phonenumber_error_.setVisible(true);
                register_phone_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return null;  // 拒绝修改
            }
        }));

    }
    


    private void bindControlsToViewModel()
    {
        login_username_.textProperty().bindBidirectional(view_model_.loginUsernameProperty());
        login_password_.textProperty().bindBidirectional(view_model_.loginPasswordProperty());
        login_button_.disableProperty().bind(view_model_.loginButtonDisabledProperty());

        register_username_.textProperty().bindBidirectional(view_model_.registerUsernameProperty());
        register_password_.textProperty().bindBidirectional(view_model_.registerPasswordProperty());
        register_phone_.textProperty().bindBidirectional(view_model_.registerPhoneProperty());
        register_address_.textProperty().bindBidirectional(view_model_.registerAddressProperty());
        view_model_.registerClientTypeProperty().bind(register_client_type_.getSelectionModel().selectedItemProperty());

        register_username_next_button_.disableProperty().bind(view_model_.registerNextButtonDisabledProperty());
        register_client_register_button_.disableProperty().bind(view_model_.registerButtonDisabledProperty());
    }

    private void setupUi()
    {
        initPasswordFieldStyle();
        initButtonStyle();
        populateClientTypeChoiceBox();

        login_button_.setOnAction(e ->
        {

            String newText = login_password_.getPassword();  // 获取输入后的完整文本
            if (newText.matches("^[a-zA-Z0-9!.,@#$%^&*]+$") || newText.isEmpty())
            {
                if (login_password_.getPassword().length() > 5 && login_password_.getText().length() < 19)
                    password_warning_message_.setVisible(false);
            } else
            {
                password_warning_message_.setText("密码不能包含非法字符");
                password_warning_message_.setVisible(true);
                login_password_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
            }

            if (login_password_.getPassword().length() < 6)
            {
                password_warning_message_.setVisible(true);
                password_warning_message_.setText("密码长度不能小于6位。");
                login_password_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
            } else if (login_password_.getPassword().length() > 20)
            {
                password_warning_message_.setVisible(true);
                password_warning_message_.setText("密码长度不能多于20位。");
                login_password_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
            } else
            {
                password_warning_message_.setVisible(false);
                login_password_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            }

            view_model_.login();
        });
        register_client_register_button_.setOnAction(e -> view_model_.register());

        login_clear_button_.setOnAction(e -> {
            view_model_.loginUsernameProperty().set("");
            view_model_.loginPasswordProperty().set("");
        });
        register_username_clear_button_.setOnAction(e -> {
            view_model_.registerUsernameProperty().set("");
            view_model_.registerPasswordProperty().set("");
        });

        register_user_box_.setVisible(true);
        register_next_box_.setVisible(false);
        register_username_next_button_.setOnAction(e ->
                {
                    String newText = register_password_.getPassword();  // 获取输入后的完整文本
                    if (newText.matches("^[a-zA-Z0-9!.,@#$%^&*]+$") || newText.isEmpty())
                    {
                        if (register_password_.getPassword().length() > 6 && register_password_.getText().length() < 20)
                            password_register_error_.setVisible(false);
                    } else
                    {
                        password_register_error_.setText("密码不能包含非法字符");
                        password_register_error_.setVisible(true);
                        register_password_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                        return;
                    }

                    if (register_password_.getPassword().length() < 6)
                    {
                        password_register_error_.setVisible(true);
                        password_register_error_.setText("密码长度不能小于6位。");
                        register_password_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                        return;
                    } else if (register_password_.getPassword().length() > 20)
                    {
                        password_register_error_.setVisible(true);
                        password_register_error_.setText("密码长度不能多于20位。");
                        register_password_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                        return;
                    } else
                    {
                        password_register_error_.setVisible(false);
                        register_password_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                    }
                switchPanes(register_user_box_, register_next_box_);});
        register_client_back_button_.setOnAction(e -> switchPanes(register_next_box_, register_user_box_));
    }

    private void populateClientTypeChoiceBox()
    {
        register_client_type_.getItems().addAll(new IndividualClient(), new CorporationClient());
        register_client_type_.setConverter(new StringConverter<>()
        {
            @Override
            public String toString(AbstractClient client)
            {
                return switch (client)
                {
                    case null -> null;
                    case IndividualClient individualClient -> "个人客户";
                    case CorporationClient corporationClient -> "公司客户";
                    default -> client.getClass().getSimpleName();
                };
            }

            @Override
            public AbstractClient fromString(String string)
            {
                return null;
            }
        });
        register_client_type_.getSelectionModel().selectFirst();
    }

    private void initPasswordFieldStyle()
    {
        var icon_1 = new FontIcon(Feather.EYE_OFF);
        icon_1.setCursor(Cursor.HAND);
        icon_1.setOnMouseClicked(e -> {
            icon_1.setIconCode(login_password_.getRevealPassword() ? Feather.EYE_OFF : Feather.EYE);
            login_password_.setRevealPassword(!login_password_.getRevealPassword());
        });
        login_password_.setRight(icon_1);

        var icon_2 = new FontIcon(Feather.EYE_OFF);
        icon_2.setCursor(Cursor.HAND);
        icon_2.setOnMouseClicked(e -> {
            icon_2.setIconCode(register_password_.getRevealPassword() ? Feather.EYE_OFF : Feather.EYE);
            register_password_.setRevealPassword(!register_password_.getRevealPassword());
        });
        register_password_.setRight(icon_2);
    }

    private void initButtonStyle()
    {
        login_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        login_button_.setMnemonicParsing(true);

        login_clear_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        login_clear_button_.setMnemonicParsing(true);

        register_username_next_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        register_username_next_button_.setMnemonicParsing(true);

        register_username_clear_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        register_username_clear_button_.setMnemonicParsing(true);

        register_client_back_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        register_client_back_button_.setMnemonicParsing(true);

        register_client_register_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        register_client_register_button_.setMnemonicParsing(true);
    }

    private void switchPanes(Node pane_to_fade_out, Node pane_to_fade_in)
    {
        Duration duration = Duration.millis(500);
        Timeline fade_out_animation = Animations.fadeOutRight(pane_to_fade_out, duration);
        Timeline fade_in_animation = Animations.fadeInLeft(pane_to_fade_in, duration);
        fade_out_animation.setOnFinished(event ->
        {
            pane_to_fade_out.setVisible(false);
            fade_in_animation.play();
        });
        pane_to_fade_in.setOpacity(0);
        pane_to_fade_in.setVisible(true);
        pane_to_fade_in.toFront();
        fade_out_animation.play();
    }
}
