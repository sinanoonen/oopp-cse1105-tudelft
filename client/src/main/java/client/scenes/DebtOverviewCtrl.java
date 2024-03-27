package client.scenes;

import algorithms.DebtSettler;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Event;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Controller for the DebtOverview scene.
 */
public class DebtOverviewCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private Event event;
    private DebtSettler debtSettler;

    @FXML
    private AnchorPane root;
    @FXML
    private ListView<Node> participantsDebtContainer;
    @FXML
    private Button debtSettleButton;
    @FXML
    private Pane buttonDarkener;
    @FXML
    private Hyperlink backLink;
    @FXML
    private Text balanceText;

    @Inject
    public DebtOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }
    }

    /**
     * Method to refresh the scene. This is needed for some reason.
     */
    public void refresh(Event event) {
        this.debtSettler = new DebtSettler(event);
        this.event = event;

        debtSettleButton.requestFocus();

        buttonDarkener.setVisible(false);
        changeBackgroundColor(backLink, "transparent");

        resetParticipantsDebtContainer();

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
            balanceText.setFill(javafx.scene.paint.Color.WHITE);
        } else {
            UIUtils.deactivateHighContrastMode(root);
            balanceText.setFill(javafx.scene.paint.Color.web("#8e8e8e"));
        }
    }

    @FXML
    public void toggleDarkenedDebtsButton(MouseEvent event) {
        buttonDarkener.setVisible(!buttonDarkener.isVisible());
    }

    public void onBackClicked(MouseEvent event) {
        mainCtrl.showEventOverview(this.event);
    }

    public void showDebtSettler() {
        mainCtrl.showDebtPaymentOverview(event, debtSettler);
    }

    /**
     * Darkens the background of the hyperlink on hover.
     *
     * @param event MouseEvent
     */
    public void toggleHyperlinkBackground(MouseEvent event) {
        changeBackgroundColor(backLink,
                backLink.getStyle().contains("transparent")
                        ? "#2b2b2b"
                        : "transparent"
        );
    }

    private void resetParticipantsDebtContainer() {
        participantsDebtContainer.getItems().removeAll(participantsDebtContainer.getItems());
        List<Node> participantsDebt = debtSettler
                .getDebts()
                .keySet()
                .stream()
                .map(this::debtCellFactory)
                .toList();
        participantsDebtContainer.getItems().addAll(participantsDebt);
    }

    private Node debtCellFactory(String user) {
        Pane base = new Pane();
        base.setPrefWidth(participantsDebtContainer.getPrefWidth() - 20);
        base.setPrefHeight(100);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text username = new Text(user);
        final double nameTopPadding = base.getPrefHeight() / 2 + 5;
        final double nameLeftPadding = 0.12f * base.getPrefWidth();
        username.setLayoutX(base.getLayoutX() + nameLeftPadding);
        username.setLayoutY(base.getLayoutY() + nameTopPadding);
        username.setFont(Font.font("SansSerif", 15));
        username.setFill(Paint.valueOf("#FFFFFF"));
        username.setMouseTransparent(true);

        Text debt = new Text(String.valueOf(-1 * (debtSettler.getDebts().get(user))));
        final double debtLeftPadding = 0.75f * base.getPrefWidth();
        debt.setLayoutX(base.getLayoutX() + debtLeftPadding);
        debt.setLayoutY(base.getLayoutY() + nameTopPadding);
        debt.setFont(Font.font("SansSerif", 15));
        debt.setFill(Paint.valueOf("#FFFFFF"));
        debt.setMouseTransparent(true);

        base.getChildren().addAll(username, debt);

        return base;
    }

    /**
     * Changes the background color of an FXML node.
     * Will add color if not already present.
     *
     * @param node node whose color to change
     * @param color color string
     */
    private void changeBackgroundColor(Node node, String color) {
        String currentStyle = node.getStyle();
        String newColor = "-fx-background-color: " + color + ";";

        if (currentStyle.contains("-fx-background-color")) {
            // remove existing background color
            int startIndex = currentStyle.indexOf("-fx-background-color");
            int endIndex = currentStyle.indexOf(";", startIndex);
            currentStyle = currentStyle.substring(0, startIndex) + currentStyle.substring(endIndex + 1);
        }

        node.setStyle(currentStyle + newColor);
    }
}
