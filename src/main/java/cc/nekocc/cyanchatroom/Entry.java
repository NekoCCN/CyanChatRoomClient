package cc.nekocc.cyanchatroom;

//import javafx.application.Application;
//import javafx.scene.image.Image;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class Entry extends Application
//{
//    @Override
//    public void start(Stage stage) throws IOException
//    {
//        Application.setUserAgentStylesheet(new atlantafx.base.theme.PrimerLight().getUserAgentStylesheet());
//        stage.setTitle("Hello!");
//        Navigator.setPrimaryStage(stage);
//        Navigator.navigateTo("fxml/Login.fxml");
//        stage.setResizable(false);
//        stage.getIcons().add((new Image(String.valueOf(getClass().getResource("/Image/ICON.png")))));
//        stage.show();
//    }
//
//    public static void main(String[] args)
//    {
//        launch();
//    }
//}

import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.model.entity.User;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Entry extends Application
{
    @Override
    public void start(Stage primary_stage)
    {
        AppRepository server = AppRepository.getInstance();


        server.connectionStatusProperty().addListener((obs, old_status, new_status) -> {
            System.out.println("连接状态变更: " + new_status);

            if (new_status == AppRepository.ConnectionStatus.CONNECTED) {
                System.out.println("连接成功，正在尝试登录...");
                var user = server.login("Neko", "143150151");

                user.thenAccept(result -> {
                    if (result != null) {
                        System.out.println("登录成功！用户: " + result);
                        server.setCurrentUser(new User(result.getPayload().user()));
                    } else {
                        System.err.println("登录失败，用户名或密码错误。");
                    }

                    server.getUserDetails(result.getPayload().user().id()).thenAccept(user_details -> {
                        if (user_details != null) {
                            System.out.println("用户详情: " + user_details.getPayload().signature());
                        } else {
                            System.err.println("获取用户详情失败。");
                        }
                    }).exceptionally(ex -> {
                        System.err.println("获取用户详情时发生异常: " + ex.getMessage());
                        return null;
                    });

                    server.sendMessage("USER", UUID.fromString("0197f803-9a96-7cd0-8482-234162984117"
                    ), "TEXT", false, "Hello, this is a test message!");

                }).exceptionally(ex -> {
                    System.err.println("登录过程中发生异常: " + ex.getMessage());
                    return null;
                });


            }
        });

        server.currentUserProperty().addListener((obs, old_user, new_user) -> {
            if (new_user != null) {
                System.out.println("登录成功！用户: " + new_user);
            }
        });

        server.lastErrorMessageProperty().addListener((obs, old_msg, new_msg) -> {
            if (new_msg != null && !new_msg.isEmpty()) {
                System.err.println("收到错误信息: " + new_msg);
            }
        });

        System.out.println("正在连接到服务器...");
        server.connectToServer("localhost:23333");


    }

    public static void main(String... args) {
        launch(args);
    }
}