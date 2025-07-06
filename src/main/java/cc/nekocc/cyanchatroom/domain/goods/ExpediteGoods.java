package cc.nekocc.cyanchatroom.domain.goods;

public class ExpediteGoods extends AbstractGoods
{
    @Override
    public double getBillingRate()
    {
        double weight = getBillableWeight();
        if (weight < 20)
        {
            return 60;
        }
        if (weight < 50)
        {
            return 50;
        }
        if (weight < 100)
        {
            return 40;
        }
        return 30;
    }

    public String getDisplayTypeName()
    {
        return "加急货物";
    }
}