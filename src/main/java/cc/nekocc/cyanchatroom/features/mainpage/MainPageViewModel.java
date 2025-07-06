package cc.nekocc.cyanchatroom.features.mainpage;

import cc.nekocc.cyanchatroom.Navigator;
import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.order.Order;
import cc.nekocc.cyanchatroom.domain.order.OrderStatus;
import cc.nekocc.cyanchatroom.features.orderdetaildialog.OrderDetailDialog;
import cc.nekocc.cyanchatroom.model.OrderRepository;
import cc.nekocc.cyanchatroom.model.UserSession;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.List;
import java.util.stream.Collectors;

public class MainPageViewModel
{
    private final OrderRepository order_repository_;
    private final User current_user_;

    private final StringProperty user_greetings_ = new SimpleStringProperty();
    private final StringProperty unpaid_orders_text_ = new SimpleStringProperty();
    private final StringProperty transit_orders_text_ = new SimpleStringProperty();

    private final ListProperty<Order> user_orders_ = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<PieChart.Data> pie_chart_data_ = new SimpleListProperty<>(FXCollections.observableArrayList());

    public MainPageViewModel()
    {
        this.order_repository_ = new OrderRepository();
        this.current_user_ = UserSession.getInstance().getCurrentUser();
        loadData();
    }

    public void loadData()
    {
        List<Order> orders = order_repository_.findByClient(current_user_.getUsername()).collect(Collectors.toList());
        user_orders_.set(FXCollections.observableArrayList(orders));

        user_greetings_.set("你好, " + current_user_.getUsername());

        long unpaid_count = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING_PAYMENT).count();
        long transit_count = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.IN_TRANSIT).count();

        unpaid_orders_text_.set("您有 " + unpaid_count + " 个订单待支付。");
        transit_orders_text_.set("您有 " + transit_count + " 个订单正在运输中。");

        ObservableList<PieChart.Data> new_pie_data = FXCollections.observableArrayList();

        if (unpaid_count > 0)
        {
            new_pie_data.add(new PieChart.Data("待支付", unpaid_count));
        }
        if (transit_count > 0)
        {
            new_pie_data.add(new PieChart.Data("运输中", transit_count));
        }

        pie_chart_data_.set(new_pie_data);
    }

    public void createNewOrder()
    {
        Navigator.navigateTo("fxml/CreateOrder.fxml", Navigator.AnimationType.SLIDE_UP);
    }

    public void viewOrderDetails(Order order)
    {
        OrderDetailDialog dialog = new OrderDetailDialog(order, this.order_repository_);
        dialog.showAndWait().ifPresent(payment_succeeded -> {
            if (payment_succeeded)
            {
                loadData();
            }
        });
    }

    public StringProperty userGreetingsProperty()
    {
        return user_greetings_;
    }

    public StringProperty unpaidOrdersTextProperty()
    {
        return unpaid_orders_text_;
    }

    public StringProperty transitOrdersTextProperty()
    {
        return transit_orders_text_;
    }

    public ListProperty<Order> userOrdersProperty()
    {
        return user_orders_;
    }

    public ListProperty<PieChart.Data> pieChartDataProperty()
    {
        return pie_chart_data_;
    }
}