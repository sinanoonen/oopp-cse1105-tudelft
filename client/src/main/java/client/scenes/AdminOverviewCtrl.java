package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;



/**
 * The controller for the admin overview.
 */
public class AdminOverviewCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField title;
    @FXML
    private Button sortByTitleButton;
    @FXML
    private Button sortByCreationDateButton;
    @FXML
    private Button sortByLastActivityButton;
    @FXML
    private ListView<Event> eventContainer;
    @FXML
    private Button exportEventButton;
    @FXML
    private Button importEventButton;
    @FXML
    private Button deleteEventButton;

    private boolean ifSortByTitle;
    private boolean ifSortByCreationDate;
    private boolean ifSortByLastActivity;

    private Event selectedEvent;

    /**
     * The constructor for the controller.
     *
     * @param server the server utils
     * @param mainCtrl the main controller
     */
    @Inject
    public AdminOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the scene.
     */
    public void initialize() {
        ifSortByCreationDate = false;
        ifSortByLastActivity = false;
        ifSortByTitle = false;

        loadEvents();
        setupEventListView();
        setupEventSelection();
    }

    private void loadEvents() {
        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() throws Exception {
                List<Event> events = server.getEvents();

                if (ifSortByTitle) {
                    events.sort(Comparator.comparing(Event::getTitle));
                } else if (ifSortByCreationDate) {
                    events.sort(Comparator.comparing(Event::getCreationDate));
                }

                return events;
            }
        };

        task.setOnSucceeded(event -> eventContainer.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(event -> {
            Throwable cause = task.getException();
            cause.printStackTrace();
            showAlert("Error", "Failed to load events: " + cause.getMessage());
        });
        new Thread(task).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupEventSelection() {
        eventContainer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedEvent = newValue;
        });
    }

    private void setupEventListView() {
        eventContainer.setCellFactory(lv -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty || event == null ? null : event.getTitle());
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getClickCount() == 2 && !empty) {
                        showEventDetailsPopup(event);
                    }
                });
            }
        });
    }

    private void showEventDetailsPopup(Event event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Event Details");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #333333; -fx-text-fill: #FFFFFF;");

        TextArea textArea = new TextArea(formatEventDetails(event));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-control-inner-background: #333333; "
            + "-fx-text-fill: #FFFFFF; "
            + "-fx-font-size: 12pt; "
            + "-fx-background-color: #333333; "
            + "-fx-border-color: #c30052; ");

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #333333;");

        dialogPane.setContent(scrollPane);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().add(closeButton);

        Button closeButtonControl = (Button) dialogPane.lookupButton(closeButton);
        closeButtonControl.setStyle("-fx-background-color: #3f3f3f; "
            + "-fx-text-fill: #FFFFFF; -fx-border-color: #c30052; "
            + "-fx-border-radius: 5; -fx-border-width: 2;");

        dialog.showAndWait();
    }

    // to be improved
    private String formatEventDetails(Event event) {
        return "Title: " + event.getTitle() + "\n"
            + "Invite Code: " + event.getInviteCode() + "\n"
            + "Participants: " + event.getParticipants() + "\n"
            + "Tags: " + event.getTags() + "\n"
            + "Expenses: " + event.getExpenses() + "\n"
            + "Payments: " + event.getPayments() + "\n"
            + "Creation Date:" + event.getCreationDate() + "\n";
    }

    @FXML
    private void handleSortByTitle() {
        ObservableList<Event> events = eventContainer.getItems();
        events.sort(Comparator.comparing(Event::getTitle));
        eventContainer.setItems(events);

        ifSortByTitle = true;
        ifSortByCreationDate = false;
        ifSortByLastActivity = false;
    }

    @FXML
    private void handleSortByCreationDate() {
        ObservableList<Event> events = eventContainer.getItems();
        events.sort(Comparator.comparing(Event::getCreationDate));
        eventContainer.setItems(events);

        ifSortByTitle = false;
        ifSortByCreationDate = true;
        ifSortByLastActivity = false;
    }

    @FXML
    private void handleSortByLastActivity() {
        ifSortByTitle = false;
        ifSortByCreationDate = false;
        ifSortByLastActivity = true;
    }

    @FXML
    private void handleExportEvent() {

    }

    @FXML
    private void handleImportEvent() {
        if (selectedEvent != null) {
            return;
        }
    }

    @FXML
    private void handleDeleteEvent() {
        if (selectedEvent != null) {
            UUID uuid = selectedEvent.getInviteCode();
            server.deleteEvent(uuid);
            selectedEvent = null;
            loadEvents();
        }
    }
}
