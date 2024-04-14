package client.scenes;

import algorithms.ExchangeProvider;
import client.enums.Language;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.scene.text.FontWeight;
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
    private final UIUtils uiUtils;
    private final ClientUtils clientUtils;

    private Event event;
    private Map<Language, Image> flags;
    private Thread pollingThread;

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
    private Pane expenseDetails;
    @FXML
    private Pane expenseDetailsDarkener;
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
    private Button expenseDetailsButton;
    @FXML
    private TextField filterTextField;
    @FXML
    private ChoiceBox<String> tagFilterChoiceBox;
    @FXML
    private ChoiceBox<String> filterOwnerChoiceBox;
    @FXML
    private ChoiceBox<String> filterbyParticipantChoiceBox;
    private boolean expenseMenuVisible = false;
    private boolean expenseDetailsVisible = false;
    @FXML
    private Text expenseDetailsText;
    @FXML
    private Text titleDescription;
    @FXML
    private Text expenseDescription;
    @FXML
    private Text titleDate;
    @FXML
    private Text expenseDate;
    @FXML
    private Text titleOwner;
    @FXML
    private Text expenseOwner;
    @FXML
    private Text titleAmount;
    @FXML
    private Text expenseAmount;
    @FXML
    private Text titleTags;
    @FXML
    private Text expenseTags;
    @FXML
    private Text titleParticipants;
    @FXML
    private Text involvedParticipants;
    @FXML
    private Text expenseIncludes;
    @FXML
    private ComboBox<Language> languageDropdown;

    /**
     * Constructor for the EventOverview controller.
     *
     * @param serverUtils serverUtils
     * @param mainCtrl    mainCtrl
     * @param socket      socket
     * @param uiUtils    uiUtils
     * @param clientUtils  clientUtils
     */
    @Inject
    public EventOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl,
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

        serverUtils.longPollEvents(e -> {
            // If we have not yet opened the overview ever or if we are not looking at the event
            if (event == null
                    || !e.equals(event.getInviteCode().toString())) {
                return;
            }
            boolean hadParticipantsOpen = participantsMenu.isVisible();
            boolean hadAddParticipantsOpen = addParticipantsMenu.isVisible();
            onExit();
            refresh(serverUtils.getEventByUUID(UUID.fromString(e)));
            if (hadParticipantsOpen) {
                toggleParticipants();
                return;
            }
            if (hadAddParticipantsOpen) {
                toggleAddParticipants();
            }
        });

        tagFilterChoiceBox.setOnAction((event) -> {
            int selectedIndex = tagFilterChoiceBox.getSelectionModel().getSelectedIndex();
            Object selectedItem = tagFilterChoiceBox.getSelectionModel().getSelectedItem();

            if (tagFilterChoiceBox.getValue() != null && tagFilterChoiceBox.getValue().equals("All")) {
                tagFilterChoiceBox.setValue(null);
            }

            resetTransactionsContainer();
        });

        if (filterOwnerChoiceBox != null) {
            filterOwnerChoiceBox.setOnAction((event) -> {
                int selectedIndex = filterOwnerChoiceBox.getSelectionModel().getSelectedIndex();
                Object selectedItem = filterOwnerChoiceBox.getSelectionModel().getSelectedItem();

                if (filterOwnerChoiceBox.getValue() != null
                        && filterOwnerChoiceBox.getValue().equals("No filter.")) {
                    filterOwnerChoiceBox.setValue(null);
                }

                resetTransactionsContainer();
            });

        }
        if (filterbyParticipantChoiceBox != null) {
            filterbyParticipantChoiceBox.setOnAction((event) -> {
                int selectedIndex = filterbyParticipantChoiceBox.getSelectionModel().getSelectedIndex();
                Object selectedItem = filterbyParticipantChoiceBox.getSelectionModel().getSelectedItem();

                if (filterbyParticipantChoiceBox
                        .getValue() != null
                        && filterbyParticipantChoiceBox
                        .getValue()
                        .equals("No filter.")
                ) {
                    filterbyParticipantChoiceBox.setValue(null);
                }

                resetTransactionsContainer();
            });
        }


        uiUtils.addTooltip(inviteCodeButton, "CTRL + C: Copy invite code");
        uiUtils.addTooltip(backLink, "ESC: Back");
        uiUtils.addTooltip(addExpense, "CTRL + N: Add expense");
        uiUtils.addTooltip(addParticipantButton, "CTRL + N: Add participant");
        uiUtils.addTooltip(newParticipantButton, "CTRL + N: Create participant");
        uiUtils.addTooltip(confirmButton, "ENTER: Confirm");
        uiUtils.addTooltip(filterTextField, "CTRL + F: Filter");

        root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (!participantsMenu.isVisible()               // NO MENUS OPEN
                    && !addParticipantsMenu.isVisible()
                    && !expenseMenu.isVisible()) {
                if (keyEvent.getCode().equals(KeyCode.ESCAPE) && !title.isEditable()) {
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

        flags = new HashMap<>();
        for (Language language : Language.values()) {
            Image img = new Image(
                    "client/img/flag_" + language.name().toLowerCase() + ".png",
                    35, 20, false, true);
            flags.put(language, img);
        }

        class ImageCell extends ListCell<Language> {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(new ImageView(flags.get(item)));
                }
            }
        }

        languageDropdown.setCellFactory(lv -> new ImageCell());
        languageDropdown.setButtonCell(new ImageCell());
        languageDropdown.setOnAction(event -> {
            clientUtils.setLanguage(languageDropdown.getValue());
            updateLanguage();
        });
        languageDropdown.getItems().addAll(flags.keySet());
    }

    @Override
    public void updateLanguage() {
        var lm = uiUtils.getLanguageMap();
        backLink.setText(lm.get("general_back"));
        inviteCodeButton.setText(lm.get("eventoverview_invite_code"));
        participantsButton.setText(lm.get("eventoverview_participants"));
        debtsButton.setText(lm.get("eventoverview_debts"));
        filterTextField.setPromptText(lm.get("eventoverview_filter"));
        expenseIncludes.setText(lm.get("eventoverview_expense_includes"));
        Text popupText = (Text) clipboardPopup.getChildren().getFirst();
        popupText.setText(lm.get("eventoverview_copied_to_clipboard"));

        addParticipantButton.setText(lm.get("eventoverview_add_participant"));
        closeButton.setText(lm.get("general_close"));
        confirmButton.setText(lm.get("general_confirm"));
        newParticipantButton.setText(lm.get("general_new"));

        editExpense.setText(lm.get("eventoverview_edit_expense"));
        removeExpense.setText(lm.get("eventoverview_delete_expense"));
        expenseDetailsButton.setText(lm.get("eventoverview_expense_details"));
        expenseDetailsText.setText(lm.get("eventoverview_expense_details"));
        titleDescription.setText(lm.get("addexpense_description") + ": ");
        titleDate.setText(lm.get("addexpense_date") + ": ");
        titleOwner.setText(lm.get("addexpense_sponsor") + ": ");
        titleAmount.setText(lm.get("addexpense_quantity") + ": ");
        titleParticipants.setText(lm.get("eventoverview_expense_involved") + ": ");
        titleTags.setText(lm.get("eventoverview_expense_tags") + ": ");
    }

    /**
     * Method to refresh the scene.
     */
    public void refresh(Event event) {

        this.event = event;
        mainCtrl.getPrimaryStage().setTitle(event.getTitle());

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
        expenseDetailsDarkener.setLayoutX(root.getLayoutX());
        expenseDetailsDarkener.setPrefWidth(root.getWidth());
        expenseDetailsDarkener.setLayoutY(root.getLayoutY());
        expenseDetailsDarkener.setPrefHeight(root.getHeight());


        if (participantsMenu.isVisible()) {
            toggleParticipants();
        }
        if (addParticipantsMenu.isVisible()) {
            toggleAddParticipants();
        }
        if (expenseMenu.isVisible()) {
            toggleExpenseMenu();
        }
        if (expenseDetails.isVisible()) {
            toggleExpenseDetails();
        }

        changeBackgroundColor(backLink, "transparent");

        resetTransactionsContainer();
        resetNewParticipantsContainer();
        resetParticipantsContainer();

        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
        } else {
            uiUtils.deactivateHighContrastMode(root);
        }

        Set<String> tags = new HashSet<>();
        Set<String> participants = new HashSet<>();
        for (Transaction t : event.transactions()) {
            tags.addAll(t.getTags().stream().map(Tag::getName).toList());
            participants.add(t.getOwner());
            if (t instanceof Expense) {
                participants.addAll(((Expense) t).getDebts().keySet());
            }
        }


        String all = uiUtils.getLanguageMap().get("eventoverview_all");
        String noFilter = uiUtils.getLanguageMap().get("eventoverview_no_filter");
        tagFilterChoiceBox.getItems().removeAll(tagFilterChoiceBox.getItems());
        tagFilterChoiceBox.getItems().addAll(tags);
        tagFilterChoiceBox.getItems().addFirst(all);

        if (filterOwnerChoiceBox != null) {
            filterOwnerChoiceBox.getItems().removeAll(filterOwnerChoiceBox.getItems());
            filterOwnerChoiceBox.getItems().addAll(participants);
            filterOwnerChoiceBox.getItems().addFirst(noFilter);
        }

        if (filterbyParticipantChoiceBox != null) {
            filterbyParticipantChoiceBox.getItems().removeAll(filterbyParticipantChoiceBox.getItems());
            filterbyParticipantChoiceBox.getItems().addAll(participants);
            filterbyParticipantChoiceBox.getItems().addFirst(noFilter);
        }



        resetTransactionsContainer();


        socket.registerForMessages("/topic/eventsUpdated", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                UUID uuid = UUID.fromString(message.getContent().substring(15));
                if (event != null && uuid.equals(event.getInviteCode())) {
                    uiUtils.showEventDeletedWarning(event.getTitle());
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
            if (expenseDetailsVisible && !isClickInsideNode(expenseDetails, e.getSceneX(), e.getSceneY())) {
                toggleExpenseDetails();
            }
        });

        languageDropdown.setValue(clientUtils.getLanguage());
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
        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
            title.undo();
        }
        titleBox.setVisible(false);
        title.setEditable(false);
        if (title.getText().equals(event.getTitle())) {
            return;
        }
        event.setTitle(title.getText());
        Event updated = serverUtils.updateEvent(event);
        onExit();
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

    public void toggleExpenseDetailsVisibility(boolean visible) {
        expenseDetailsVisible = visible;
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
     * Toggles expense details.
     */
    public void toggleExpenseDetails() {
        expenseDetailsDarkener.toFront();
        expenseDetailsDarkener.setVisible(!expenseDetailsDarkener.isVisible());
        expenseDetailsDarkener.setMouseTransparent(!expenseDetailsDarkener.isVisible());
        expenseDetails.toFront();
        expenseDetails.setVisible(!expenseDetails.isVisible());
        expenseDetails.setMouseTransparent(!expenseDetails.isMouseTransparent());
        expenseDetails.getChildren().forEach(child -> {
            child.setVisible(expenseDetails.isVisible());
            child.setMouseTransparent(expenseDetails.isMouseTransparent());
        });
        Node selectedNode = transactionContainer.getSelectionModel().getSelectedItem();
        String participants = "";
        String tags = "";
        if (selectedNode != null) {
            Expense expense = (Expense) selectedNode.getUserData();
            expenseDescription.setText(expense.getDescription());
            expenseDescription.setFont(Font.font("SansSerif", 12));
            expenseDescription.setFill(Paint.valueOf("#FFFFFF"));
            expenseDescription.setLayoutX(titleDescription.getLayoutX() + 100);
            expenseDescription.setLayoutY(titleDescription.getLayoutY());
            titleDescription.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));


            expenseDate.setText(expense.getDate().toString());
            expenseDate.setFont(Font.font("SansSerif", 12));
            expenseDate.setFill(Paint.valueOf("#FFFFFF"));
            expenseDate.setLayoutX(titleDate.getLayoutX() + 100);
            expenseDate.setLayoutY(titleDate.getLayoutY());
            titleDate.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

            expenseOwner.setText(expense.getOwner());
            expenseOwner.setFont(Font.font("SansSerif", 12));
            expenseOwner.setFill(Paint.valueOf("#FFFFFF"));
            expenseOwner.setLayoutX(titleOwner.getLayoutX() + 100);
            expenseOwner.setLayoutY(titleOwner.getLayoutY());
            titleOwner.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

            expenseAmount.setText(String.valueOf(expense.getAmount()));
            expenseAmount.setFont(Font.font("SansSerif", 12));
            expenseAmount.setFill(Paint.valueOf("#FFFFFF"));
            expenseAmount.setLayoutX(titleAmount.getLayoutX() + 100);
            expenseAmount.setLayoutY(titleAmount.getLayoutY());
            titleAmount.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
            for (String p : expense.getDebts().keySet()) {
                participants += p;
                participants += ", ";
            }
            if (participants.length() > 2) {
                participants = participants.substring(0, participants.length() - 2);
            }
            involvedParticipants.setText(participants);
            involvedParticipants.setFont(Font.font("SansSerif", 12));
            involvedParticipants.setFill(Paint.valueOf("#FFFFFF"));
            involvedParticipants.setLayoutX(titleParticipants.getLayoutX() + 100);
            involvedParticipants.setLayoutY(titleParticipants.getLayoutY());
            titleParticipants.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

            for (Tag t : expense.getTags()) {
                tags += t.getName();
                tags += ", ";
            }
            if (tags.length() > 2) {
                tags = tags.substring(0, tags.length() - 2);
            }
            expenseTags.setText(tags);
            expenseTags.setFont(Font.font("SansSerif", 12));
            expenseTags.setFill(Paint.valueOf("#FFFFFF"));
            expenseTags.setLayoutX(titleTags.getLayoutX() + 100);
            expenseTags.setLayoutY(titleTags.getLayoutY());
            titleTags.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
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
            toggleExpenseMenu();
        }
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
                    uiUtils.getLanguageMap().get("eventoverview_error_existing_debts"),
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

    Node expenseCellFactory(Expense expense) {
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
                clientUtils.getCurrency().toString());
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

        Text currency = new Text(clientUtils.getCurrency().toString());
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
                clientUtils.getCurrency().toString());
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

        Text currency = new Text(clientUtils.getCurrency().toString());
        final double currencyTopPadding = senderTopPadding;
        final double currencyLeftPadding = amountLeftPadding + amount.getText().length() * 9;
        currency.setLayoutX(base.getLayoutX() + currencyLeftPadding);
        currency.setLayoutY(base.getLayoutY() + currencyTopPadding);
        currency.setFont(Font.font("SansSerif", 15));
        currency.setFill(Paint.valueOf("#FFFFFF"));
        currency.setMouseTransparent(true);

        base.getChildren().addAll(sender, recipient, amount, currency);

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
                .filter(t -> (t.getDescription()
                        .contains(filterTextField.getText()) || t.getDebts()
                        .containsKey(filterTextField.getText()))
                        && (filterbyParticipantChoiceBox.getValue() == null
                        || t.getDebts().containsKey(filterbyParticipantChoiceBox.getValue())))
                .map(e -> (Transaction) e)
                .toList();


        List<Transaction> filteredTransactions = new ArrayList<>(filteredExpenses);
        for (Transaction t : event.transactions()) {
            if (t instanceof Payment
                    && (t.getOwner().contains(filterTextField.getText())
                    && (filterOwnerChoiceBox
                        .getValue() == null
                        || t
                        .getOwner().equals(filterOwnerChoiceBox.getValue()))
                    && (filterbyParticipantChoiceBox
                    .getValue() == null
                        || ((Payment) t).getRecipient().equals(filterbyParticipantChoiceBox.getValue())))) {
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

        if (filterOwnerChoiceBox != null && filterOwnerChoiceBox.getValue() != null) {
            filteredTransactions = filteredTransactions.stream()
                    .filter(t -> t.getOwner().equals(filterOwnerChoiceBox.getValue()))
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
        if (node == null) {
            return;
        }

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

    public Event getEvent() {
        return event;
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }

    public TextField getTitle() {
        return title;
    }

    public void setTitle(TextField title) {
        this.title = title;
    }

    public void setTitleBox(Pane titleBox) {
        this.titleBox = titleBox;
    }

    public Button getInviteCodeButton() {
        return inviteCodeButton;
    }

    public void setInviteCodeButton(Button inviteCodeButton) {
        this.inviteCodeButton = inviteCodeButton;
    }

    public void setParticipantsButton(Button participantsButton) {
        this.participantsButton = participantsButton;
    }

    public void setDebtsButton(Button debtsButton) {
        this.debtsButton = debtsButton;
    }

    public void setButtonDarkener(Pane buttonDarkener) {
        this.buttonDarkener = buttonDarkener;
    }

    public StackPane getClipboardPopup() {
        return clipboardPopup;
    }

    public void setClipboardPopup(StackPane clipboardPopup) {
        this.clipboardPopup = clipboardPopup;
    }

    public Pane getParticipantsMenu() {
        return participantsMenu;
    }

    public void setParticipantsMenu(Pane participantsMenu) {
        this.participantsMenu = participantsMenu;
    }

    public ListView<Node> getParticipantsList() {
        return participantsList;
    }

    public void setParticipantsList(ListView<Node> participantsList) {
        this.participantsList = participantsList;
    }

    public void setParticipantsDarkener(Pane participantsDarkener) {
        this.participantsDarkener = participantsDarkener;
    }

    public void setExpenseDetails(Pane expenseDetails) {
        this.expenseDetails = expenseDetails;
    }

    public void setExpenseDetailsDarkener(Pane expenseDetailsDarkener) {
        this.expenseDetailsDarkener = expenseDetailsDarkener;
    }

    public void setAddParticipantsMenu(Pane addParticipantsMenu) {
        this.addParticipantsMenu = addParticipantsMenu;
    }

    public void setNewParticipantsList(ListView<Node> newParticipantsList) {
        this.newParticipantsList = newParticipantsList;
    }

    public void setAddParticipantsDarkener(Pane addParticipantsDarkener) {
        this.addParticipantsDarkener = addParticipantsDarkener;
    }

    public void setTransactionContainer(
        ListView<Node> transactionContainer) {
        this.transactionContainer = transactionContainer;
    }

    public void setBackLink(Hyperlink backLink) {
        this.backLink = backLink;
    }

    public Pane getErrorPopup() {
        return errorPopup;
    }

    public void setErrorPopup(Pane errorPopup) {
        this.errorPopup = errorPopup;
    }

    public void setExpenseMenu(Pane expenseMenu) {
        this.expenseMenu = expenseMenu;
    }

    public void setExpenseDarkener(Pane expenseDarkener) {
        this.expenseDarkener = expenseDarkener;
    }

    public void setAddExpense(Circle addExpense) {
        this.addExpense = addExpense;
    }

    public void setAddParticipantButton(Button addParticipantButton) {
        this.addParticipantButton = addParticipantButton;
    }

    public void setCloseButton(Button closeButton) {
        this.closeButton = closeButton;
    }

    public void setNewParticipantButton(Button newParticipantButton) {
        this.newParticipantButton = newParticipantButton;
    }

    public void setConfirmButton(Button confirmButton) {
        this.confirmButton = confirmButton;
    }

    public void setEditExpense(Button editExpense) {
        this.editExpense = editExpense;
    }

    public void setRemoveExpense(Button removeExpense) {
        this.removeExpense = removeExpense;
    }

    public void setFilterTextField(TextField filterTextField) {
        this.filterTextField = filterTextField;
    }

    public void setTagFilterChoiceBox(ChoiceBox<String> tagFilterChoiceBox) {
        this.tagFilterChoiceBox = tagFilterChoiceBox;
    }


    public void setLanguageDropdown(ComboBox<Language> languageDropdown) {
        this.languageDropdown = languageDropdown;
    }
}
