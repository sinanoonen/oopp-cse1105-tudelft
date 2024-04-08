package client.scenes;

import algorithms.ExchangeProvider;
import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.User;
import commons.WebSocketMessage;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Tag;
import commons.transactions.Transaction;
import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
public class EventOverviewCtrl implements Initializable, LanguageInterface {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final WebSocketServerUtils socket;

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
    private Button participantsButton;
    @FXML
    private Button debtsButton;
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
    @FXML
    private Pane errorPopup;
    @FXML
    private Pane expenseMenu;
    @FXML
    private Pane expenseDarkener;
    @FXML
    private Circle addExpense;
    @FXML
    private Button addParticipantButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button newParticipantButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Button editExpense;
    @FXML
    private Button removeExpense;
    @FXML
    private TextField filterTextField;
    @FXML
    private ChoiceBox<String> tagFilterChoiceBox;
    private boolean expenseMenuVisible = false;

    /**
     * Constructor for the EventOverview controller.
     *
     * @param serverUtils serverUtils
     * @param mainCtrl    mainCtrl
     * @param socket      socket
     */
    @Inject
    public EventOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl,
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

        tagFilterChoiceBox.setOnAction((event) -> {
            int selectedIndex = tagFilterChoiceBox.getSelectionModel().getSelectedIndex();
            Object selectedItem = tagFilterChoiceBox.getSelectionModel().getSelectedItem();

            if (tagFilterChoiceBox.getValue() != null && tagFilterChoiceBox.getValue().equals("All")) {
                tagFilterChoiceBox.setValue(null);
            }

            resetTransactionsContainer();
        });

        UIUtils.addTooltip(inviteCodeButton, "CTRL + C: Copy invite code");
        UIUtils.addTooltip(backLink, "ESC: Back");
        UIUtils.addTooltip(addExpense, "CTRL + N: Add expense");
        UIUtils.addTooltip(addParticipantButton, "CTRL + N: Add participant");
        UIUtils.addTooltip(newParticipantButton, "CTRL + N: Create participant");
        UIUtils.addTooltip(confirmButton, "ENTER: Confirm");
        UIUtils.addTooltip(filterTextField, "CTRL + F: Filter");

        root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (!participantsMenu.isVisible()               // NO MENUS OPEN
                    && !addParticipantsMenu.isVisible()
                    && !expenseMenu.isVisible()) {
                if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                    onBackClicked(null);
                    return;
                }
                if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.C)) {
                    toggleDarkenedButton(null);
                    toggleDarkenedButton(null);
                    copyInviteCode(null);
                    return;
                }
                if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.F)) {
                    filterTextField.requestFocus();
                    filterTextField.setEditable(true);
                    return;
                }
                if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.N)) {
                    onNewExpenseClicked();
                }
            } else {
                if (participantsMenu.isVisible()) {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        toggleParticipants();
                        return;
                    }
                    if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.N)) {
                        swapParticipantsAddParticipants();
                        return;
                    }
                }
                if (addParticipantsMenu.isVisible()) {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        swapParticipantsAddParticipants();
                        return;
                    }
                    if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.N)) {
                        onNewParticipantClicked();
                        return;
                    }
                    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                        onAddParticipantsConfirm();
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void updateLanguage() {
        var lm = UIUtils.getLanguageMap();
        backLink.setText(lm.get("general_back"));
        inviteCodeButton.setText(lm.get("eventoverview_invite_code"));
        participantsButton.setText(lm.get("eventoverview_participants"));
        debtsButton.setText(lm.get("eventoverview_debts"));
        filterTextField.setPromptText(lm.get("eventoverview_filter"));
        Text popupText = (Text) clipboardPopup.getChildren().getFirst();
        popupText.setText(lm.get("eventoverview_copied_to_clipboard"));
        addParticipantButton.setText(lm.get("eventoverview_add_participant"));
        closeButton.setText(lm.get("general_close"));
        confirmButton.setText(lm.get("general_confirm"));
        newParticipantButton.setText(lm.get("general_new"));
        editExpense.setText(lm.get("eventoverview_edit_expense"));
        removeExpense.setText(lm.get("eventoverview_delete_expense"));
    }

    /**
     * Method to refresh the scene.
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
        expenseDarkener.setLayoutX(root.getLayoutX());
        expenseDarkener.setPrefWidth(root.getWidth());
        expenseDarkener.setLayoutY(root.getLayoutY());
        expenseDarkener.setPrefHeight(root.getHeight());

        if (participantsMenu.isVisible()) {
            toggleParticipants();
        }
        if (addParticipantsMenu.isVisible()) {
            toggleAddParticipants();
        }
        if (expenseMenu.isVisible()) {
            toggleExpenseMenu();
        }

        changeBackgroundColor(backLink, "transparent");

        resetTransactionsContainer();
        resetParticipantsContainer();

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }

        Set<String> tags = new HashSet<>();
        for (Transaction t : event.transactions()) {
            tags.addAll(t.getTags().stream().map(Tag::getName).toList());
        }

        tagFilterChoiceBox.getItems().removeAll(tagFilterChoiceBox.getItems());
        tagFilterChoiceBox.getItems().addAll(tags);
        tagFilterChoiceBox.getItems().addFirst("All");

        resetTransactionsContainer();


        socket.registerForMessages("/topic/eventsUpdated", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                UUID uuid = UUID.fromString(message.getContent().substring(15));
                if (event != null && uuid.equals(event.getInviteCode())) {
                    UIUtils.showEventDeletedWarning(event.getTitle());
                    mainCtrl.showHomePage();
                }
            });
        });

        root.setOnMouseClicked(e -> {
            if (expenseMenuVisible && !isClickInsideNode(expenseMenu, e.getSceneX(), e.getSceneY())) {
                // Close the expenseMenu pane
                toggleExpenseMenu();
                onExit();
                mainCtrl.showEventOverview(event);
            }
        });

        updateLanguage();
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
        if (title.getText().equals(event.getTitle())) {
            return;
        }
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
     * toggles expense menu which shows edit or remove expenses.
     */
    @FXML
    public void toggleExpenseMenu() {
        expenseDarkener.toFront();
        expenseDarkener.setVisible(!expenseDarkener.isVisible());
        expenseDarkener.setMouseTransparent(!expenseDarkener.isVisible());
        expenseMenu.toFront();
        expenseMenu.setVisible(!expenseMenu.isVisible());
        expenseMenu.setMouseTransparent(!expenseMenu.isVisible());
        expenseMenu.getChildren().forEach(child -> {
            child.setVisible(expenseMenu.isVisible());
            child.setMouseTransparent(expenseMenu.isMouseTransparent());
        });
        toggleExpenseMenuVisibility(expenseMenu.isVisible());
    }

    public void toggleExpenseMenuVisibility(boolean visible) {
        expenseMenuVisible = visible;
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
        toggleExpenseMenu();
    }

    private void participantClickHandler(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Pane userContainer = (Pane) button.getParent();
        User user = (User) userContainer.getUserData();

        System.out.println(event.transactions());
        System.out.println(event.transactions()
                .stream()
                .filter(t -> t instanceof Expense)
                .map(t -> ((Expense) t).getDebts().keySet())
                .toList()
        );

        if (event.getTotalEURDebt(user) != 0) {
            HomePageCtrl.displayErrorPopup(
                    UIUtils.getLanguageMap().get("eventoverview_error_existing_debts"),
                    errorPopup
            );
            return;
        }
        serverUtils.removeUserFromEvent(event.getInviteCode(), user.getEmail());
        Event updated = serverUtils.getEventByUUID(event.getInviteCode());
        onExit();
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


        double convertedValue = expense.getAmount();
        convertedValue = ExchangeProvider.convertCurrency(convertedValue,
                expense.getCurrency().toString(),
                ClientUtils.getCurrency().toString());
        convertedValue = Math.round(convertedValue * 100.0) / 100.0;
        Text amount = new Text(String.valueOf(convertedValue));


        final double amountTopPadding = titleTopPadding;
        final double amountLeftPadding = 2.8 / 4f * base.getPrefWidth();
        amount.setLayoutX(base.getLayoutX() + amountLeftPadding);
        amount.setLayoutY(base.getLayoutY() + amountTopPadding);
        amount.setFont(Font.font("SansSerif", 15));
        amount.setFill(Paint.valueOf("#FFFFFF"));
        amount.setMouseTransparent(true);
        amount.setTextAlignment(TextAlignment.RIGHT);

        Text currency = new Text(ClientUtils.getCurrency().toString());
        final double currencyTopPadding = titleTopPadding;
        final double currencyLeftPadding = amountLeftPadding + amount.getText().length() * 9;
        currency.setLayoutX(base.getLayoutX() + currencyLeftPadding);
        currency.setLayoutY(base.getLayoutY() + currencyTopPadding);
        currency.setFont(Font.font("SansSerif", 15));
        currency.setFill(Paint.valueOf("#FFFFFF"));
        currency.setMouseTransparent(true);


        List<Tag> tags = expense.getTags().subList(0, Math.min(3, expense.getTags().size()));
        Function<Tag, Node> tagCellFactory = tag -> {
            Pane tagBase = new Pane();
            tagBase.setPrefWidth(100);
            tagBase.setPrefHeight(20);
            Color color = new Color(tag.getColor());
            String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            changeBackgroundColor(tagBase, hex);

            Text tagText = new Text(tag.getName());
            tagText.setFont(Font.font("SansSerif", 12));
            final double tagLeftPadding = 5;
            final double tagTopPadding = tagBase.getPrefHeight() / 2 + 5;
            tagText.setLayoutX(tagBase.getLayoutX() + tagLeftPadding);
            tagText.setLayoutY(tagBase.getLayoutY() + tagTopPadding);

            tagBase.getChildren().addAll(tagText);
            tagBase.setMouseTransparent(true);
            tagBase.getChildren().forEach(child -> child.setMouseTransparent(true));
            return tagBase;
        };
        List<Pane> tagNodes = tags
                .stream()
                .map(tagCellFactory)
                .map(node -> (Pane) node)
                .toList();
        final double baseTopPadding = base.getPrefHeight() / 5;
        final double tagLeftPadding = base.getPrefWidth() / 2 - 50;
        for (int i = 0; i < tagNodes.size(); i++) {
            Pane tagNode = tagNodes.get(i);
            double tagTopPadding = baseTopPadding + i * 20;
            tagNode.setLayoutX(tagLeftPadding);
            tagNode.setLayoutY(tagTopPadding);
        }

        base.getChildren().addAll(expenseTitle, amount, currency);
        base.getChildren().addAll(tagNodes);

        base.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() > 1) {
                toggleExpenseMenu();
            }
        });


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

        double convertedValue = payment.getAmount();
        convertedValue = ExchangeProvider.convertCurrency(convertedValue,
                payment.getCurrency().toString(),
                ClientUtils.getCurrency().toString());
        convertedValue = Math.round(convertedValue * 100.0) / 100.0;
        Text amount = new Text(String.valueOf(convertedValue));

        amount.setTextAlignment(TextAlignment.CENTER);
        final double amountTopPadding = senderTopPadding;
        final double amountLeftPadding = 0.4f * base.getPrefWidth();
        amount.setLayoutX(base.getLayoutX() + amountLeftPadding);
        amount.setLayoutY(base.getLayoutY() + amountTopPadding);
        amount.setFont(Font.font("SansSerif", 15));
        amount.setFill(Paint.valueOf("#FFFFFF"));
        amount.setMouseTransparent(true);

        Text currency = new Text(ClientUtils.getCurrency().toString());
        final double currencyTopPadding = senderTopPadding;
        final double currencyLeftPadding = amountLeftPadding + amount.getText().length() * 9;
        currency.setLayoutX(base.getLayoutX() + currencyLeftPadding);
        currency.setLayoutY(base.getLayoutY() + currencyTopPadding);
        currency.setFont(Font.font("SansSerif", 15));
        currency.setFill(Paint.valueOf("#FFFFFF"));
        currency.setMouseTransparent(true);

        base.getChildren().addAll(sender, recipient, amount, currency);
        base.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() > 1) {
                toggleExpenseMenu();
            }
        });

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
        base.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() > 1) {
                onExit();
                mainCtrl.showEditUser(user, event);
            }
        });

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
        base.setOnMouseClicked(mouseEvent -> addCheckBox.setSelected(!addCheckBox.isSelected()));

        return base;
    }

    // ---------------- GENERAL METHODS ---------------- //

    private void resetTransactionsContainer() {
        transactionContainer.getItems().removeAll(transactionContainer.getItems());
        List<Transaction> filteredExpenses = new ArrayList<>(event.transactions());
        filteredExpenses = filteredExpenses
                .stream()
                .filter(t -> t instanceof Expense)
                .map(t -> (Expense) t)
                .filter(t -> t.getDescription()
                        .contains(filterTextField.getText()) || t.getDebts()
                        .containsKey(filterTextField.getText()))
                .map(e -> (Transaction) e)
                .toList();

        List<Transaction> filteredTransactions = new ArrayList<>(filteredExpenses);
        for (Transaction t : event.transactions()) {
            if (t instanceof Payment) {
                filteredTransactions.add(t);
            }
        }


        if (tagFilterChoiceBox.getValue() != null) {
            filteredTransactions = filteredTransactions.stream()
                    .filter(t -> t.getTags().stream()
                            .map(Tag::getName).toList()
                            .contains(tagFilterChoiceBox.getValue()))
                    .toList();
        }



        List<Node> transactions = filteredTransactions
                .stream()
                .map(this::transactionCellFactory)
                .toList();
        transactionContainer.getItems().addAll(transactions);
    }

    public void filterTransactionTextFieldRefresher() {
        resetTransactionsContainer();
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
                .filter(user -> !event.getParticipants().contains(user)) // users not already added
                .filter(user -> user.getEventID().equals(event.getInviteCode())) // only users part of this event
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
        onExit();
        mainCtrl.showHomePage();
    }

    public void onNewParticipantClicked() {
        onExit();
        mainCtrl.showCreateUser(event);
    }

    public void onDebtsClicked() {
        onExit();
        mainCtrl.showDebtOverview(event);
    }

    public void onNewExpenseClicked() {
        onExit();
        mainCtrl.showAddExpense(event);
    }

    /**
     * Removes expenses from UI and server.
     */
    public void removeExpense() {
        Node selectedNode = transactionContainer.getSelectionModel().getSelectedItem();
        if (selectedNode != null) {
            Expense expenseToRemove = (Expense) selectedNode.getUserData();
            serverUtils.removeExpense(event.getInviteCode(), expenseToRemove);
            event.removeTransaction(expenseToRemove);
            resetTransactionsContainer();
        }
        onExit();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Edits expense.
     */
    public void editExpense() {
        Node selectedNode = transactionContainer.getSelectionModel().getSelectedItem();
        if (selectedNode != null) {
            Expense expenseToUpdate = (Expense) selectedNode.getUserData();
            onExit();
            mainCtrl.showEditExpense(event, expenseToUpdate);
        }

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
        onExit();
        refresh(updated);
        toggleParticipants();
    }

    private boolean isClickInsideNode(Node node, double sceneX, double sceneY) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        return bounds.contains(sceneX, sceneY);
    }


    /**
     * Unsubscribe from sockets and any other clean-up code.
     */
    public void onExit() {
        socket.unregisterFromMessages("/topic/eventsUpdated");
    }
}
