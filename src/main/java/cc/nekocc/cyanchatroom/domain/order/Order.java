package cc.nekocc.cyanchatroom.domain.order;

import cc.nekocc.cyanchatroom.domain.client.AbstractClient;
import cc.nekocc.cyanchatroom.domain.goods.AbstractGoods;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order
{
    private String order_id_;
    private LocalDateTime order_date_time_;
    private OrderStatus status_;

    private AbstractClient sender_;

    private String receiver_name_;
    private String receiver_phone_;
    private String receiver_address_;

    private List<AbstractGoods> goods_list_;

    private double base_cost_;
    private double final_cost_;

    public Order(AbstractClient sender)
    {
        this.order_id_ = UUID.randomUUID().toString();
        this.order_date_time_ = LocalDateTime.now();
        this.status_ = OrderStatus.PENDING_PAYMENT;
        this.goods_list_ = new ArrayList<>();
        this.sender_ = sender;
    }

    public void calculateCosts()
    {
        base_cost_ = goods_list_.stream()
                .mapToDouble(AbstractGoods::getPrice)
                .sum();
        final_cost_ = base_cost_ * sender_.getBillingDiscount();
    }

    public void addGoods(AbstractGoods goods)
    {
        if (canModify())
        {
            goods_list_.add(goods);
            calculateCosts();
        } else
        {
            throw new IllegalStateException("订单状态为 " + status_.getDisplayName() + ", 无法添加货物。");
        }
    }

    public void removeGoods(AbstractGoods goods)
    {
        if (canModify())
        {
            goods_list_.remove(goods);
            calculateCosts();
        } else
        {
            throw new IllegalStateException("订单状态为 " + status_.getDisplayName() + ", 无法移除货物。");
        }
    }

    public void pay()
    {
        if (status_ == OrderStatus.PENDING_PAYMENT)
        {
            status_ = OrderStatus.IN_TRANSIT;
        } else
        {
            throw new IllegalStateException("只有待支付状态的订单才能支付。");
        }
    }

    public boolean canModify()
    {
        return status_ == OrderStatus.PENDING_PAYMENT;
    }

    public String getOrderId()
    {
        return order_id_;
    }

    public String getReceiverAddress()
    {
        return receiver_address_;
    }

    public OrderStatus getOrderStatus()
    {
        return status_;
    }

    public double getFinalCost()
    {
        return final_cost_;
    }

    public LocalDateTime getOrderDateTime()
    {
        return order_date_time_;
    }

    public AbstractClient getSender()
    {
        return sender_;
    }

    public String getReceiverName()
    {
        return receiver_name_;
    }

    public String getReceiverPhone()
    {
        return receiver_phone_;
    } // <-- 新增的 Getter

    public List<AbstractGoods> getGoodsList()
    {
        return Collections.unmodifiableList(goods_list_);
    }

    public void setReceiverName(String receiver_name)
    {
        this.receiver_name_ = receiver_name;
    }

    public void setReceiverPhone(String receiver_phone)
    {
        this.receiver_phone_ = receiver_phone;
    }

    public void setReceiverAddress(String receiver_address)
    {
        this.receiver_address_ = receiver_address;
    }
}