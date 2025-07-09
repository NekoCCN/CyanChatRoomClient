package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import cc.nekocc.cyanchatroom.features.chatpage.chatwindow.ChatWindowsController;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class ChatTabViewModel {


    private final ObjectProperty<Parent> chat_window_pane_ = new SimpleObjectProperty<>();
    private final ChatWindowsController chat_windows_controller ;

    public ChatTabViewModel() {
        chat_windows_controller = (ChatWindowsController)ViewTool.loadFXML("fxml/ChatWindow.fxml");
    }



    public ObjectProperty<Parent> getChat_window_pane_() {
        return chat_window_pane_;
    }


    public AnchorPane getChat_window_pane() {
        return chat_windows_controller.getRoot_pane_();
    }
}
