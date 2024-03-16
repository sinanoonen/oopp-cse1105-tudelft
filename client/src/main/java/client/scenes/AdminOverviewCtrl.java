package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
        loadEvents();
        setupEventListView();
        setupEventSelection();
    }

    private void loadEvents() {
        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() throws Exception {
                return server.getEvents();
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
            }
        });
    }

    @FXML
    private void handleSortByTitle() {

    }

    @FXML
    private void handleSortByCreationDate() {

    }

    @FXML
    private void handleSortByLastActivity() {

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
            return;
        }
    }
}
