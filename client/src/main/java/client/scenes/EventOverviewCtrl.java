package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.User;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Transaction;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller for the EventOverview scene.
 * OUTLINE:
 * - BASE METHODS
 * - VISUAL EFFECTS HANDLERS
 * - CELL CLICK HANDLERS
 * - CELL FACTORIES
 * - GENERAL METHODS
 */
public class EventOverviewCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    private Event event;

    @FXML
    private AnchorPane root;
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
    private Pane participantsMenu;
    @FXML
    private ListView<Node> participantsList;
    @FXML
    private Pane participantsDarkener;
    @FXML
    private Pane addParticipantsMenu;
    @FXML
    private ListView<Node> newParticipantsList;
    @FXML
    private Pane addParticipantsDarkener;
    @FXML
    private ListView<Node> transactionContainer;
    @FXML
    private Hyperlink backLink;

    @Inject
    public EventOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UIUtils.activateHighContrastMode(root);
    }

    /**
     * Method to refresh the scene. This is needed for some reason.
     */
    public void refresh(Event event) {

        this.event = event;

        inviteCodeButton.requestFocus();

        title.setText(event.getTitle());
        title.setEditable(false);
        titleBox.setVisible(false);

        // Prepare invite button
        buttonDarkener.setVisible(false);
        clipboardPopup.setOpacity(0);

        // Ensure darkeners cover entire app
        participantsDarkener.setLayoutX(root.getLayoutX());
        participantsDarkener.setPrefWidth(root.getWidth());
        participantsDarkener.setLayoutY(root.getLayoutY());
        participantsDarkener.setPrefHeight(root.getHeight());
        addParticipantsDarkener.setLayoutX(root.getLayoutX());
        addParticipantsDarkener.setPrefWidth(root.getWidth());
        addParticipantsDarkener.setLayoutY(root.getLayoutY());
        addParticipantsDarkener.setPrefHeight(root.getHeight());

        if (participantsMenu.isVisible()) {
            toggleParticipants();
        }
        if (addParticipantsMenu.isVisible()) {
            toggleAddParticipants();
        }

        changeBackgroundColor(backLink, "transparent");

        resetTransactionsContainer();
        resetParticipantsContainer();
    }

    // ---------------- VISUAL EFFECTS HANDLERS ---------------- //

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
     * @param keyEvent the key that was just pressed
     */
    @FXML
    public void onTitleFieldDeselect(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)
                && !keyEvent.getCode().equals(KeyCode.ESCAPE)
                || title.getText().isEmpty()) {
            return;
        }
        titleBox.setVisible(false);
        title.setEditable(false);
        event.setTitle(title.getText());
        Event updated = serverUtils.updateEvent(event);
        refresh(updated);
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
     * Toggles the participants display.
     */
    @FXML
    public void toggleParticipants() {
        participantsDarkener.toFront();
        participantsDarkener.setVisible(!participantsDarkener.isVisible());
        participantsDarkener.setMouseTransparent(!participantsDarkener.isVisible());
        participantsMenu.toFront();
        participantsMenu.setVisible(!participantsMenu.isVisible());
        participantsMenu.setMouseTransparent(!participantsMenu.isVisible());
        participantsMenu.getChildren().forEach(child -> {
            child.setVisible(participantsMenu.isVisible());
            child.setMouseTransparent(participantsMenu.isMouseTransparent());
        });
    }

    /**
     * Swaps between the participants menu and the add participants menu.
     */
    public void swapParticipantsAddParticipants() {
        toggleParticipants();
        toggleAddParticipants();
    }

    /**
     * Toggles the add participants menu.
     */
    public void toggleAddParticipants() {
        addParticipantsDarkener.toFront();
        addParticipantsDarkener.setVisible(!addParticipantsDarkener.isVisible());
        addParticipantsDarkener.setMouseTransparent(!addParticipantsDarkener.isVisible());
        addParticipantsMenu.toFront();
        addParticipantsMenu.setVisible(!addParticipantsMenu.isVisible());
        addParticipantsMenu.setMouseTransparent(!addParticipantsMenu.isMouseTransparent());
        addParticipantsMenu.getChildren().forEach(child -> {
            child.setVisible(addParticipantsMenu.isVisible());
            child.setMouseTransparent(addParticipantsMenu.isMouseTransparent());
        });
        if (addParticipantsMenu.isVisible()) {
            resetNewParticipantsContainer();
        }
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

    // ---------------- CELL CLICK HANDLERS ---------------- //

    private void transactionClickHandler(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() != 2) {
            return;
        }
        Node source = (Node) mouseEvent.getSource();
        Transaction transaction = (Transaction) source.getUserData();
        if (transaction instanceof Expense) {
            // mainCtrl.showExpenseOverview((Expense) transaction);
        } else {
            // mainCtrl.showPaymentOverview((Payment) transaction);
        }
    }

    private void participantClickHandler(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Pane userContainer = (Pane) button.getParent();
        User user = (User) userContainer.getUserData();
        serverUtils.removeUserFromEvent(event.getInviteCode(), user.getEmail());
        Event updated = serverUtils.getEventByUUID(event.getInviteCode());
        refresh(updated);
        toggleParticipants();
    }

    // ---------------- CELL FACTORIES ---------------- //

    private Node transactionCellFactory(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        Node res = transaction instanceof Expense
                ? expenseCellFactory((Expense) transaction)
                : paymentCellFactory((Payment) transaction);
        res.setOnMouseClicked(this::transactionClickHandler);
        res.setUserData(transaction);
        return res;
    }

    private Node expenseCellFactory(Expense expense) {
        Pane base = new Pane();
        base.setPrefWidth(transactionContainer.getPrefWidth() - 20);
        base.setPrefHeight(100);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text expenseTitle = new Text(expense.getDescription());
        final double titleTopPadding = base.getPrefHeight() / 2 + 5;
        final double titleLeftPadding = base.getPrefWidth() / 16;
        expenseTitle.setLayoutX(base.getLayoutX() + titleLeftPadding);
        expenseTitle.setLayoutY(base.getLayoutY() + titleTopPadding);
        expenseTitle.setFont(Font.font("SansSerif", 15));
        expenseTitle.setFill(Paint.valueOf("#FFFFFF"));
        expenseTitle.setMouseTransparent(true);

        Text amount = new Text(String.valueOf(expense.getAmount()));
        final double amountTopPadding = titleTopPadding;
        final double amountLeftPadding = 3f / 4f * base.getPrefWidth();
        amount.setLayoutX(base.getLayoutX() + amountLeftPadding);
        amount.setLayoutY(base.getLayoutY() + amountTopPadding);
        amount.setFont(Font.font("SansSerif", 15));
        amount.setFill(Paint.valueOf("#FFFFFF"));
        amount.setMouseTransparent(true);

        base.getChildren().addAll(expenseTitle, amount);

        return base;
    }

    private Node paymentCellFactory(Payment payment) {
        Pane base = new Pane();
        base.setPrefWidth(transactionContainer.getPrefWidth() - 20);
        base.setPrefHeight(100);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text sender = new Text(payment.getSender());
        final double senderTopPadding = base.getPrefHeight() / 2 + 5;
        final double senderLeftPadding = base.getPrefWidth() / 16;
        sender.setLayoutX(base.getLayoutX() + senderLeftPadding);
        sender.setLayoutY(base.getLayoutY() + senderTopPadding);
        sender.setFont(Font.font("SansSerif", 15));
        sender.setFill(Paint.valueOf("#FFFFFF"));
        sender.setMouseTransparent(true);

        Text recipient = new Text(payment.getRecipient());
        final double recipientTopPadding = senderTopPadding;
        final double recipientLeftPadding = 3f / 4f * base.getPrefWidth();
        recipient.setLayoutX(base.getLayoutX() + recipientLeftPadding);
        recipient.setLayoutY(base.getLayoutY() + recipientTopPadding);
        recipient.setFont(Font.font("SansSerif", 15));
        recipient.setFill(Paint.valueOf("#FFFFFF"));
        recipient.setMouseTransparent(true);

        Text amount = new Text(String.valueOf(payment.getAmount()));
        final double amountTopPadding = senderTopPadding;
        final double amountLeftPadding = 0.4f * base.getPrefWidth();
        amount.setLayoutX(base.getLayoutX() + amountLeftPadding);
        amount.setLayoutY(base.getLayoutY() + amountTopPadding);
        amount.setFont(Font.font("SansSerif", 15));
        amount.setFill(Paint.valueOf("#FFFFFF"));
        amount.setMouseTransparent(true);

        base.getChildren().addAll(sender, recipient, amount);

        return base;
    }

    private Node userCellFactory(User user) {
        Pane base = new Pane();
        base.setPrefWidth(participantsList.getPrefWidth() - 20);
        base.setPrefHeight(50);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text username = new Text(user.getName());
        final double nameTopPadding = base.getPrefHeight() / 2 + 5;
        final double nameLeftPadding = base.getPrefWidth() / 16;
        username.setLayoutX(base.getLayoutX() + nameLeftPadding);
        username.setLayoutY(base.getLayoutY() + nameTopPadding);
        username.setFont(Font.font("SansSerif", 15));
        username.setFill(Paint.valueOf("#FFFFFF"));
        username.setMouseTransparent(true);

        Button removeButton = new Button("X");
        final double buttonTopPadding = 10;
        final double buttonLeftPadding = 3 * base.getPrefWidth() / 4;
        removeButton.setLayoutX(username.getLayoutX() + buttonLeftPadding);
        removeButton.setLayoutY(base.getLayoutY() + buttonTopPadding);
        removeButton.setFont(Font.font("SansSerif", 15));
        removeButton.setTextFill(Paint.valueOf("#FFFFFF"));
        removeButton.setStyle("-fx-background-color: transparent;");
        removeButton.setOnAction(this::participantClickHandler);

        base.getChildren().addAll(username, removeButton);
        base.setUserData(user);

        return base;
    }

    private Node newParticipantCellFactory(User user) {
        Pane base = new Pane();
        base.setPrefWidth(newParticipantsList.getPrefWidth() - 20);
        base.setPrefHeight(50);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-width: 3;"
                + " -fx-border-color: black;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text username = new Text(user.getName());
        final double nameTopPadding = base.getPrefHeight() / 2 + 5;
        final double nameLeftPadding = base.getPrefWidth() / 16;
        username.setLayoutX(base.getLayoutX() + nameLeftPadding);
        username.setLayoutY(base.getLayoutY() + nameTopPadding);
        username.setFont(Font.font("SansSerif", 15));
        username.setFill(Paint.valueOf("#FFFFFF"));
        username.setMouseTransparent(true);

        CheckBox addCheckBox = new CheckBox();
        final double checkboxTopPadding = 15;
        final double checkboxLeftPadding = 3 * base.getPrefWidth() / 4;
        addCheckBox.setLayoutX(username.getLayoutX() + checkboxLeftPadding);
        addCheckBox.setLayoutY(base.getLayoutY() + checkboxTopPadding);

        base.getChildren().addAll(username, addCheckBox);
        base.setUserData(user);

        return base;
    }

    // ---------------- GENERAL METHODS ---------------- //

    private void resetTransactionsContainer() {
        transactionContainer.getItems().removeAll(transactionContainer.getItems());
        List<Node> transactions = event
                .transactions()
                .stream()
                .map(this::transactionCellFactory)
                .toList();
        transactionContainer.getItems().addAll(transactions);
    }

    private void resetParticipantsContainer() {
        participantsList.getItems().removeAll(participantsList.getItems());
        List<Node> users = event
                .getParticipants()
                .stream()
                .map(this::userCellFactory)
                .toList();
        participantsList.getItems().addAll(users);
    }

    private void resetNewParticipantsContainer() {
        newParticipantsList.getItems().removeAll(newParticipantsList.getItems());
        List<Node> users = serverUtils.getUsers()
                .stream()
                .filter(user -> !event.getParticipants().contains(user))
                .map(this::newParticipantCellFactory)
                .toList();
        newParticipantsList.getItems().addAll(users);
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

    public void onBackClicked(MouseEvent event) {
        mainCtrl.showHomePage();
    }

    /**
     * Adds the selected participants to the event.
     */
    public void onAddParticipantsConfirm() {
        List<Pane> selectedPanes = newParticipantsList
                .getItems()
                .stream()
                .map(node -> (Pane) node)
                .filter(pane -> pane.getChildren()
                        .stream()
                        .filter(child -> child instanceof CheckBox)
                        .map(child -> (CheckBox) child)
                        .findFirst()
                        .get()
                        .isSelected())
                .toList();
        List<User> selectedUsers = selectedPanes
                .stream()
                .map(pane -> (User) pane.getUserData())
                .toList();

        Event updated = event;
        for (User u : selectedUsers) {
            updated = serverUtils.addUserToEvent(updated, u);
        }
        refresh(updated);
        toggleParticipants();
    }
}
