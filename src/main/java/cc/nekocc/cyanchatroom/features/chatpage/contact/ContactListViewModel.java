package cc.nekocc.cyanchatroom.features.chatpage.contact;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.function.BiConsumer;

public class ContactListViewModel
{
    private final ObservableList<String> online_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> busy_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> away_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> do_not_disturb_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> offline_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> group_contacts_ = FXCollections.observableArrayList();

    private final StringProperty selected_contact_ = new SimpleStringProperty();
    private BiConsumer<String, TreeItem<String>> on_data_ready_callback_;

    public void addOrUpdateContact(String username, Status status)
    {
        removeContact(username);
        switch (status)
        {
            case ONLINE -> online_contacts_.add(username);
            case BUSY -> busy_contacts_.add(username);
            case AWAY -> away_contacts_.add(username);
            case DO_NOT_DISTURB -> do_not_disturb_contacts_.add(username);
            case OFFLINE -> offline_contacts_.add(username);
        }
    }

    private void removeContact(String username)
    {
        online_contacts_.remove(username);
        busy_contacts_.remove(username);
        away_contacts_.remove(username);
        do_not_disturb_contacts_.remove(username);
        offline_contacts_.remove(username);
    }

    public void addGroupContact(String groupName)
    {
        if (!group_contacts_.contains(groupName))
        {
            group_contacts_.add(groupName);
        }
    }

    public ObservableList<String> getGroupContacts()
    {
        return group_contacts_;
    }

    public void clearContacts()
    {
        online_contacts_.clear();
        busy_contacts_.clear();
        away_contacts_.clear();
        do_not_disturb_contacts_.clear();
        offline_contacts_.clear();
        group_contacts_.clear();
    }

    public ObservableList<String> getOnlineContacts()
    {
        return online_contacts_;
    }

    public ObservableList<String> getBusyContacts()
    {
        return busy_contacts_;
    }

    public void onSelectedContact(TreeItem<String> username)
    {
        System.out.println("Selected contact: " + username.getValue());
        if (on_data_ready_callback_ != null && username != null)
        {
            on_data_ready_callback_.accept(username.getValue(), username);
        }
    }

    public void setOnDataReadyCallback(BiConsumer<String, TreeItem<String>> callback)
    {
        this.on_data_ready_callback_ = callback;
    }

    public ObservableList<String> getAwayContacts()
    {
        return away_contacts_;
    }

    public ObservableList<String> getDoNotDisturbContacts()
    {
        return do_not_disturb_contacts_;
    }

    public ObservableList<String> getOfflineContacts()
    {
        return offline_contacts_;
    }
}