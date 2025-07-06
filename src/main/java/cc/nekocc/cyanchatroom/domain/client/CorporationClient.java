package cc.nekocc.cyanchatroom.domain.client;

public class CorporationClient extends AbstractClient
{
    public CorporationClient()
    {
        super();
    }

    public CorporationClient(String name, String phone, String address)
    {
        super(name, phone, address);
    }

    @Override
    public double getBillingDiscount()
    {
        return 0.8;
    }
}