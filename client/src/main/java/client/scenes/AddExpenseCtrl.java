package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.User;
import commons.transactions.Expense;
import commons.transactions.Tag;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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

    private Event event;
    private List<String> participants = new ArrayList<>();
    private final List<String> currencies = List.of("EUR", "USD");
    private Set<Tag> tags;

    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    public void refresh(Event event) {
        this.event = event;
        this.tags = event.getTags();
    }


    /**
     * Initializes fxml elements.
     */
    @FXML
    public void initialize() {
        refresh(event);
        participants = event.getParticipants().stream()
                        .map(User::getName)
                                .toList();
        whoPaid.getItems().addAll(participants);
        currencyChoiceBox.getItems().addAll(currencies);
        expenseTags.getItems().addAll(tags);

    }

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

    @FXML
    private void handleAddTagButtonClick() {
        // Retrieve the selected item from the ChoiceBox
        Tag selectedTag = expenseTags.getValue();

        if (selectedTag != null && !selectedTags.getItems().contains(selectedTag)) {
            // Add the selected tag to the ListView
            selectedTags.getItems().add(selectedTag);
        }
    }

    @FXML
    private void handleCancelButtonClick(ActionEvent event) {
        // Handle cancel button click
    }

    @FXML
    private void handleAddButtonClick() {
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

        Expense expense = new Expense(owner, expenseDate, expenseAmount, expenseDescription,debtors);
        for (Tag tag : selectedTagsSet) {
            expense.addTag(tag);
        }

        event.addTransaction(expense);
        Expense saved = server.addExpense(event.getInviteCode(), expense);

    }

}
