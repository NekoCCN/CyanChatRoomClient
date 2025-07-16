package cc.nekocc.cyanchatroom;

import javafx.application.Application;
import javafx.application.Platform;
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
        stage.setOnCloseRequest(_->{
            Platform.exit();
            System.exit(0);
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