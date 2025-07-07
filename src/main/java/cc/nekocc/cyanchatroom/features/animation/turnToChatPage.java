package cc.nekocc.cyanchatroom.features.animation;



import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.Navigator;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class turnToChatPage implements Initializable {

    @FXML
    private AnchorPane pack_pane_;
    @FXML
    private ImageView loading_backgound_;
    @FXML
    private Label username_label;
    @FXML
    private Label tip_label_;
    @FXML
    private ImageView loading_icon_;
    @FXML
    private Text loading_text_;
    @FXML
    private Text welcome_text_;

    private RotateTransition rotate;



    private FadeTransition username_animation;

    private final EventHandler<WindowEvent> windowShownHandler = _ -> {
        rotate.playFromStart();
        username_animation.playFromStart();
    };

    public turnToChatPage(){}
    public void initialize(URL var1, ResourceBundle var2) {
        sysnotifydata();
        setupStyle();
        setupResponsiveLayout();
        setupAnimation();
        setupTips();

    }

    // 同步系统数据
    private void sysnotifydata()
    {
        username_label.setText("Geonstar");
        Navigator.getStage().setTitle("欢迎使用聊天室 "+username_label.getText());
    }
    // 样式设置
    private void setupStyle()
    {


        loading_icon_.getStyleClass().addAll(Styles.ACCENT);
        loading_text_.getStyleClass().addAll(Styles.TEXT_NORMAL);
        welcome_text_.getStyleClass().addAll(Styles.TEXT_NORMAL);
        username_label.getStyleClass().addAll(Styles.TEXT_NORMAL);
        tip_label_.getStyleClass().addAll(Styles.TEXT_NORMAL);
    }
    // 小提示
    private void setupTips()
    {
        int tips_index = (int)(Math.random() * 10);
        String tips_text = switch (tips_index) {
            case 0 -> "重构是程序员的主力技能。";
            case 1 -> "普通程序员+google=超级程序员。";
            case 2 -> "好的项目作风硬派，一键测试，一键发布，一键部署。";
            case 3 -> "程序员是写代码的，不是写文档的。";
            case 4 -> "重构/优化/修复Bug，同时只能作一件。";
            case 5 -> "最好的工具是纸笔；其次好的是markdown。";
            case 6 -> "你也许不知道，这个加载动画没有加载任何东西,时间是4.8s。";
            case 7 -> "不知怎么选技术书时就挑薄的。起码不会太贵，且你能看完。";
            case 8 -> "造轮子是很好的锻炼方法。前提是你见过别的轮子。";
            case 9 -> "至少半数时间将花在集成上。时间，时间，时间总是不够。";
            default -> "不要定过大、过远、过细的计划。即使定了也没有用。";
        };
        tip_label_.setText("Tip:"+tips_text);
        tip_label_.getStyleClass().addAll(Styles.TEXT_NORMAL);
    }


    private void setupResponsiveLayout()
    {


        pack_pane_.sceneProperty().addListener((scene_observable, old_scene, new_scene) ->
        {
            if (old_scene == null && new_scene != null)
            {
                new_scene.windowProperty().addListener((window_observable, old_window, new_window) -> {
                    if (old_window == null && new_window != null) {
                        loading_backgound_.fitWidthProperty().bind(pack_pane_.widthProperty());
                        loading_backgound_.fitHeightProperty().bind(pack_pane_.heightProperty());
                    }
                });
            }
        });
    }
    private void setupAnimation(){

        rotate = new RotateTransition(Duration.seconds(1), loading_icon_);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setDuration(Duration.millis(800));
        rotate.setCycleCount(6);
        rotate.setOnFinished(_ -> {
            Navigator.navigateTo("fxml/ChatPage.fxml", Navigator.AnimationType.FADE);
            Navigator.getStage().getScene().getWindow().removeEventHandler(WindowEvent.WINDOW_SHOWN, windowShownHandler);
          //  Navigator.getStage().setResizable(true);  现在窗口大小的调节还是有问题，先关掉了
        });
        username_animation = new FadeTransition(Duration.millis(1000),username_label);
        username_animation.setFromValue(0);
        username_animation.setToValue(1);
        Navigator.getStage().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_SHOWN, windowShownHandler);

    }


}
