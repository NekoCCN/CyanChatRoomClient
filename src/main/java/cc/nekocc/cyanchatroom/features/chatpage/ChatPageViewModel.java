package cc.nekocc.cyanchatroom.features.chatpage;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatPageViewModel {


    private final StringProperty username_title_property_ = new SimpleStringProperty();



    public ChatPageViewModel(){
        initialize();
    }



    private void initialize(){

    }
    public StringProperty getUsername_title_property() {
        return username_title_property_;
    }
}
