package cc.nekocc.cyanchatroom;

import javafx.application.Application;
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
        Navigator.navigateTo("fxml/Login.fxml");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}