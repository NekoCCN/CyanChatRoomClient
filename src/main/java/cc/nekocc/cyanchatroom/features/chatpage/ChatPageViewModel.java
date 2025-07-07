package cc.nekocc.cyanchatroom.features.chatpage;


import cc.nekocc.cyanchatroom.domain.chatwindow.ChatTab;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


import java.util.ArrayList;

public class ChatPageViewModel {

    private ArrayList<ChatTab> user_list_ = new ArrayList<>();
    private final StringProperty username_title_property_ = new SimpleStringProperty();





    public ChatPageViewModel(){
        initialize();
    }


    private void initialize(){
        for(int i = 0;  i <10 ;i++)
            user_list_.add(new ChatTab());

    }
    public StringProperty getUsername_title_property() {
        return username_title_property_;
    }

    public ArrayList<ChatTab> getUser_list_() {
        return user_list_;
    }

    public void setUser_list_(ArrayList<ChatTab> user_list_) {
        this.user_list_ = user_list_;
    }
}
