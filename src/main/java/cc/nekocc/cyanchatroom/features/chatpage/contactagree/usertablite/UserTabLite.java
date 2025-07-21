package cc.nekocc.cyanchatroom.features.chatpage.contactagree.usertablite;


import cc.nekocc.cyanchatroom.model.AppRepository;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.UUID;

public class UserTabLite {
    private final StringProperty nick_name_property_ = new SimpleStringProperty("未加载...");
    private final UUID user_id_;
    private Parent root_ ;
    private CheckBox select_checkbox_ = new CheckBox();

    public UserTabLite(UUID user_id)
    {
        user_id_ = user_id;
        AppRepository.getInstance().getUserDetails(user_id).thenAccept(response -> {
           if(response.getPayload().request_status())
           {
               nick_name_property_.set(response.getPayload().nick_name());
               System.out.println("群聊创建内用户列表username :"+nick_name_property_.get());
           }
           else{
               ViewTool.showAlert(Alert.AlertType.ERROR, "错误", "无法获取用户信息");
           }
        });
        this.root_ = getTab();
    }

    public VBox getTab()
    {

        select_checkbox_.setText("");
        HBox tab = new HBox();
        tab.setAlignment(Pos.CENTER_LEFT);
        Label username_label_ = new Label();

        Label empty = new  Label();
        empty.setPrefWidth(10);

        username_label_.textProperty().bind(nick_name_property_);
        username_label_.setPrefWidth(Control.USE_COMPUTED_SIZE);
        username_label_.setFont(Font.font("微软雅黑", FontWeight.BOLD, 20));

        tab.getChildren().addAll(empty,username_label_,select_checkbox_);
        tab.setSpacing(5);
        tab.setPrefWidth(300);
        tab.setMaxHeight(Control.USE_COMPUTED_SIZE);
        tab.setPrefHeight(Control.USE_COMPUTED_SIZE);
        tab.setMaxWidth(Control.USE_COMPUTED_SIZE);
        VBox tab_box = new VBox();
        Label empty_ = new Label();
        empty_.setPrefHeight(10);
        tab_box.getChildren().addAll(empty_,tab);
        return tab_box;
    }

    public Parent getRootPane() {
        return root_;
    }
    public UUID getUserID() {
        return user_id_;
    }

    public boolean isActive() {
        return select_checkbox_.isSelected();
    }
    public void setActive(boolean active) {
        select_checkbox_.setSelected(active);
    }
}
