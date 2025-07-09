package cc.nekocc.cyanchatroom.domain;

import cc.nekocc.cyanchatroom.domain.client.AbstractClient;
import cc.nekocc.cyanchatroom.domain.userstatus.Status;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;

import java.net.URL;


// User我修改了下，可以接入我那个聊天界面

public class User
{
    private String username_;
    private String hashed_password_;
    private boolean is_locked_;
    private AbstractClient client_data_;
    private Status status_;
    private URL avatar_url_;


    public User()
    {
        this.username_ = "示例用户"+(int)(Math.random()*10);
        status_ = Status.getRandomStatus();
    }

    public User(String username, String hashedPassword, AbstractClient clientData)
    {
        username_ = username;
        hashed_password_ = hashedPassword;
        client_data_ = clientData;
        is_locked_ = false;
    }

    public User(String username,  Status status)
    {
        username_ = username;
        is_locked_ = false;
        status_ = status;
    }
    public void setUsername(String username_)
    {
        this.username_ = username_;
    }

    public String getUsername()
    {
        return username_;
    }

    public String getHashedPassword()
    {
        return hashed_password_;
    }

    public AbstractClient getClientData()
    {
        return client_data_;
    }

    public Status getStatus_() {
        return status_;
    }

    public void setStatus_(Status status_) {
        this.status_ = status_;
    }
}
