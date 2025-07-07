package cc.nekocc.cyanchatroom.features.animation;



import cc.nekocc.cyanchatroom.Navigator;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.animation.RotateTransition;

import java.net.URL;
import java.util.ResourceBundle;

public class turnToChatPage implements Initializable {

    @FXML
    private Pane pack_pane_;
    @FXML
    private Rectangle stack_rect_;
    @FXML
    private Label welcome_label;
    @FXML
    private Label username_label;
    private RotateTransition rotate;
    private FadeTransition username_animation;

    private final EventHandler<WindowEvent> windowShownHandler = _ -> {
        rotate.playFromStart();
        username_animation.playFromStart();
    };

    public turnToChatPage(){}
    public void initialize(URL var1, ResourceBundle var2) {
        setupResponsiveLayout();
        setupAnimation();
    }

    private void setupResponsiveLayout()
    {

        username_label.setText("示例");
        pack_pane_.sceneProperty().addListener((scene_observable, old_scene, new_scene) ->
        {
            if (old_scene == null && new_scene != null)
            {
                new_scene.windowProperty().addListener((window_observable, old_window, new_window) -> {
                    if (old_window == null && new_window != null) {
                        stack_rect_.widthProperty().bind(pack_pane_.widthProperty());
                        stack_rect_.heightProperty().bind(pack_pane_.heightProperty());
                    }
                });
            }
        });
    }
    private void setupAnimation(){
        username_label.setText("示例");
        rotate = new RotateTransition(Duration.seconds(1), welcome_label);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setDuration(Duration.millis(300));
        rotate.setCycleCount(12);
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        rotate.setOnFinished(_ ->{
            Navigator.navigateTo("fxml/ChatPage.fxml",Navigator.AnimationType.FADE);
            Navigator.getStage().getScene().getWindow().removeEventHandler(WindowEvent.WINDOW_SHOWN,windowShownHandler);
            Navigator.getStage().setResizable(true);
        });
        username_animation = new FadeTransition(Duration.millis(1000),username_label);
        username_animation.setFromValue(0);
        username_animation.setToValue(1);
        Navigator.getStage().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_SHOWN, windowShownHandler);

    }


}
