package cc.nekocc.cyanchatroom.features.addorder;

import cc.nekocc.cyanchatroom.Navigator;
import cc.nekocc.cyanchatroom.domain.User;
import cc.nekocc.cyanchatroom.domain.client.IndividualClient;
import cc.nekocc.cyanchatroom.domain.goods.AbstractGoods;
import cc.nekocc.cyanchatroom.domain.goods.DangerousGoods;
import cc.nekocc.cyanchatroom.domain.goods.ExpediteGoods;
import cc.nekocc.cyanchatroom.domain.goods.NormalGoods;
import cc.nekocc.cyanchatroom.domain.order.Order;
import cc.nekocc.cyanchatroom.model.OrderRepository;
import cc.nekocc.cyanchatroom.model.UserSession;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

import java.lang.reflect.InvocationTargetException;

public class AddOrderViewModel
{
    private final OrderRepository order_repository_;
    private final User current_user_;
    private final Order order_in_progress_;

    public final StringProperty sender_name_ = new SimpleStringProperty();
    public final StringProperty sender_phone_ = new SimpleStringProperty();
    public final StringProperty sender_address_ = new SimpleStringProperty();
    public final StringProperty sender_type_ = new SimpleStringProperty();

    public final StringProperty receiver_name_ = new SimpleStringProperty();
    public final StringProperty receiver_phone_ = new SimpleStringProperty();
    public final StringProperty receiver_address_ = new SimpleStringProperty();

    public final StringProperty good_name_ = new SimpleStringProperty();
    public final StringProperty good_weight_ = new SimpleStringProperty();
    public final StringProperty good_length_ = new SimpleStringProperty();
    public final StringProperty good_width_ = new SimpleStringProperty();
    public final StringProperty good_height_ = new SimpleStringProperty();
    public final ObjectProperty<Class<? extends AbstractGoods>> selected_good_type_ = new SimpleObjectProperty<>();
    public final ListProperty<AbstractGoods> added_goods_ = new SimpleListProperty<>(FXCollections.observableArrayList());

    public final StringProperty final_cost_text_ = new SimpleStringProperty();

    public AddOrderViewModel()
    {
        order_repository_ = new OrderRepository();
        current_user_ = UserSession.getInstance().getCurrentUser();
        order_in_progress_ = new Order(current_user_.getClientData());
    }

    public void initialize()
    {
        sender_name_.set(current_user_.getClientData().getName());
        sender_phone_.set(current_user_.getClientData().getPhone());
        sender_address_.set(current_user_.getClientData().getAddress());
        sender_type_.set(current_user_.getClientData() instanceof IndividualClient ? "个人客户" : "公司客户");
    }

    public void addGood()
    {
        try
        {
            if (good_name_.get().isEmpty())
            {
                showAlert(Alert.AlertType.WARNING, "提示", "请输入货物名称。");
                return;
            }
            double weight = Double.parseDouble(good_weight_.get());
            double length = Double.parseDouble(good_length_.get());
            double width = Double.parseDouble(good_width_.get());
            double height = Double.parseDouble(good_height_.get());

            Class<? extends AbstractGoods> good_class = selected_good_type_.get();
            AbstractGoods new_good = good_class.getDeclaredConstructor().newInstance();

            new_good.setName(good_name_.get());
            new_good.setWeight(weight);
            new_good.setLength(length);
            new_good.setWidth(width);
            new_good.setHeight(height);

            order_in_progress_.addGoods(new_good);
            added_goods_.set(FXCollections.observableArrayList(order_in_progress_.getGoodsList()));
            clearGoodInputs();
        }
        catch (NumberFormatException e)
        {
            showAlert(Alert.AlertType.ERROR, "输入错误", "重量和尺寸必须是有效的数字。");
        }
        catch (Exception e)
        {
            showAlert(Alert.AlertType.ERROR, "错误", "无法创建货物对象: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeGood(AbstractGoods good)
    {
        order_in_progress_.removeGoods(good);
        added_goods_.set(FXCollections.observableArrayList(order_in_progress_.getGoodsList()));
    }

    public void clearGoodInputs()
    {
        good_name_.set("");
        good_weight_.set("");
        good_length_.set("");
        good_width_.set("");
        good_height_.set("");
    }

    public void finalizeOrder()
    {
        order_in_progress_.setReceiverName(receiver_name_.get());
        order_in_progress_.setReceiverPhone(receiver_phone_.get());
        order_in_progress_.setReceiverAddress(receiver_address_.get());
        order_in_progress_.calculateCosts();
        final_cost_text_.set(String.format("最终费用: %.2f 元", order_in_progress_.getFinalCost()));
    }

    public void saveOrder()
    {
        order_repository_.addOrder(order_in_progress_);
        showAlert(Alert.AlertType.INFORMATION, "成功", "订单已成功保存！");
        cancel();
    }

    public void payAndSaveOrder()
    {
        order_in_progress_.pay();
        saveOrder();
    }

    public void cancel()
    {
        Navigator.navigateTo("fxml/MainPage.fxml", Navigator.AnimationType.SLIDE_DOWN);
    }

    private void showAlert(Alert.AlertType alert_type, String title, String content)
    {
        Alert alert = new Alert(alert_type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}