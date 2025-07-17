package cc.nekocc.cyanchatroom.Buffer;

import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow.ChatWindowsController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class Buffer {

    /**
     * 比较乱
     * 这是个缓存池
     * 其中比较重要的节点数据，例如用户的Tab标签栏的VBox,当前的聊天窗口，最后的聊天窗口都在里面
     * current和last的在这里是为了实现从好友列表到聊天主页面的聊天窗口回到上一个聊天窗口
     */


    public static ArrayList<ChatWindowsController> chat_window_list_ = new ArrayList<>();
    public static ArrayList<ChatTabController> user_tab_list_ = new ArrayList<>();
    public static SimpleObjectProperty<AnchorPane> chat_windows_contain_ = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<VBox> user_list_box_ = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<ChatWindowsController> current_chat_window_ = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<ChatWindowsController> last_window_ = new SimpleObjectProperty<>();




    // 刷新用户标签背景，具体表现在我在点击另一个用户标签时，上一个用户标签的背景颜色会变回默认颜色
    // 不知道这个方法可不可以放在这里
    public static void rewriteUserActive(){
        for(ChatTabController user : user_tab_list_){
            if(user.isActive()) {
                user.setIsActive(false);
                user.getUserTab().setStyle("-fx-background-color: transparent");
            }
        }
    }




}
