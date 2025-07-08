package cc.nekocc.cyanchatroom.features.orderdetaildialog;

import cc.nekocc.cyanchatroom.domain.goods.AbstractGoods;
import cc.nekocc.cyanchatroom.domain.order.PaymentMethod;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class OrderDetailDialogController
{
    @FXML
    private Label order_id_label_;
    @FXML
    private Label order_status_label_;
    @FXML
    private Label sender_label_;
    @FXML
    private Label receiver_label_;
    @FXML
    private Label cost_label_;
    @FXML
    private TableView<AbstractGoods> goods_table_;
    @FXML
    private TableColumn<AbstractGoods, String> col_goods_name_;
    @FXML
    private TableColumn<AbstractGoods, String> col_goods_type_;
    @FXML
    private TableColumn<AbstractGoods, Number> col_goods_price_;
    @FXML
    private VBox payment_pane_;
    @FXML
    private RadioButton alipay_radio_;
    @FXML
    private RadioButton wechat_radio_;
    @FXML
    private RadioButton cash_radio_;
    @FXML
    private ToggleGroup payment_toggle_group_;

    public void setViewModel(OrderDetailViewModel view_model)
    {
        order_id_label_.textProperty().bind(view_model.order_id_);
        order_status_label_.textProperty().bind(view_model.order_status_);
        sender_label_.textProperty().bind(view_model.sender_);
        receiver_label_.textProperty().bind(view_model.receiver_);
        cost_label_.textProperty().bind(view_model.cost_);

        goods_table_.itemsProperty().bind(view_model.goods_list_);
        col_goods_name_.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        col_goods_type_.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayTypeName()));
        col_goods_price_.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()));

        payment_pane_.visibleProperty().bind(view_model.payment_pane_visible_);
        payment_pane_.managedProperty().bind(view_model.payment_pane_visible_);

        alipay_radio_.setUserData(PaymentMethod.ALIPAY);
        wechat_radio_.setUserData(PaymentMethod.WECHAT);
        cash_radio_.setUserData(PaymentMethod.CASH);

        payment_toggle_group_.selectedToggleProperty().addListener((obs, old_toggle, new_toggle) ->
        {
            if (new_toggle != null)
            {
                view_model.selected_payment_method_.set((PaymentMethod) new_toggle.getUserData());
            }
            else
            {
                view_model.selected_payment_method_.set(null);
            }
        });
    }
}