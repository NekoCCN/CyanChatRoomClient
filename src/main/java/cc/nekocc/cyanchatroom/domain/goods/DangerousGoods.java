package cc.nekocc.cyanchatroom.domain.goods;

public class DangerousGoods extends AbstractGoods
{
    @Override
    public double getBillingRate()
    {
        double weight = getBillableWeight();
        if (weight < 20)
        {
            return 80;
        }
        if (weight < 50)
        {
            return 50;
        }
        if (weight < 100)
        {
            return 30;
        }
        return 20;
    }

    public String getDisplayTypeName()
    {
        return "危险品";
    }
}