package client.scenes;

import algorithms.DebtSettler;
import algorithms.ExchangeProvider;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import commons.Event;
import commons.WebSocketMessage;
import commons.transactions.Payment;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    private final WebSocketServerUtils socket;
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

    /**
     * A constructor for the DebtPaymentOverview.
     *
     * @param serverUtils the server utils
     * @param mainCtrl the main controller
     * @param socket the socket connection
     */
    @Inject
    public DebtPaymentOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl,
                                   WebSocketServerUtils socket) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.socket = socket;
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

        socket.registerForMessages("/topic/eventsUpdated", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                UUID uuid = UUID.fromString(message.getContent().substring(15));
                if (event != null && uuid.equals(event.getInviteCode())) {
                    UIUtils.showEventDeletedWarning(event.getTitle());
                    mainCtrl.showHomePage();
                }
            });
        });
    }


    public void onBackClicked(MouseEvent event) {
        onExit();
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
        base.setPrefHeight(30);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        base.setExpanded(false);
        String titleText = settlement.replace("___", ClientUtils.getCurrency().toString());
        String[] settlementArray = settlement.split(" ");
        double amount = Double.parseDouble(settlementArray[3]);
        double convertedValue = ExchangeProvider.convertCurrency(amount,
                "EUR",
                ClientUtils.getCurrency().toString());
        convertedValue = Math.round(convertedValue * 100.0) / 100.0;
        titleText = titleText.replace(String.valueOf(amount), String.valueOf(convertedValue));

        base.setText(titleText);
        base.setFont(Font.font("SansSerif", 15));

        Pane pane = new Pane();
        pane.setPrefWidth(base.getPrefWidth());
        pane.setPrefHeight(120);
        pane.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text content = new Text(debtSettler.getSettledDebts().get(settlement));
        final double nameTopPadding = 0.2f * pane.getPrefHeight();
        final double nameLeftPadding = 0.02f * pane.getPrefWidth();
        content.setLayoutY(pane.getLayoutY() + nameTopPadding);
        content.setLayoutX(base.getLayoutX() + nameLeftPadding);
        content.setFont(Font.font("SansSerif", 15));
        content.setFill(Paint.valueOf("#FFFFFF"));

        Button payOffDebt = new Button();
        payOffDebt.requestFocus();
        double finalConvertedValue = convertedValue;
        payOffDebt.onActionProperty().set(actionEvent -> {
            Payment payment = new Payment(settlementArray[0], LocalDate.now(), (float) finalConvertedValue,
                    ClientUtils.getCurrency(), settlementArray[settlementArray.length - 1]);
            payment = serverUtils.addPayment(event.getInviteCode(), payment);
            event.addTransaction(payment);
            mainCtrl.showDebtOverview(event);
        });
        final double buttonTopPadding = 0.01f * pane.getPrefHeight();
        final double buttonLeftPadding = 0.62f * pane.getPrefWidth();
        payOffDebt.setLayoutY(buttonTopPadding);
        payOffDebt.setLayoutX(buttonLeftPadding);
        payOffDebt.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: #c30052;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );
        payOffDebt.setText("Debt has been paid");
        payOffDebt.setFont(Font.font("SansSerif", 15));
        payOffDebt.setTextFill(Paint.valueOf("#FFFFFF"));

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(pane);
            UIUtils.activateHighContrastMode(payOffDebt);
        } else {
            UIUtils.deactivateHighContrastMode(pane);
            UIUtils.deactivateHighContrastMode(payOffDebt);
        }

        pane.getChildren().addAll(content, payOffDebt);

        base.setContent(pane);
        base.setOnMouseClicked(mouseEvent -> {
            if (base.isExpanded()) {
                base.setPrefHeight(120);
            } else {
                base.setPrefHeight(30);
            }
        });



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

    /**
     * Unsubscribe from sockets and any other clean-up code.
     */
    public void onExit() {
        socket.unregisterFromMessages("/topic/eventsUpdated");
    }

}
