package cc.nekocc.cyanchatroom.features.animation;



import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class turnToChatPage implements Initializable {

    @FXML
    private AnchorPane stack_pane_;
    @FXML
    private Rectangle stack_rect_;
    @FXML
    private Label welcome_label;
    @FXML
    private Label username_label;

    private Timeline welcome_animation;
    public turnToChatPage(){}


    public void initialize(URL var1, ResourceBundle var2) {

        setupResponsiveLayout();
    }

    private void setupResponsiveLayout()
    {
        username_label.setText("示例");
        stack_pane_.sceneProperty().addListener((scene_observable, old_scene, new_scene) ->
        {
            if (old_scene == null && new_scene != null)
            {
                new_scene.windowProperty().addListener((window_observable, old_window, new_window) -> {
                    if (old_window == null && new_window != null) {
                        stack_rect_.widthProperty().bind(stack_pane_.widthProperty());
                        stack_rect_.heightProperty().bind(stack_pane_.heightProperty());
                    }
                });
            }
        });
    }/*
    private void setupAnimation(){
        username_label.setText("示例");
        welcome_animation = new Timeline(
                new KeyFrame(Duration.ZERO,e->{
                    new KeyValue(welcome_label.rotateProperty(),0);
                }),
                new KeyFrame(Duration.millis(50),e->{
                    new KeyValue(welcome_label.rotateProperty(),180, Interpolator.EASE_BOTH);
                }),
                new KeyFrame(Duration.millis(100),e->{
                    new KeyValue(welcome_label.rotateProperty(),360, Interpolator.EASE_BOTH);
                })
        );
        welcome_animation.setDelay(Duration.seconds(10));
        FadeTransition username_animation = new FadeTransition(Duration.millis(2000),username_label);
        welcome_animation.setCycleCount(20);
        welcome_animation.play();
    }

*/
}
