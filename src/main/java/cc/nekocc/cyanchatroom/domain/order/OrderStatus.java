package cc.nekocc.cyanchatroom.domain.order;

public enum OrderStatus
{
    PENDING_PAYMENT("待支付"),
    IN_TRANSIT("运输中");

    private final String display_name_;

    OrderStatus(String displayName)
    {
        display_name_ = displayName;
    }
    public String getDisplayName()
    {
        return display_name_;
    }
}
