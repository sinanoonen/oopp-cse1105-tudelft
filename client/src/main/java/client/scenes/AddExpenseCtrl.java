package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.User;
import commons.transactions.Tag;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
    private CheckBox onlySomePeople;
    @FXML
    private VBox additionalCheckboxesContainer;
    private List<CheckBox> additionalCheckboxes = new ArrayList<>();
    @FXML
    private ChoiceBox<Tag> expenseTags;
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

    @FXML
    private void sinan(ActionEvent event) {
        if (onlySomePeople.isSelected()) {
            // Clear existing checkboxes
            additionalCheckboxesContainer.getChildren().clear();

            // Add checkboxes for selecting users from the event
            for (String participant : participants) {
                CheckBox checkBox = new CheckBox(participant);
                additionalCheckboxes.add(checkBox);
                additionalCheckboxesContainer.getChildren().add(checkBox);
            }
        } else {
            // Remove additional checkboxes
            additionalCheckboxesContainer.getChildren().clear();
            additionalCheckboxes.clear();
        }
    }




    @FXML
    private void handleCancelButtonClick(ActionEvent event) {
        // Handle cancel button click
    }

    @FXML
    private void handleAddButtonClick(ActionEvent event) {
        // Handle add button click
    }

}
