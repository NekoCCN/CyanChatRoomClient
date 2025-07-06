package cc.nekocc.cyanchatroom.features.addorder;

import javafx.util.Callback;
import animatefx.animation.SlideInRight;
import animatefx.animation.SlideOutLeft;
import cc.nekocc.cyanchatroom.domain.goods.AbstractGoods;
import cc.nekocc.cyanchatroom.domain.goods.DangerousGoods;
import cc.nekocc.cyanchatroom.domain.goods.ExpediteGoods;
import cc.nekocc.cyanchatroom.domain.goods.NormalGoods;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddOrderController implements Initializable
{
    private AddOrderViewModel view_model_;

    @FXML
    private AnchorPane stack1_pane_;
    @FXML
    private AnchorPane stack2_pane_;
    @FXML
    private AnchorPane stack3_pane_;
    @FXML
    private AnchorPane stack4_pane_;
    private List<Node> wizard_panes_;
    private int current_page_index_ = 0;

    @FXML
    private Rectangle stack1_rect_;
    @FXML
    private Rectangle stack2_rect_;
    @FXML
    private Rectangle stack3_rect_;
    @FXML
    private Rectangle stack4_rect_;

    @FXML
    private TextField stack1_sender_name_;
    @FXML
    private TextField stack1_sender_phone_field_;
    @FXML
    private TextField stack1_sender_address_field_;
    @FXML
    private TextField stack1_sender_type_field_;
    @FXML
    private Button stack1_next_;
    @FXML
    private Button stack1_cancel;

    @FXML
    private TextField stack2_receiver_name_;
    @FXML
    private TextField stack2_receiver_phone_;
    @FXML
    private TextField stack2_receiver_address_;
    @FXML
    private Button stack2_empty_;
    @FXML
    private Button stack2_next_;
    @FXML
    private Button stack2_back_;

    @FXML
    private TextField stack3_good_name_;
    @FXML
    private TextField stack3_good_weight_;
    @FXML
    private TextField stack3_good_lengh_;
    @FXML
    private TextField stack3_good_width_;
    @FXML
    private TextField stack3_good_height_;
    @FXML
    private ChoiceBox<Class<? extends AbstractGoods>> stack3_good_type_;
    @FXML
    private Button stack3_add_;
    @FXML
    private Button stack3_empty_;
    @FXML
    private TableView<AbstractGoods> stack3_goods_;
    @FXML
    private TableColumn<AbstractGoods, String> stack3_goods_name_;
    @FXML
    private TableColumn<AbstractGoods, String> stack3_goods_type_;
    @FXML
    private TableColumn<AbstractGoods, Double> stack3_goods_volume_;
    @FXML
    private TableColumn<AbstractGoods, Double> stack3_goods_weight_;
    @FXML
    private Button stack3_next_;
    @FXML
    private Button stack3_back_;

    @FXML
    private Button stack4_pay_;
    @FXML
    private Button stack4_save_;

    @Override
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        view_model_ = new AddOrderViewModel();
        view_model_.initialize();

        wizard_panes_ = Arrays.asList(stack1_pane_, stack2_pane_, stack3_pane_, stack4_pane_);

        bindControls();
        setupActions();
        setupGoodsTable();
        setupResponsiveLayout();

        wizard_panes_.forEach(p -> p.setVisible(false));
        wizard_panes_.get(0).setVisible(true);
    }

    private void setupGoodsTable()
    {
        stack3_goods_name_.setCellValueFactory(cell_data ->
                new SimpleStringProperty(cell_data.getValue().getName())
        );
        stack3_goods_type_.setCellValueFactory(cell_data ->
                new SimpleStringProperty(cell_data.getValue().getDisplayTypeName())
        );
        stack3_goods_volume_.setCellValueFactory(cell_data ->
                new SimpleDoubleProperty(cell_data.getValue().getVolume()).asObject()
        );
        stack3_goods_weight_.setCellValueFactory(cell_data ->
                new SimpleDoubleProperty(cell_data.getValue().getBillableWeight()).asObject()
        );

        stack3_good_type_.getItems().addAll(NormalGoods.class, ExpediteGoods.class, DangerousGoods.class);
        stack3_good_type_.setConverter(new StringConverter<>()
        {
            @Override
            public String toString(Class<? extends AbstractGoods> clazz) {
                if (clazz == null) return null;
                if (clazz.equals(NormalGoods.class)) return "普通货物";
                if (clazz.equals(ExpediteGoods.class)) return "加急货物";
                if (clazz.equals(DangerousGoods.class)) return "危险品";
                return "未知类型";
            }
            @Override
            public Class<? extends AbstractGoods> fromString(String string) { return null; }
        });
        stack3_good_type_.getSelectionModel().selectFirst();

        TableColumn<AbstractGoods, Void> remove_col = new TableColumn<>("移除");
        Callback<TableColumn<AbstractGoods, Void>, TableCell<AbstractGoods, Void>> cell_factory = param -> {
            return new TableCell<>()
            {
                private final Button remove_button = new Button("X");
                {
                    remove_button.setOnAction(event -> {
                        AbstractGoods good = getTableView().getItems().get(getIndex());
                        view_model_.removeGood(good);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : remove_button);
                }
            };
        };
        remove_col.setCellFactory(cell_factory);
        stack3_goods_.getColumns().add(remove_col);
    }

    private void setupResponsiveLayout()
    {
        stack1_pane_.sceneProperty().addListener((scene_observable, old_scene, new_scene) ->
        {
            if (old_scene == null && new_scene != null)
            {
                new_scene.windowProperty().addListener((window_observable, old_window, new_window) -> {
                    if (old_window == null && new_window != null)
                    {
                        Stage stage = (Stage) new_window;

                        stack1_rect_.heightProperty().bind(stage.getScene().heightProperty());
                        stack2_rect_.heightProperty().bind(stage.getScene().heightProperty());
                        stack3_rect_.heightProperty().bind(stage.getScene().heightProperty());

                        stack4_rect_.widthProperty().bind(stack4_pane_.widthProperty());
                        stack4_rect_.heightProperty().bind(stack4_pane_.heightProperty());
                    }
                });
            }
        });
    }


    private void bindControls()
    {
        stack1_sender_name_.textProperty().bind(view_model_.sender_name_);
        stack1_sender_phone_field_.textProperty().bind(view_model_.sender_phone_);
        stack1_sender_address_field_.textProperty().bind(view_model_.sender_address_);
        stack1_sender_type_field_.textProperty().bind(view_model_.sender_type_);

        stack2_receiver_name_.textProperty().bindBidirectional(view_model_.receiver_name_);
        stack2_receiver_phone_.textProperty().bindBidirectional(view_model_.receiver_phone_);
        stack2_receiver_address_.textProperty().bindBidirectional(view_model_.receiver_address_);

        stack3_good_name_.textProperty().bindBidirectional(view_model_.good_name_);
        stack3_good_weight_.textProperty().bindBidirectional(view_model_.good_weight_);
        stack3_good_lengh_.textProperty().bindBidirectional(view_model_.good_length_);
        stack3_good_width_.textProperty().bindBidirectional(view_model_.good_width_);
        stack3_good_height_.textProperty().bindBidirectional(view_model_.good_height_);
        stack3_goods_.itemsProperty().bind(view_model_.added_goods_);
        view_model_.selected_good_type_.bind(stack3_good_type_.getSelectionModel().selectedItemProperty());
    }

    private void setupActions()
    {
        stack1_next_.setOnAction(_ -> navigateToStep(1));
        stack2_back_.setOnAction(_ -> navigateToStep(0));
        stack2_next_.setOnAction(_ -> navigateToStep(2));
        stack3_back_.setOnAction(_ -> navigateToStep(1));
        stack3_next_.setOnAction(_ -> {
            view_model_.finalizeOrder();
            navigateToStep(3);
        });

        stack1_cancel.setOnAction(_ -> view_model_.cancel());
        stack3_add_.setOnAction(_ -> view_model_.addGood());
        stack3_empty_.setOnAction(_ -> view_model_.clearGoodInputs());
        stack4_save_.setOnAction(_ -> view_model_.saveOrder());
        stack4_pay_.setOnAction(_ -> view_model_.payAndSaveOrder());
    }

    private void navigateToStep(int next_page_index)
    {
        Node current_pane = wizard_panes_.get(current_page_index_);
        Node next_pane = wizard_panes_.get(next_page_index);

        SlideOutLeft out_animation = new SlideOutLeft(current_pane);
        SlideInRight in_animation = new SlideInRight(next_pane);

        out_animation.setOnFinished(e ->
        {
            current_pane.setVisible(false);
            in_animation.play();
            next_pane.setVisible(true);
        });
        out_animation.play();

        current_page_index_ = next_page_index;
    }
}