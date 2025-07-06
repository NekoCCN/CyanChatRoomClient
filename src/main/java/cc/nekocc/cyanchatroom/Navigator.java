package cc.nekocc.cyanchatroom;

import animatefx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Navigator
{
    private static Stage primary_stage_;

    public enum AnimationType
    {
        FADE,
        SLIDE_LEFT,
        SLIDE_RIGHT,
        SLIDE_UP,
        SLIDE_DOWN,
        ZOOM,
        FLIP,
        JACK_IN_THE_BOX
    }

    public static void setPrimaryStage(Stage stage)
    {
        primary_stage_ = stage;
    }

    public static void navigateTo(String fxml_file)
    {
        navigateTo(fxml_file, AnimationType.FADE);
    }

    public static void navigateToSlideLeft(String fxml_file)
    {
        navigateTo(fxml_file, AnimationType.SLIDE_LEFT);
    }

    public static void navigateToSlideRight(String fxml_file)
    {
        navigateTo(fxml_file, AnimationType.SLIDE_RIGHT);
    }

    public static void navigateToSlideUp(String fxml_file)
    {
        navigateTo(fxml_file, AnimationType.SLIDE_UP);
    }

    public static void navigateToFlip(String fxml_file)
    {
        navigateTo(fxml_file, AnimationType.FLIP);
    }

    public static void navigateTo(String fxml_file, AnimationType animation_type)
    {
        AnimationFX out_animation;
        AnimationFX in_animation = switch (animation_type)
        {
            case SLIDE_LEFT ->
            {
                out_animation = new SlideOutLeft();
                yield new SlideInRight();
            }
            case SLIDE_RIGHT ->
            {
                out_animation = new SlideOutRight();
                yield new SlideInLeft();
            }
            case SLIDE_UP ->
            {
                out_animation = new SlideOutUp();
                yield new SlideInUp();
            }
            case SLIDE_DOWN ->
            {
                out_animation = new SlideOutDown();
                yield new SlideInDown();
            }
            case ZOOM ->
            {
                out_animation = new ZoomOut();
                yield new ZoomIn();
            }
            case FLIP ->
            {
                out_animation = new FlipOutY();
                yield new FlipInY();
            }
            case JACK_IN_THE_BOX ->
            {
                out_animation = new FadeOut();
                yield new JackInTheBox();
            }
            default ->
            {
                out_animation = new FadeOut();
                yield new FadeIn();
            }
        };

        transitionTo(fxml_file, out_animation, in_animation);
    }

    private static void transitionTo(String fxml_file, AnimationFX out_animation, AnimationFX in_animation)
    {
        try
        {
            Parent new_root = FXMLLoader.load(Objects.requireNonNull(Navigator.class.getResource(fxml_file)));
            Scene current_scene = primary_stage_.getScene();

            if (current_scene == null)
            {
                primary_stage_.setScene(new Scene(new_root));
                primary_stage_.show();
                return;
            }

            out_animation.setNode(current_scene.getRoot());
            out_animation.setOnFinished(_ ->
            {
                Scene new_scene = new Scene(new_root, current_scene.getWidth(), current_scene.getHeight());

                in_animation.setNode(new_root);
                in_animation.play();

                primary_stage_.setScene(new_scene);
            });
            out_animation.play();
        }
        catch (IOException e)
        {
            System.err.println("无法加载页面: " + fxml_file);
            e.printStackTrace();
        }
    }
}

