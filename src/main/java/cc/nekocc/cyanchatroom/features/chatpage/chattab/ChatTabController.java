package cc.nekocc.cyanchatroom.features.chatpage.chattab;

import cc.nekocc.cyanchatroom.model.entity.ConversationType;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ChatTabController
{
    @FXML
    private AnchorPane tabRootPane;
    @FXML
    private StackPane avatarPane;
    @FXML
    private Circle avatarCircle;
    @FXML
    private Label avatarLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label statusLabel;

    public void setData(ChatTabViewModel viewModel, Runnable onClickAction)
    {
        usernameLabel.textProperty().bind(viewModel.oppositeUserNameProperty());

        avatarLabel.textProperty().bind(Bindings.createStringBinding(
                () -> viewModel.getOppositeUserName().isEmpty() ? "" : viewModel.getOppositeUserName().substring(0, 1).toUpperCase(),
                viewModel.oppositeUserNameProperty()
        ));

        if (viewModel.getConversationType() == ConversationType.GROUP)
        {
            statusLabel.textProperty().unbind();
            statusLabel.textFillProperty().unbind();
            statusLabel.setText("群聊");
            statusLabel.setTextFill(Color.GRAY);
        } else
        {
            statusLabel.textProperty().bind(Bindings.createStringBinding(
                    () -> viewModel.oppositeStatusProperty().get().toDisplayString(),
                    viewModel.oppositeStatusProperty()
            ));

            statusLabel.textFillProperty().bind(Bindings.createObjectBinding(
                    () -> Color.web(viewModel.oppositeStatusProperty().get().getColor()),
                    viewModel.oppositeStatusProperty()
            ));
        }

        tabRootPane.setOnMouseClicked(e -> onClickAction.run());
    }

    public AnchorPane getRoot()
    {
        return tabRootPane;
    }
}