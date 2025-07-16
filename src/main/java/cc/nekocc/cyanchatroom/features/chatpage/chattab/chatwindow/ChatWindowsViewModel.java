package cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow;


import java.util.UUID;

public class ChatWindowsViewModel {

    private UUID opposite_id;
    private String opposite_username = " ";
    public ChatWindowsViewModel( ){}
    public String getUserName() {
        return opposite_username;
    }


    public void setUserName(String username_) {
        this.opposite_username = username_;
    }


    public UUID getOppositeID() {
        return opposite_id;
    }

    public void setOppositeID(UUID opposite_id) {
        this.opposite_id = opposite_id;
    }
}
