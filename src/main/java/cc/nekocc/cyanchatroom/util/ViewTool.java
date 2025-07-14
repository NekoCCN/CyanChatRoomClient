package cc.nekocc.cyanchatroom.util;


import cc.nekocc.cyanchatroom.Navigator;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

public class ViewTool {



    public static FXMLLoader loadFXML(String fxml_file)
    {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Navigator.class.getResource(fxml_file)));
            loader.load();
            return loader;
        } catch (Exception e) {
            System.err.println("无法加载页面: " + fxml_file);
            e.printStackTrace();
            return null;
        }

    }

    public static String getImagePath(String image_name)
    {
        return Objects.requireNonNull(ViewTool.class.getResource("/Image/" + image_name)).toExternalForm();
    }

    public static StackPane getDefaultAvatar(String name){
        Label username_label_title_ = new Label(String.valueOf(name.charAt(0)));
        Circle circle = new Circle();
        StackPane avatar = new StackPane(circle,username_label_title_);
        avatar.setPrefHeight(Control.USE_COMPUTED_SIZE);
        avatar.setPrefWidth(Control.USE_COMPUTED_SIZE);
        circle.setStyle("-fx-fill: #dbdbdb;");
        circle.setCenterX(40);
        circle.setRadius(22);
        circle.setCenterY(45);
        username_label_title_.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD,20));
        username_label_title_.setStyle("-fx-text-fill: #57606A");
        username_label_title_.setLayoutX(10);
        username_label_title_.setLayoutY(40);
        return avatar;
    }
    public static void showAlert(Alert.AlertType alert_type, String title, String content)
    {
        Alert alert = new Alert(alert_type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}