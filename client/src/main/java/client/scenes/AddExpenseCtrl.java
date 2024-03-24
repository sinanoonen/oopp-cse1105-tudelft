package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.User;
import commons.transactions.Expense;
import commons.transactions.Tag;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
    private boolean initialized = false;

    private Event event;
    private List<String> participants = new ArrayList<>();
    private final List<String> currencies = List.of("EUR", "USD");
    private Set<Tag> tags;

    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    /**
     * Refreshes the scene.
     *
     * @param event event the expense belongs to
     */
    public void refresh(Event event) {
        this.event = event;
        this.tags = event.getTags();
        initialized = true;
        initialize();
    }


    /**
     * Initializes fxml elements.
     */
    @FXML
    public void initialize() {
        if (!initialized) {
            // Delay initialization until after refresh() is called
            return;
        }
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
     *  Sets up the list view that shows the selected tags.
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
        mainCtrl.showEventOverview(event);
    }

    @FXML
    private void handleAddButtonClick() {
        try {
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
            for (Tag tag : selectedTagsSet) {
                expense.addTag(tag);
            }

            event.addTransaction(expense);
            server.addExpense(event.getInviteCode(), expense);

        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();

        }
        clearFields();
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

}
