package cc.nekocc.cyanchatroom.features.mainpage;

import cc.nekocc.cyanchatroom.domain.order.Order;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable
{
    @FXML
    private Label user_greetings_;
    @FXML
    private Label unpaid_orders_num_;
    @FXML
    private Label transit_orders_num_;
    @FXML
    private PieChart pie_chart_;
    @FXML
    private TableView<Order> order_list_;

    @FXML
    private TableColumn<Order, String> col_order_id_;
    @FXML
    private TableColumn<Order, String> col_sender_name_;
    @FXML
    private TableColumn<Order, String> col_receiver_name_;
    @FXML
    private TableColumn<Order, String> col_receiver_address_;
    @FXML
    private TableColumn<Order, Void> col_action_;

    @FXML
    private Button new_order_button_;
    @FXML
    private Button refresh_button_;

    private MainPageViewModel view_model_;

    @Override
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        view_model_ = new MainPageViewModel();
        bindControlsToViewModel();
        setupTableView();
        setupUi();
    }

    private void bindControlsToViewModel()
    {
        user_greetings_.textProperty().bind(view_model_.userGreetingsProperty());
        unpaid_orders_num_.textProperty().bind(view_model_.unpaidOrdersTextProperty());
        transit_orders_num_.textProperty().bind(view_model_.transitOrdersTextProperty());

        order_list_.itemsProperty().bind(view_model_.userOrdersProperty());
        pie_chart_.dataProperty().bind(view_model_.pieChartDataProperty());
    }

    private void setupTableView()
    {
        col_order_id_.setCellValueFactory(cell_data -> new SimpleStringProperty(cell_data.getValue().getOrderId()));
        col_sender_name_.setCellValueFactory(cell_data -> new SimpleStringProperty(cell_data.getValue().getSender().getName()));
        col_receiver_name_.setCellValueFactory(cell_data -> new SimpleStringProperty(cell_data.getValue().getReceiverName()));
        col_receiver_address_.setCellValueFactory(cell_data -> new SimpleStringProperty(cell_data.getValue().getReceiverAddress()));

        Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cell_factory = new Callback<>()
        {
            @Override
            public TableCell<Order, Void> call(final TableColumn<Order, Void> param)
            {
                return new TableCell<>()
                {
                    private final Button btn = new Button("查看");
                    {
                        btn.setOnAction(event -> {
                            Order order = getTableView().getItems().get(getIndex());
                            view_model_.viewOrderDetails(order);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        };
        col_action_.setCellFactory(cell_factory);
    }

    private void setupUi()
    {
        order_list_.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        new_order_button_.setOnAction(event -> view_model_.createNewOrder());
        refresh_button_.setOnAction(event -> view_model_.loadData());

        pie_chart_.setLegendVisible(false);
        pie_chart_.setLabelsVisible(true);
    }
}