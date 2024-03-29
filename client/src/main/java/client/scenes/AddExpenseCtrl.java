package client.scenes;

import client.utils.ManageExpenseMode;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import commons.Event;
import commons.User;
import commons.WebSocketMessage;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Tag;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javax.inject.Inject;

/**
 * Controller for adding an expense to an event.
 */
public class AddExpenseCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final WebSocketServerUtils socket;


    @FXML
    private Label title;
    @FXML
    private ChoiceBox<String> whoPaid;
    @FXML
    private TextField description;
    @FXML
    private TextField amount;
    @FXML
    private ChoiceBox<String> currencyChoiceBox;
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

    private Event event;
    private List<String> participants = new ArrayList<>();
    private final List<String> currencies = List.of("EUR", "USD");
    private Set<Tag> tags;
    private ManageExpenseMode mode;
    private Expense expenseToUpdate = null;
    private boolean initialized = false;

    /**
     * The constructor for the controller.
     *
     * @param server the server
     * @param mainCtrl the main controller
     * @param socket the web socket
     */
    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl, WebSocketServerUtils socket) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.socket = socket;
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
            selectedTags.getItems().addAll(expense.getTags());
            setupListViewCellFactory();
        }
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
    }

    /**
     * Shows the participants when clicked only some people button.
     */
    public void handleOnlySomePeople() {
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
     * Sets up the list view that shows the selected tags.
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
                        } else {
                            setText(tag.getName()); // Display only the name of the tag
                            int color = tag.getColor();
                            String newColor = intToString(color);
                            setStyle("-fx-background-color: " + newColor);
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
        clearFields();
        onExit();
        mainCtrl.showEventOverview(event);
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
<<<<<<< client/src/main/java/client/scenes/AddExpenseCtrl.java
     * Creates new expense.
     */
    public void create() {
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

        Expense expense = new Expense(owner, expenseDate, expenseAmount, expenseDescription, debtors);
        if (equallyEverybody.isSelected()) {
            expense.setSplitEqually(true);
        } else {
            expense.setSplitEqually(false);
        }
        for (Tag tag : selectedTagsSet) {
            expense.addTag(tag);
        }

        event.addTransaction(expense);
        server.addExpense(event.getInviteCode(), expense);

    }

    /**
     * Updates the expense.
     *
     * @param expense expense to be updated.
     */
    public void update(Expense expense) {
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

        // Check if the expense is split equally
        boolean splitEqually = equallyEverybody.isSelected();

        // Update the expense object with the new values
        expense.setOwner(owner);
        expense.setDescription(expenseDescription);
        expense.setAmount(expenseAmount);
        expense.setDate(expenseDate);
        expense.setSplitEqually(splitEqually);
        // Clear existing tags and add the updated tags
        expense.setTags(selectedTagsSet.stream().toList());
        server.updateExpense(event.getInviteCode(), expense);

    }

    public void setExpenseToUpdate(Expense expenseToUpdate) {
        this.expenseToUpdate = expenseToUpdate;
    }

     * Unsubscribe from sockets and any other clean-up code.
     */
    public void onExit() {
        socket.unregisterFromMessages("/topic/eventsUpdated");
    }
}
