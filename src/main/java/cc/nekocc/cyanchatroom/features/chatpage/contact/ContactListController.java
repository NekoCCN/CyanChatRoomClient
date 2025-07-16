package cc.nekocc.cyanchatroom.features.chatpage.contact;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ContactListController implements Initializable {

    @FXML
    private AnchorPane contact_tree_;
    @FXML
    private TreeView root_list_;

    private final ContactListViewModel view_model_ = new ContactListViewModel();
    private final TreeItem<String> contact_root = new TreeItem<>("联系人");
    private final TreeItem<String> group_root  = new TreeItem<>("群聊");
    private final TreeItem<String> Online_root = new TreeItem<>("");
    private final TreeItem<String> Busy_root = new TreeItem<>("");
    private final TreeItem<String> Away_root = new TreeItem<>("");
    private final TreeItem<String> Do_not_disturb_root = new TreeItem<>("");
    private final TreeItem<String> Offline_root = new TreeItem<>("");
    private final TreeItem<String> Black_root = new TreeItem<>("");




    public ContactListController() {}
    public void initialize(URL url, ResourceBundle resource_bundle) {
        setupList();
        setupStyle();

    }

    private void setupStyle(){
        AnchorPane.setBottomAnchor(contact_tree_,0.0);
        AnchorPane.setTopAnchor(contact_tree_,0.0);
        AnchorPane.setLeftAnchor(contact_tree_,0.0);
        AnchorPane.setRightAnchor(contact_tree_,0.0);
    }


    private void setupList() {
        TreeItem<String> root = new TreeItem<>();
        root_list_.setRoot(root);
        root.getChildren().addAll(contact_root,group_root);
        contact_root.getChildren().addAll(Online_root,Busy_root,Away_root,Do_not_disturb_root,Offline_root,Black_root);
        Text online = new Text("在线");
        online.setFill(Color.web(Status.ONLINE.getColor(),1.0));
        Online_root.setGraphic(online);
        Text busy = new Text("忙碌");
        busy.setFill(Color.web(Status.BUSY.getColor(),1.0));
        Busy_root.setGraphic(busy);
        Text away = new Text("离开");
        away.setFill(Color.web(Status.AWAY.getColor(),1.0));
        Away_root.setGraphic(away);
        Text do_not_disturb = new Text("勿打扰");
        do_not_disturb.setFill(Color.web(Status.DO_NOT_DISTURB.getColor(),1.0));
        Do_not_disturb_root.setGraphic(do_not_disturb);
        Text offline = new Text("离线");
        offline.setFill(Color.web(Status.OFFLINE.getColor(),1.0));
        Offline_root.setGraphic(offline);
        Text black = new Text("黑名单");
        black.setFill(Color.rgb(214,77,91));
        Black_root.setGraphic(black);

    }


    public AnchorPane getRootPane(){
        return contact_tree_;
    }

    public void clearContactList() {
        Online_root.getChildren().clear();
        Busy_root.getChildren().clear();
        Away_root.getChildren().clear();
        Do_not_disturb_root.getChildren().clear();
        Offline_root.getChildren().clear();
        Black_root.getChildren().clear();
    }

    public void addContact(String username,Status user) {
        switch (user) {
            case ONLINE -> view_model_.syncContact(Online_root,username);
            case BUSY -> view_model_.syncContact(Busy_root,username);
            case AWAY -> view_model_.syncContact(Away_root,username);
            case DO_NOT_DISTURB -> view_model_.syncContact(Do_not_disturb_root, username);
            case OFFLINE -> view_model_.syncContact(Offline_root, username);


        }


    }

}
