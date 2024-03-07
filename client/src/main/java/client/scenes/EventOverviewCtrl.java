package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Transaction;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller for the EventOverview scene.
 */
public class EventOverviewCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    private Event event;

    @FXML
    private TextField title;
    @FXML
    private Pane titleBox;
    @FXML
    private Button inviteCodeButton;
    @FXML
    private Pane buttonDarkener;
    @FXML
    private StackPane clipboardPopup;
    @FXML
    private ListView<Node> transactionContainer;

    @Inject
    public EventOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void foo() {
        refresh(event);
    }

    /**
     * Method to refresh the scene. This is needed for some reason.
     */
    public void refresh(Event event) {
        this.event = event;

        System.out.println("Refreshing event overview");

        title.setEditable(false);
        titleBox.setVisible(false);

        buttonDarkener.setVisible(false);
        clipboardPopup.setOpacity(0);

        transactionContainer.getItems().removeAll(transactionContainer.getItems());
        List<Node> items = event
                .getTransactions()
                .stream()
                .map(this::transactionCellFactory)
                .toList();
        transactionContainer.getItems().addAll(items);


    }

    /**
     * Function for allowing user to edit the title on double-click.
     *
     * @param event MouseEvent from user
     */
    @FXML
    public void onTitleFieldClicked(MouseEvent event) {
        if (!(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)) {
            return;
        }
        title.setEditable(true);
        titleBox.setVisible(true);
    }

    /**
     * Handles deselecting the title text box.
     *
     * @param event the key that was just pressed
     */
    @FXML
    public void onTitleFieldDeselect(KeyEvent event) {
        if (!event.getCode().equals(KeyCode.ENTER)
                && !event.getCode().equals(KeyCode.ESCAPE)
                || title.getText().isEmpty()) {
            return;
        }

        titleBox.setVisible(false);
        title.setEditable(false);
    }

    /**
     * Onclick event to copy the event's invite code to the user's clipboard.
     *
     * @param event MouseEvent
     */
    @FXML
    public void copyInviteCode(MouseEvent event) {
        String inviteCode = this.event.getInviteCode().toString();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(inviteCode);
        clipboard.setContent(content);
    }

    /**
     * Darkens the invite code button on click, and un-darkens on release.
     * Also handles the "copied to clipboard" popup animation.
     *
     * @param event MouseEvent
     */
    @FXML
    public void toggleDarkenedButton(MouseEvent event) {
        buttonDarkener.setVisible(!buttonDarkener.isVisible());
        if (!buttonDarkener.isVisible()) {
            clipboardPopup.toFront();
            FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(0.5), clipboardPopup);
            fadeInTransition.setFromValue(0);
            fadeInTransition.setToValue(1);

            FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(0.5), clipboardPopup);
            fadeOutTransition.setFromValue(1);
            fadeOutTransition.setToValue(0);
            fadeOutTransition.setDelay(Duration.seconds(1));

            fadeInTransition.setOnFinished(finished -> fadeOutTransition.play());

            fadeInTransition.play();
        }
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

    private Node transactionCellFactory(Transaction transaction) {
        return transaction instanceof Expense
                ? expenseCellFactory((Expense) transaction)
                : paymentCellFactory((Payment) transaction);
    }

    private Node expenseCellFactory(Expense expense) {
        Pane base = new Pane();
        base.setPrefWidth(transactionContainer.getPrefWidth() - 10);
        base.setPrefHeight(100);
        base.setStyle("-fx-background-color: #444444; -fx-border-width: 3; -fx-border-color: black;");

        Text expenseTitle = new Text(expense.getDescription());
        final double titleTopPadding = base.getPrefHeight() / 2 + 5;
        final double titleLeftPadding = base.getPrefWidth() / 16;
        expenseTitle.setLayoutX(base.getLayoutX() + titleLeftPadding);
        expenseTitle.setLayoutY(base.getLayoutY() + titleTopPadding);
        expenseTitle.setFont(Font.font("SansSerif", 15));
        expenseTitle.setFill(Paint.valueOf("#FFFFFF"));

        Text amount = new Text(String.valueOf(expense.getAmount()));
        final double amountTopPadding = titleTopPadding;
        final double amountLeftPadding = 3f / 4f * base.getPrefWidth();
        amount.setLayoutX(base.getLayoutX() + amountLeftPadding);
        amount.setLayoutY(base.getLayoutY() + amountTopPadding);
        amount.setFont(Font.font("SansSerif", 15));
        amount.setFill(Paint.valueOf("#FFFFFF"));

        base.getChildren().addAll(expenseTitle, amount);

        return base;
    }

    private Node paymentCellFactory(Payment payment) {
        Pane base = new Pane();
        base.setPrefWidth(transactionContainer.getPrefWidth() - 20);
        base.setPrefHeight(100);
        base.setStyle("-fx-background-color: #444444; -fx-border-width: 3; -fx-border-color: black;");
        return base;
    }

}
