package cc.nekocc.cyanchatroom.domain.client;

import java.util.UUID;

public abstract class AbstractClient
{
    private String client_id_;
    private String name_;
    private String phone_;
    private String address_;

    public AbstractClient()
    {  }

    public AbstractClient(String name, String phone, String address)
    {
        client_id_ = UUID.randomUUID().toString();
        name_ = name;
        phone_ = phone;
        address_ = address;
    }

    public String getClientId()
    {
        return client_id_;
    }
    public String getName()
    {
        return name_;
    }

    public abstract double getBillingDiscount();

    public String getPhone()
    {
        return phone_;
    }

    public String getAddress()
    {
        return address_;
    }
}