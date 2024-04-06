package client.scenes;

import static client.scenes.HomePageCtrl.fadeInOutPopup;

import client.utils.*;
import commons.Currency;
import commons.Event;
import commons.User;
import commons.WebSocketMessage;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Tag;
import jakarta.ws.rs.WebApplicationException;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javax.inject.Inject;

/**
 * Controller for adding an expense to an event.
 */
public class AddExpenseCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final WebSocketServerUtils socket;


    @FXML
    private AnchorPane root;
    @FXML
    private Label title;
    @FXML
    private ChoiceBox<String> whoPaid;
    @FXML
    private TextField description;
    @FXML
    private TextField amount;
    @FXML
    private ChoiceBox<Currency> currencyChoiceBox;
    @FXML
    private CheckBox equallyEverybody;
    @FXML
    private CheckBox onlySomePeople;
    @FXML
    private VBox additionalCheckboxesContainer;
    @FXML
    private List<CheckBox> additionalCheckboxes = new ArrayList<>();
    @FXML
    private ChoiceBox<Tag> expenseTags;
    @FXML
    private ListView<Tag> selectedTags;

    @FXML
    private DatePicker datePicker;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;
    @FXML
    private Button addTag;
    @FXML
    private Pane errorPopup;

    private Event event;
    private List<String> participants = new ArrayList<>();
    private final List<Currency> currencies = List.of(Currency.values());
    private Set<Tag> tags;
    private ManageExpenseMode mode;
    private Expense expenseToUpdate = null;

    /**
     * The constructor for the controller.
     *
     * @param server the server
     * @param mainCtrl the main controller
     * @param socket the web socket
     */
    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl, WebSocketServerUtils socket) {
        System.out.println("Constructing add expense ctrl");
        this.server = server;
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
     * Refreshes the scene.
     *
     * @param event event the expense belongs to
     */
    public void refresh(ManageExpenseMode mode, Event event, Expense expense) {
        this.event = event;
        this.tags = event.getTags();
        this.mode = mode;

        socket.registerForMessages("/topic/eventsUpdated", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                UUID uuid = UUID.fromString(message.getContent().substring(15));
                if (event != null && uuid.equals(event.getInviteCode())) {
                    UIUtils.showEventDeletedWarning(event.getTitle());
                    mainCtrl.showHomePage();
                }
            });
        });

        whoPaid.getItems().clear();
        currencyChoiceBox.getItems().clear();
        expenseTags.getItems().clear();
        selectedTags.getItems().clear();
        clearFields();

        if (mode.equals(ManageExpenseMode.CREATE)) {
            title.setText("Add Expense");
            participants = event.getParticipants().stream()
                    .map(User::getName)
                    .toList();
            whoPaid.getItems().addAll(participants);
            currencyChoiceBox.getItems().addAll(currencies);
            expenseTags.getItems().addAll(tags);
            expenseTags.setConverter(new StringConverter<Tag>() {
                @Override
                public String toString(Tag tag) {
                    return tag != null ? tag.getName() : "";
                }

                @Override
                public Tag fromString(String string) {
                    return null;
                }
            });
        } else {
            participants = event.getParticipants().stream()
                    .map(User::getName)
                    .toList();
            whoPaid.getItems().addAll(participants);
            currencyChoiceBox.getItems().addAll(currencies);
            expenseTags.getItems().addAll(tags);
            if (expense.isSplitEqually()) {
                equallyEverybody.setSelected(true);
            } else {
                onlySomePeople.setSelected(true);
                List<String> selectedDebtors = expense.getDebts().keySet().stream().toList();

                additionalCheckboxesContainer.getChildren().clear();

                for (String participant : participants) {
                    CheckBox checkBox = new CheckBox(participant);
                    additionalCheckboxes.add(checkBox);
                    additionalCheckboxesContainer.getChildren().add(checkBox);
                }
                for (Node node : additionalCheckboxesContainer.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) node;
                        if (selectedDebtors.contains(checkBox.getText())
                                && !checkBox.getText().equals(whoPaid.getValue())) {
                            checkBox.setSelected(true);
                        }
                    }
                }
            }
            expenseTags.setConverter(new StringConverter<Tag>() {
                @Override
                public String toString(Tag tag) {
                    return tag != null ? tag.getName() : "";
                }

                @Override
                public Tag fromString(String string) {
                    return null;
                }
            });

            title.setText("Edit Expense");
            whoPaid.setValue(expense.getOwner());
            description.setText(expense.getDescription());
            amount.setText(Float.toString(expense.getAmount()));
            datePicker.setValue(expense.getDate());
            currencyChoiceBox.setValue(expense.getCurrency());
            selectedTags.getItems().addAll(expense.getTags());
            setupListViewCellFactory();

        }
        equallyEverybody.setOnAction(e -> {
            if (equallyEverybody.isSelected()) {
                onlySomePeople.setSelected(false);
                equallyEverybody.setSelected(true);
            }
            for (CheckBox checkBox : additionalCheckboxes) {
                checkBox.setSelected(false);
            }
        });
    }

    /**
     * Refreshes the scene.
     *
     * @param event event the payment belongs to
     */
    public void refresh(ManageExpenseMode mode, Event event, Payment payment) {
        this.event = event;
        this.tags = event.getTags();
        this.mode = mode;
        if (mode == ManageExpenseMode.CREATE) {
            participants = event.getParticipants().stream()
                    .map(User::getName)
                    .toList();
            whoPaid.getItems().addAll(participants);
            currencyChoiceBox.getItems().addAll(currencies);
            expenseTags.getItems().addAll(tags);
            expenseTags.setConverter(new StringConverter<Tag>() {
                @Override
                public String toString(Tag tag) {
                    return tag != null ? tag.getName() : "";
                }

                @Override
                public Tag fromString(String string) {
                    return null;
                }
            });
        } else {
            participants = event.getParticipants().stream()
                    .map(User::getName)
                    .toList();
            whoPaid.getItems().addAll(participants);
            currencyChoiceBox.getItems().addAll(currencies);
            currencyChoiceBox.setValue(payment.getCurrency());
            expenseTags.getItems().addAll(tags);
            expenseTags.setConverter(new StringConverter<Tag>() {
                @Override
                public String toString(Tag tag) {
                    return tag != null ? tag.getName() : "";
                }

                @Override
                public Tag fromString(String string) {
                    return null;
                }
            });
            title.setText("Edit Payment");
            whoPaid.setValue(payment.getOwner());
            //description.setText(payment.getDescription());
            amount.setText(Float.toString(payment.getAmount()));
            datePicker.setValue(payment.getDate());

            selectedTags.getItems().addAll(payment.getTags());
            setupListViewCellFactory();
        }
        equallyEverybody.setOnAction(e -> {
            if (equallyEverybody.isSelected()) {
                onlySomePeople.setSelected(false);
            }
            for (CheckBox checkBox : additionalCheckboxes) {
                checkBox.setSelected(false);
            }
        });
    }

    /**
     * Shows the participants when clicked only some people button.
     */
    public void handleOnlySomePeople() {
        if (onlySomePeople.isSelected()) {
            equallyEverybody.setSelected(false);
        }

        if (onlySomePeople.isSelected()) {
            additionalCheckboxesContainer.getChildren().clear();

            for (String participant : participants) {
                CheckBox checkBox = new CheckBox(participant);
                additionalCheckboxes.add(checkBox);
                additionalCheckboxesContainer.getChildren().add(checkBox);
            }
        } else {
            additionalCheckboxesContainer.getChildren().clear();
            additionalCheckboxes.clear();
        }
    }

    /**
     * Controls the add new tag button.
     */
    @FXML
    private void handleAddTagButtonClick() {
        // Retrieve the selected item from the ChoiceBox
        Tag selectedTag = expenseTags.getValue();

        if (selectedTag != null && !selectedTags.getItems().contains(selectedTag)) {
            // Add the selected tag to the ListView
            selectedTags.getItems().add(selectedTag);
        }
        setupListViewCellFactory();

    }

    /**
     * Sets up the list view cell factory for the tags.
     */
    public void setupListViewCellFactory() {
        selectedTags.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
            @Override
            public ListCell<Tag> call(ListView<Tag> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Tag tag, boolean empty) {
                        super.updateItem(tag, empty);
                        if (tag == null || empty) {
                            setText(null);
                            setGraphic(null); // Clear the graphic if the cell is empty
                        } else {
                            // Display the name of the tag
                            setText(tag.getName());

                            // Create a button for removing the tag
                            Button removeButton = new Button("X");
                            removeButton.setStyle("-fx-background-color: transparent;");
                            removeButton.setOnAction(e -> {
                                // Retrieve the selected tag and remove it from the list
                                Tag selectedTag = getItem();
                                if (selectedTag != null) {
                                    getListView().getItems().remove(selectedTag);
                                }
                            });

                            // Set the button as the graphic for the cell
                            setGraphic(removeButton);

                            // Set the background color of the cell
                            int color = tag.getColor();
                            String newColor = intToString(color);
                            setBackground(new Background(new BackgroundFill(Color.web(newColor),
                                    CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }
                };
            }
        });
    }

    /**
     * Converts rgb color int to css parsable string.
     *
     * @param rgb color
     * @return colors string representation
     */
    public String intToString(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    @FXML
    private void handleCancelButtonClick() {
        // Handle cancel button click
        onExit();
        mainCtrl.showEventOverview(event);
    }

    @FXML
    private void handleAddButtonClick() {
        try {
            if (mode == ManageExpenseMode.CREATE) {
                create();
            } else {
                update(expenseToUpdate);
            }

        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();

        }
        onExit();
    }

    private void clearFields() {
        whoPaid.setValue(null);
        description.clear();
        amount.clear();
        datePicker.setValue(null);
        equallyEverybody.setSelected(false);
        onlySomePeople.setSelected(false);

        for (CheckBox cb : additionalCheckboxes) {
            cb.setSelected(false);
        }
        selectedTags.getItems().clear();
    }

    /**
     * Handle key events.
     *
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                handleAddButtonClick();
                break;
            case ESCAPE:
                handleCancelButtonClick();
                break;
            default:
                break;
        }
    }

    /**
     * Creates new expense.
     */
    public void create() {
        if (!validateInputs()) {
            return;
        }
        String owner = whoPaid.getValue();
        String expenseDescription = description.getText();
        float expenseAmount = Float.parseFloat(amount.getText());
        LocalDate expenseDate = datePicker.getValue();
        List<String> debtors = new ArrayList<>();
        if (equallyEverybody.isSelected()) {
            debtors = this.participants;
        } else {
            for (CheckBox checkBox : additionalCheckboxes) {
                if (checkBox.isSelected()) {
                    debtors.add(checkBox.getText());
                }
            }
        }
        ObservableList<Tag> selectedTagsList = selectedTags.getItems();
        Set<Tag> selectedTagsSet = Set.copyOf(selectedTagsList);

        Expense expense = new Expense(owner,
                expenseDate,
                expenseAmount,
                currencyChoiceBox.getValue(),
                expenseDescription,
                debtors
        );

        if (equallyEverybody.isSelected()) {
            expense.setSplitEqually(true);
        } else {
            expense.setSplitEqually(false);
        }
        for (Tag tag : selectedTagsSet) {
            expense.addTag(tag);
        }

        expense = server.addExpense(event.getInviteCode(), expense);
        event.addTransaction(expense);
        clearFields();
        mainCtrl.showEventOverview(event);

    }

    /**
     * Updates the expense.
     *
     * @param expense expense to be updated.
     */
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    public void update(Expense expense) {
        if (!validateInputs()) {
            return;
        }

        String owner = whoPaid.getValue();
        String expenseDescription = description.getText();
        float expenseAmount = Float.parseFloat(amount.getText());
        LocalDate expenseDate = datePicker.getValue();
        List<String> debtors = new ArrayList<>();
        Currency currency = currencyChoiceBox.getValue();
        if (equallyEverybody.isSelected()) {
            debtors = this.participants;
        } else {
            for (CheckBox checkBox : additionalCheckboxes) {
                if (checkBox.isSelected()) {
                    debtors.add(checkBox.getText());
                }
            }
        }
        ObservableList<Tag> selectedTagsList = selectedTags.getItems();
        Set<Tag> selectedTagsSet;
        selectedTagsSet = Set.copyOf(selectedTagsList);

        // Check if the expense is split equally
        boolean splitEqually = equallyEverybody.isSelected();

        // Update the expense object with the new values
        expense.setOwner(owner);
        expense.setDescription(expenseDescription);
        expense.setAmount(expenseAmount);
        expense.setDate(expenseDate);
        expense.setSplitEqually(splitEqually);
        expense.setCurrency(currency);
        Map<String, Float> debts = new HashMap<>();
        for (String debtor : debtors) {
            debts.put(debtor, 0f);
        }
        expense.setDebts(debts);
        if (splitEqually) {
            expense.splitEqually(expenseAmount);
        } else {
            Map<String, Integer> usersMultiplierMap = new HashMap<>();
            debtors.forEach(p -> {
                usersMultiplierMap.put(p, 1);
            });
            expense.splitAmong(expenseAmount, usersMultiplierMap);
        }
        // Clear existing tags and add the updated tags
        expense.setTags(selectedTagsSet.stream().toList());
        expense = server.updateExpense(event.getInviteCode(), expense);
        clearFields();
        mainCtrl.showEventOverview(event);
    }

    private boolean validateInputs() {
        // OWNER CHECKING
        if (isNullOrEmpty(whoPaid.getValue())) {
            AddExpenseCtrl.displayErrorPopup("Name cannot be empty", errorPopup);
            return false;
        }
        // DESCRIPTION CHECKING
        if (isNullOrEmpty(description.getText())) {
            AddExpenseCtrl.displayErrorPopup("Description cannot be empty", errorPopup);
            return false;
        }
        if (!isValidAmount(amount.getText())) {
            AddExpenseCtrl.displayErrorPopup("Amount cannot be more than 6 digits", errorPopup);
            return false;
        }
        // DATE CHECKING
        if (isNullOrEmpty(String.valueOf(datePicker.getValue()))) {
            AddExpenseCtrl.displayErrorPopup("Date cannot be empty", errorPopup);
            return false;
        }
        //CURRENCY CHECK
        if (currencyChoiceBox.getValue() == null) {
            AddExpenseCtrl.displayErrorPopup("Currency cannot be empty", errorPopup);
            return false;
        }
        // SPLIT CHECKING
        if (!equallyEverybody.isSelected() && !onlySomePeople.isSelected()) {
            AddExpenseCtrl.displayErrorPopup("You have to choose a split option", errorPopup);
            return false;
        }

        return true;
    }

    static void displayErrorPopup(String message, Pane errorPopup) {
        if (errorPopup.getOpacity() != 0) {
            return; // avoids spamming the error popup
        }
        errorPopup.toFront();
        Text error = (Text) errorPopup.getChildren().getFirst();
        error.setText(message);

        fadeInOutPopup(errorPopup);
    }

    private boolean isValidAmount(String amountText) {
        if (amountText == null || amountText.isEmpty()) {
            return false;
        }
        amountText = amountText.trim();
        // Check if the amount is a valid number and has at most 6 digits
        return amountText.matches("\\d{1,6}\\.?\\d{0,2}");
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public void setExpenseToUpdate(Expense expenseToUpdate) {
        this.expenseToUpdate = expenseToUpdate;
    }

    public void onExit() {
        socket.unregisterFromMessages("/topic/eventsUpdated");
    }
}
