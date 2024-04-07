package client.scenes;

import algorithms.DebtSettler;
import algorithms.ExchangeProvider;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Event;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 * Controller for the DebtPaymentOverview scene. 
 */
public class DebtPaymentOverviewCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private Event event;
    private DebtSettler debtSettler;

    @FXML
    private AnchorPane root;
    @FXML
    private ListView<Node> participantsPaymentContainer;
    @FXML
    private Hyperlink backLink;
    @FXML
    private Text instructionText;

    @Inject
    public DebtPaymentOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
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
    public void refresh(Event event, DebtSettler debtSettler) {
        this.debtSettler = debtSettler;
        this.event = event;

        changeBackgroundColor(backLink, "transparent");

        resetParticipantsPaymentContainer();

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
            instructionText.setFill(javafx.scene.paint.Color.WHITE);
        } else {
            UIUtils.deactivateHighContrastMode(root);
            instructionText.setFill(javafx.scene.paint.Color.web("#8e8e8e"));
        }
    }


    public void onBackClicked(MouseEvent event) {
        mainCtrl.showDebtOverview(this.event);
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


    private void resetParticipantsPaymentContainer() {
        participantsPaymentContainer.getItems().removeAll(participantsPaymentContainer.getItems());
        List<Node> participantsDebtPayment = debtSettler
                .getSettledDebts()
                .keySet()
                .stream()
                .map(this::debtPaymentCellFactory)
                .toList();
        participantsPaymentContainer.getItems().addAll(participantsDebtPayment);
    }

    private Node debtPaymentCellFactory(String settlement) {
        TitledPane base = new TitledPane();
        base.setPrefWidth(participantsPaymentContainer.getPrefWidth() - 20);
        base.setPrefHeight(20);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        base.setExpanded(false);
        String titleText = settlement.replace("___", ClientUtils.getCurrency().toString());
        double amount = Double.parseDouble(settlement.split(" ")[3]);
        double convertedValue = ExchangeProvider.convertCurrency(amount,
                "EUR",
                ClientUtils.getCurrency().toString());
        convertedValue = Math.round(convertedValue * 100.0) / 100.0;
        titleText = titleText.replace(String.valueOf(amount), String.valueOf(convertedValue));

        base.setText(titleText);
        base.setFont(Font.font("SansSerif", 15));

        Text content = new Text(debtSettler.getSettledDebts().get(settlement));
        final double nameLeftPadding = 0.7f * base.getPrefWidth();
        content.setLayoutX(base.getLayoutX() + nameLeftPadding);
        content.setFont(Font.font("SansSerif", 15));

        base.setContent(content);



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
