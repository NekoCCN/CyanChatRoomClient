package cc.nekocc.cyanchatroom.domain.order;

public enum PaymentMethod
{
    ALIPAY("支付宝"),
    WECHAT("微信支付"),
    CASH("现金");

    private final String display_name_;

    PaymentMethod(String display_name)
    {
        this.display_name_ = display_name;
    }

    public String getDisplayName()
    {
        return display_name_;
    }
}