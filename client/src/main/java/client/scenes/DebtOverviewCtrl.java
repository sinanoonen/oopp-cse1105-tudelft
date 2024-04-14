package client.scenes;

import algorithms.DebtSettler;
import algorithms.ExchangeProvider;
import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.WebSocketMessage;
import commons.transactions.Expense;
import java.net.URL;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Controller for the DebtOverview scene.
 */
public class DebtOverviewCtrl implements Initializable, LanguageInterface {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final WebSocketServerUtils socket;
    private final UIUtils uiUtils;
    private final ClientUtils clientUtils;

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

    /**
     * Constructor for the DebtOverview controller.
     *
     * @param serverUtils serverUtils
     * @param mainCtrl    mainCtrl
     * @param socket      socket
     * @param uiUtils    uiUtils
     * @param clientUtils  clientUtils
     */
    @Inject
    public DebtOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl,
                            WebSocketServerUtils socket, UIUtils uiUtils, ClientUtils clientUtils) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.socket = socket;
        this.uiUtils = uiUtils;
        this.clientUtils = clientUtils;
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
        balanceText.setText(lm.get("debtsoverview_participant_balance"));
        debtSettleButton.setText(lm.get("debtsoverview_settle"));
        backLink.setText(lm.get("general_back"));

        uiUtils.addTooltip(backLink, "ESC: " + lm.get("general_back"));
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

        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
            balanceText.setFill(javafx.scene.paint.Color.WHITE);
        } else {
            uiUtils.deactivateHighContrastMode(root);
            balanceText.setFill(javafx.scene.paint.Color.web("#8e8e8e"));
        }

        socket.registerForMessages("/topic/eventsUpdated", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                UUID uuid = UUID.fromString(message.getContent().substring(15));
                if (event != null && uuid.equals(event.getInviteCode())) {
                    uiUtils.showEventDeletedWarning(event.getTitle());
                    mainCtrl.showHomePage();
                }
            });
        });

        updateLanguage();
    }

    @FXML
    public void toggleDarkenedDebtsButton(MouseEvent event) {
        buttonDarkener.setVisible(!buttonDarkener.isVisible());
    }

    public void onBackClicked(MouseEvent event) {
        onExit();
        mainCtrl.showEventOverview(this.event);
    }

    public void showDebtSettler() {
        onExit();
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
        participantsDebtContainer.getItems().add(totalSpentNode());
        List<Node> participantsDebt = debtSettler
                .getDebts()
                .keySet()
                .stream()
                .map(this::debtCellFactory)
                .toList();
        participantsDebtContainer.getItems().addAll(participantsDebt);
        participantsDebtContainer.getItems().removeAll((Node) null);
    }

    private Node totalSpentNode() {
        Pane base = new Pane();
        base.setPrefWidth(participantsDebtContainer.getPrefWidth() - 20);
        base.setPrefHeight(100);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text text = new Text(uiUtils.getLanguageMap().get("debtsoverview_total_sum"));
        final double nameTopPadding = base.getPrefHeight() / 2 + 5;
        final double nameLeftPadding = 0.12f * base.getPrefWidth();
        text.setLayoutX(base.getLayoutX() + nameLeftPadding);
        text.setLayoutY(base.getLayoutY() + nameTopPadding);
        text.setFont(Font.font("SansSerif", 15));
        text.setFill(Paint.valueOf("#FFFFFF"));
        text.setMouseTransparent(true);

        double sumOfExpenses = 0.00;
        for (Expense expense : event.getExpenses()) {
            double newAmount = ExchangeProvider.convertCurrency(expense.getAmount(),
                    expense.getCurrency().toString(), clientUtils.getCurrency().toString());
            sumOfExpenses += newAmount;
        }
        sumOfExpenses = Math.round(sumOfExpenses * 100.0) / 100.0;


        Text sumText = new Text(String.valueOf(sumOfExpenses));
        final double debtLeftPadding = 0.7f * base.getPrefWidth();
        sumText.setLayoutX(base.getLayoutX() + debtLeftPadding);
        sumText.setLayoutY(base.getLayoutY() + nameTopPadding);
        sumText.setFont(Font.font("SansSerif", 15));
        sumText.setFill(Paint.valueOf("#FFFFFF"));
        sumText.setMouseTransparent(true);

        Text currency = new Text(clientUtils.getCurrency().toString());
        final double currencyLeftPadding = debtLeftPadding + sumText.getText().length() * 8;
        currency.setLayoutX(base.getLayoutX() + currencyLeftPadding);
        final double topPadding = base.getPrefHeight() / 2 + 5;
        currency.setLayoutY(base.getLayoutY() + topPadding);
        currency.setFont(Font.font("SansSerif", 15));
        currency.setFill(Paint.valueOf("#FFFFFF"));
        currency.setMouseTransparent(true);

        base.getChildren().addAll(text, sumText, currency);

        return base;
    }

    private Node debtCellFactory(String user) {
        if (debtSettler.getDebts().get(user) == 0) {
            return null;
        }
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

        double convertedValue = -1 * (debtSettler.getDebts().get(user));
        convertedValue = ExchangeProvider.convertCurrency(convertedValue,
                "EUR",
                clientUtils.getCurrency().toString());
        convertedValue = Math.round(convertedValue * 100.0) / 100.0;
        Text debt = new Text(String.valueOf(convertedValue));
        final double debtLeftPadding = 0.7f * base.getPrefWidth();
        debt.setLayoutX(base.getLayoutX() + debtLeftPadding);
        debt.setLayoutY(base.getLayoutY() + nameTopPadding);
        debt.setFont(Font.font("SansSerif", 15));
        debt.setFill(Paint.valueOf("#FFFFFF"));
        debt.setMouseTransparent(true);

        Text currency = new Text(clientUtils.getCurrency().toString());
        final double currencyLeftPadding = debtLeftPadding + debt.getText().length() * 8;
        currency.setLayoutX(base.getLayoutX() + currencyLeftPadding);
        final double topPadding = base.getPrefHeight() / 2 + 5;
        currency.setLayoutY(base.getLayoutY() + topPadding);
        currency.setFont(Font.font("SansSerif", 15));
        currency.setFill(Paint.valueOf("#FFFFFF"));
        currency.setMouseTransparent(true);

        base.getChildren().addAll(username, debt, currency);

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
