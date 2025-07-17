package cc.nekocc.cyanchatroom.features.chatpage.contact;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContactListViewModel
{
    private final ObservableList<String> online_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> busy_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> away_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> do_not_disturb_contacts_ = FXCollections.observableArrayList();
    private final ObservableList<String> offline_contacts_ = FXCollections.observableArrayList();

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

    public ObservableList<String> getOnlineContacts()
    {
        return online_contacts_;
    }

    public ObservableList<String> getBusyContacts()
    {
        return busy_contacts_;
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