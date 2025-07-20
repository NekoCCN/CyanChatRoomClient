package cc.nekocc.cyanchatroom.features.login;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

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
    private TextField register_nickname_;
    @FXML
    private TextField register_signature_;
    @FXML
    private Button register_client_register_button_;
    @FXML
    private Button register_client_back_button_;
    @FXML
    private Label phonenumber_error_;
    @FXML
    private Label verification_message_label_;
    @FXML
    private Slider verification_slider_;

    private LoginViewModel view_model_;

    private Timeline returnAnimation;
    private boolean isAnimating = false;
    private int version_slider_value = 0;
    private BooleanProperty accept_version_value = new SimpleBooleanProperty();

    @Override
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        view_model_ = new LoginViewModel();
        bindControlsToViewModel();
        setupUi();
        setupWarning();
        setupEnter();

    }


    // 检测enter键直接登录
    private void setupEnter(){
        login_password_.setOnKeyPressed(event->{
            if(event.getCode() == KeyCode.ENTER)
                checkLogin();
        });
    }



    private void setupWarning(){  // 检测用户名和密码的输入格式检错
        login_username_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if ((newText.matches("^[a-zA-Z0-9]+$") && newText.length()<= 12) || newText.isEmpty()) {
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
            if ((newText.matches("^[a-zA-Z0-9]+$") && newText.length()<= 12) || newText.isEmpty()) {
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

        register_nickname_.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();  // 获取输入后的完整文本
            if (newText.length()<= 8 || newText.isEmpty()) {
                phonenumber_error_.setVisible(false);
                login_username_.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                return change;
            }
            else{
                phonenumber_error_.setVisible(true);
                login_username_.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return null;  // 拒绝修改
            }
        }));
    }
    


    private void bindControlsToViewModel()
    {
        login_username_.textProperty().bindBidirectional(view_model_.loginUsernameProperty());
        login_password_.passwordProperty().addListener(((observableValue, s, t1) ->{
            view_model_.loginPasswordProperty().set(t1);
        }
        ));
        login_button_.disableProperty().bind(view_model_.loginButtonDisabledProperty());

        register_username_.textProperty().bindBidirectional(view_model_.registerUsernameProperty());
        register_password_.passwordProperty().addListener(((observableValue, s, t1) ->{
            view_model_.registerPasswordProperty().set(t1);
            }
        ));
        register_nickname_.textProperty().bindBidirectional(view_model_.registerNickNameProperty());
        register_signature_.textProperty().bindBidirectional(view_model_.registerSignatureProperty());


        verification_slider_.valueProperty().bindBidirectional(view_model_.version_slider_property());
        register_username_next_button_.disableProperty().bind(view_model_.registerNextButtonDisabledProperty());
        register_client_register_button_.disableProperty().bind(view_model_.registerButtonDisabledProperty());

        accept_version_value.bindBidirectional(view_model_.accept_version_Property());
    }

    private void checkLogin(){
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
    }

    private void setupUi()
    {
        initPasswordFieldStyle();
        initButtonStyle();
        initSliderEvent();
        login_button_.setOnAction(e ->
        {
            checkLogin();
        });
        register_client_register_button_.setOnAction(_ -> view_model_.register());

        login_clear_button_.setOnAction(_ -> {
            view_model_.loginUsernameProperty().set("");
            view_model_.loginPasswordProperty().set("");
        });
        register_username_clear_button_.setOnAction(_ -> {
            view_model_.registerUsernameProperty().set("");
            view_model_.registerPasswordProperty().set("");
        });

        register_user_box_.setVisible(true);
        register_next_box_.setVisible(false);
        register_username_next_button_.setOnAction(_ ->
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
                    reinitSlider();
                switchPanes(register_user_box_, register_next_box_);});
         register_client_back_button_.setOnAction(_ -> switchPanes(register_next_box_, register_user_box_));






    }

    // 重置滑块
    private void reinitSlider() {
        verification_slider_.setValue(0);
        verification_slider_.setDisable(false);
        verification_message_label_.setStyle("-fx-text-fill: #AAAAAA");
        version_slider_value = (int)(Math.random()*160+40);
        verification_message_label_.setText("请滑动到"+String.format("%d",version_slider_value/2)+"%处完成验证");
        accept_version_value.set(false);
    }


    // 初始化滑块格式
    private void initSliderEvent() {

        verification_slider_.setMax(200);
        verification_slider_.setMin(0);
        verification_slider_.setValue(0);
        accept_version_value.set(false);
        // 初始化动画
        returnAnimation = new Timeline(
                new KeyFrame(Duration.millis(20), e -> {
                    if (!isAnimating) return;

                    double currentValue = verification_slider_.getValue();
                    double progress = 0.1; // 调整这个值可以改变返回速度
                    double newValue = currentValue + (2 - currentValue) * progress;
                    verification_slider_.setValue(newValue);
                    // 当接近初始值时停止动画
                    if (Math.abs(newValue - 2) < 0.5) {
                        verification_slider_.setValue(0);
                        isAnimating = false;
                        returnAnimation.stop();
                        verification_slider_.setDisable(false);
                        verification_message_label_.setText("请滑动到"+String.format("%d",version_slider_value/2)+"%处完成验证");
                        verification_message_label_.setStyle("-fx-text-fill: #AAAAAA;");
                    }
                }));

        returnAnimation.setCycleCount(Timeline.INDEFINITE);
        verification_slider_.setPrefSize(200, 40);

        verification_slider_.setOnMouseReleased(event -> {
            if (isAnimating) return; // 如果正在动画中，不处理释放事件
            if (verification_slider_.getValue() >= version_slider_value - 8 && verification_slider_.getValue() <= version_slider_value + 8 && (Math.random() * 10 >= 1)) {
                // 验证成功
                verification_message_label_.setText("验证成功");
                verification_message_label_.setStyle("-fx-text-fill: green;");
                verification_slider_.setDisable(true);
                accept_version_value.set(true);
                isAnimating = false;
            } else {
                // 验证失败，启动返回动画
                verification_message_label_.setText("验证失败");
                verification_message_label_.setStyle("-fx-text-fill: red;");
                isAnimating = true;
                verification_slider_.setDisable(true);
                accept_version_value.set(false);
                returnAnimation.playFromStart();
            }
        });
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
