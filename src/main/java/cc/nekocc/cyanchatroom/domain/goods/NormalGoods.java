package cc.nekocc.cyanchatroom.domain.goods;

public class NormalGoods extends AbstractGoods
{
    @Override
    public double getBillingRate()
    {
        double weight = getBillableWeight();

        if (weight < 20)
        {
            return 35;
        }
        if (weight < 50)
        {
            return 30;
        }
        if (weight < 100)
        {
            return 25;
        }

        return 15;
    }

    public String getDisplayTypeName()
    {
        return "普通货物";
    }
}
