package cc.nekocc.cyanchatroom.features.chatpage.contact;

import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.features.chatpage.chattab.ChatTabController;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;

public class ContactListViewModel {


    public void syncContact(TreeItem root , ChatTabController tab) {
        root.getChildren().add(new TreeItem<String>(tab.getUser().getUsername()));

    }



}
