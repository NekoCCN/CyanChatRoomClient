package cc.nekocc.cyanchatroom.features.chatpage.contact;

import javafx.scene.control.TreeItem;

public class ContactListViewModel {


    public void syncContact(TreeItem root ,String username) {
        root.getChildren().add(new TreeItem<>(username));

    }



}
