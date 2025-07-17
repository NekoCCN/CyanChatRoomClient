package cc.nekocc.cyanchatroom.features.chatpage.contactagree;

import atlantafx.base.theme.Styles;
import cc.nekocc.cyanchatroom.util.ViewTool;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class FriendRequestCardController
{

    @FXML
    private StackPane avatar_pane_;
    @FXML
    private Label username_label_;
    @FXML
    private Label status_label_;
    @FXML
    private HBox action_buttons_pane_;
    @FXML
    private Button accept_button_;
    @FXML
    private Button refuse_button_;

    private FriendRequestViewModel view_model_;

    @FXML
    public void initialize()
    {
        accept_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SUCCESS);
        refuse_button_.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
    }

    public void setViewModel(FriendRequestViewModel viewModel)
    {
        this.view_model_ = viewModel;

        username_label_.textProperty().bind(view_model_.senderNameProperty());
        status_label_.textProperty().bind(view_model_.statusTextProperty());

        action_buttons_pane_.visibleProperty().bind(view_model_.isPendingFromOtherProperty());
        action_buttons_pane_.managedProperty().bind(action_buttons_pane_.visibleProperty());

        status_label_.visibleProperty().bind(action_buttons_pane_.visibleProperty().not());
        status_label_.managedProperty().bind(status_label_.visibleProperty());

        view_model_.senderNameProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal != null && !newVal.equals("加载中..."))
            {
                avatar_pane_.getChildren().setAll(ViewTool.getDefaultAvatar(newVal));
            }
        });
    }

    @FXML
    private void handleAccept()
    {
        if (view_model_ != null)
            view_model_.acceptRequest();
    }

    @FXML
    private void handleReject()
    {
        if (view_model_ != null)
            view_model_.rejectRequest();
    }
}