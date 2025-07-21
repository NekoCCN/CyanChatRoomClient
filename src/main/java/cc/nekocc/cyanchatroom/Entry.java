package cc.nekocc.cyanchatroom;

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Entry extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        Application.setUserAgentStylesheet(new atlantafx.base.theme.PrimerLight().getUserAgentStylesheet());
        stage.setTitle("Hello!");
        Navigator.setPrimaryStage(stage);
        Navigator.navigateTo("fxml/LoginServer.fxml");


        stage.setOnCloseRequest(event->{
            Alert alert = ViewTool.showAlert(Alert.AlertType.CONFIRMATION,"提示","确认要退出吗",false);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.YES)
                {
                    AppRepository.getInstance().disconnect();
                    Platform.exit();
                    System.exit(0);
                }
                else{
                    event.consume();
                }

            });
        });
        stage.setResizable(false);
        stage.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/ICON.png")))));
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}