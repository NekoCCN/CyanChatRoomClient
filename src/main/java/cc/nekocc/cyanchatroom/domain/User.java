package cc.nekocc.cyanchatroom.domain;

import cc.nekocc.cyanchatroom.domain.client.AbstractClient;

public class User
{
    private String username_;
    private String hashed_password_;
    private boolean is_locked_;
    private AbstractClient client_data_;

    public User()
    {
        is_locked_ = false;
    }

    public User(String username, String hashedPassword, AbstractClient clientData)
    {
        username_ = username;
        hashed_password_ = hashedPassword;
        client_data_ = clientData;
        is_locked_ = false;
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
}
