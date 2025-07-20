package cc.nekocc.cyanchatroom.features.chatpage.contact;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ContactListController implements Initializable
{

    @FXML
    private AnchorPane contact_tree_;
    @FXML
    private TreeView<String> root_list_;

    private ContactListViewModel view_model_;
    private final TreeItem<String> contact_root_ = new TreeItem<>("好友");
    private final TreeItem<String> group_root_ = new TreeItem<>("");
    private final TreeItem<String> online_root_ = new TreeItem<>("");
    private final TreeItem<String> busy_root_ = new TreeItem<>("");
    private final TreeItem<String> away_root_ = new TreeItem<>("");
    private final TreeItem<String> do_not_disturb_root_ = new TreeItem<>("");
    private final TreeItem<String> offline_root_ = new TreeItem<>("");


    public void setViewModel(ContactListViewModel viewModel)
    {
        this.view_model_ = viewModel;
        bindLists();
    }

    @Override
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        setupList();
        setupStyle();
    }

    private void setupStyle()
    {
        AnchorPane.setBottomAnchor(contact_tree_, 0.0);
        AnchorPane.setTopAnchor(contact_tree_, 0.0);
        AnchorPane.setLeftAnchor(contact_tree_, 0.0);
        AnchorPane.setRightAnchor(contact_tree_, 0.0);
    }

    private void setupList()
    {
        TreeItem<String> root = new TreeItem<>();
        root_list_.setRoot(root);
        root.getChildren().addAll(contact_root_, group_root_);
        contact_root_.setExpanded(true);
        contact_root_.getChildren().addAll(online_root_, busy_root_, away_root_, do_not_disturb_root_, offline_root_);
        online_root_.setGraphic(createStatusText("在线", Status.ONLINE.getColor()));
        busy_root_.setGraphic(createStatusText("忙碌", Status.BUSY.getColor()));
        away_root_.setGraphic(createStatusText("离开", Status.AWAY.getColor()));
        do_not_disturb_root_.setGraphic(createStatusText("勿打扰", Status.DO_NOT_DISTURB.getColor()));
        offline_root_.setGraphic(createStatusText("离线", Status.OFFLINE.getColor()));

        root_list_.setOnMouseClicked(_ -> {
                TreeItem<String> selectedItem = root_list_.getSelectionModel().getSelectedItem();
                if (selectedItem != null &&
                        (selectedItem.getParent() != contact_root_ &&
                                selectedItem.getParent() != group_root_) && selectedItem != contact_root_ && selectedItem != group_root_) {
                    view_model_.onSelectedContact(selectedItem);
                }
        });
    }

    private void bindLists()
    {
        bindListToTreeItem(view_model_.getOnlineContacts(), online_root_);
        bindListToTreeItem(view_model_.getBusyContacts(), busy_root_);
        bindListToTreeItem(view_model_.getAwayContacts(), away_root_);
        bindListToTreeItem(view_model_.getDoNotDisturbContacts(), do_not_disturb_root_);
        bindListToTreeItem(view_model_.getOfflineContacts(), offline_root_);
    }

    private void bindListToTreeItem(ObservableList<String> list, TreeItem<String> treeItem)
    {
        treeItem.getChildren().setAll(list.stream().map(TreeItem::new).collect(Collectors.toList()));

        list.addListener((ListChangeListener<String>) c ->
        {
            while (c.next())
            {
                if (c.wasAdded())
                {

                    c.getAddedSubList().forEach(name -> {
                        TreeItem<String> item = new TreeItem<>(name);
                        treeItem.getChildren().add(item);
                    });
                }
                if (c.wasRemoved())
                {
                    c.getRemoved().forEach(name -> treeItem.getChildren().removeIf(item -> item.getValue().equals(name)));
                }
            }
        });
    }

    private Text createStatusText(String text, String color)
    {
        Text statusText = new Text(text);
        statusText.setFill(Color.web(color));
        return statusText;
    }
}