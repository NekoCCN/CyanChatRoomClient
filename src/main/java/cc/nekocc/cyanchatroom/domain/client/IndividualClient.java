package cc.nekocc.cyanchatroom.domain.client;

public class IndividualClient extends AbstractClient
{
    public IndividualClient()
    {
        super();
    }

    public IndividualClient(String name, String phone, String address)
    {
        super(name, phone, address);
    }

    @Override
    public double getBillingDiscount()
    {
        return 0.9;
    }
}