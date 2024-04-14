package client.scenes;

import algorithms.DebtSettler;
import algorithms.ExchangeProvider;
import client.interfaces.LanguageInterface;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
public class DebtPaymentOverviewCtrl implements Initializable, LanguageInterface {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final UIUtils uiUtils;
    private final ClientUtils clientUtils;
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
     * @param mainCtrl    the main controller
     * @param uiUtils    the uiUtils
     * @param clientUtils  the ClientUtils
     * @param socket      the socket connection
     */
    @Inject
    public DebtPaymentOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl,
                                   UIUtils uiUtils, ClientUtils clientUtils, WebSocketServerUtils socket) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.uiUtils = uiUtils;
        this.clientUtils = clientUtils;
        this.socket = socket;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
        } else {
            uiUtils.deactivateHighContrastMode(root);
        }

        root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                onBackClicked(null);
            }
        });
    }

    @Override
    public void updateLanguage() {
        var lm = uiUtils.getLanguageMap();
        backLink.setText(lm.get("general_back"));
        instructionText.setText(lm.get("debtsoverview_payment_instructions"));
    }

    /**
     * Method to refresh the scene. This is needed for some reason.
     */
    public void refresh(Event event, DebtSettler debtSettler) {
        this.debtSettler = debtSettler;
        this.event = event;

        changeBackgroundColor(backLink, "transparent");

        resetParticipantsPaymentContainer();

        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
            instructionText.setFill(javafx.scene.paint.Color.WHITE);
        } else {
            uiUtils.deactivateHighContrastMode(root);
            instructionText.setFill(javafx.scene.paint.Color.web("#8e8e8e"));
        }

        socket.registerForMessages("/topic/event", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                UUID uuid = UUID.fromString(message.getContent().substring(15));
                if (event != null && uuid.equals(event.getInviteCode())) {
                    uiUtils.showEventDeletedWarning(event.getTitle());
                    onExit();
                    mainCtrl.showHomePage();
                }
            });
        });

        updateLanguage();
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
        String titleText = settlement.replace("___", clientUtils.getCurrency().toString());
        String[] settlementArray = settlement.split(" ");
        double amount = Double.parseDouble(settlementArray[3]);
        double convertedValue = ExchangeProvider.convertCurrency(amount,
                "EUR",
                clientUtils.getCurrency().toString());
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
                    clientUtils.getCurrency(), settlementArray[settlementArray.length - 1]);
            payment = serverUtils.addPayment(event.getInviteCode(), payment);
            event.addTransaction(payment);
            onExit();
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
        payOffDebt.setText(uiUtils.getLanguageMap().get("debtsoverview_has_been_settled"));
        payOffDebt.setFont(Font.font("SansSerif", 15));
        payOffDebt.setTextFill(Paint.valueOf("#FFFFFF"));

        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(pane);
            uiUtils.activateHighContrastMode(payOffDebt);
        } else {
            uiUtils.deactivateHighContrastMode(pane);
            uiUtils.deactivateHighContrastMode(payOffDebt);
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

        // Translations
        var lm = uiUtils.getLanguageMap();
        String baseText = base.getText();
        String contentText = content.getText();

        baseText = baseText.replace("should send", lm.get("debtsoverview_message_should_send"));
        baseText = baseText.replaceAll("to", lm.get("debtsoverview_message_to"));
        contentText = contentText.replaceAll("to", lm.get("debtsoverview_message_to"));
        contentText = contentText.replace("can transfer the money",
                lm.get("debtsoverview_message_can_transfer_money"));
        contentText = contentText.replace("can send a reminder",
                lm.get("debtsoverview_message_can_send_reminder"));
        contentText = contentText.replace("the Email", lm.get("debtsoverview_message_the_email"));

        base.setText(baseText);
        content.setText(contentText);

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
        socket.unregisterFromMessages("/topic/event");
    }

}
