package client.scenes;

import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import commons.Event;
import commons.WebSocketMessage;
import commons.transactions.Tag;
import commons.transactions.Transaction;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

/**
 * The controller for the admin overview.
 */
public class AdminOverviewCtrl implements Initializable, LanguageInterface {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final WebSocketServerUtils socket;

    @FXML
    private AnchorPane root;
    @FXML
    private TextField title;
    @FXML
    private Button sortByTitleButton;
    @FXML
    private Button sortByCreationDateButton;
    @FXML
    private Button sortByLastActivityButton;
    @FXML
    private Button exitButton;
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

    private boolean sortByTitleAscending;
    private boolean sortByCreationDateAscending;
    private boolean sortByLastActivityAscending;

    private Event selectedEvent;

    /**
     * The constructor for the controller.
     *
     * @param server   the server utils
     * @param mainCtrl the main controller
     * @param socket the web socket utils
     */
    @Inject
    public AdminOverviewCtrl(ServerUtils server, MainCtrl mainCtrl, WebSocketServerUtils socket) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.socket = socket;
    }

    /**
     * Initializes the scene.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }

        socket.registerForMessages("/topic/event", WebSocketMessage.class, message -> {
            Platform.runLater(this::loadEvents);
        });

        server.longPollEvents(e -> {
            Platform.runLater(this::loadEvents);
        });

        UIUtils.addTooltip(exitButton, "ESC: Exit");

        root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                exit();
                return;
            }
        });
    }

    @Override
    public void updateLanguage() {
        var lm = UIUtils.getLanguageMap();
        String sortBy = lm.get("adminpage_sort_by") + " ";
        title.setText(lm.get("adminpage"));
        sortByTitleButton.setText(sortBy + lm.get("adminpage_title"));
        sortByCreationDateButton.setText(sortBy + lm.get("adminpage_creation_date"));
        sortByLastActivityButton.setText(sortBy + lm.get("adminpage_last_activity"));
        exportEventButton.setText(lm.get("adminpage_export_event") + " (JSON)");
        importEventButton.setText(lm.get("adminpage_import_event") + " (JSON)");
        deleteEventButton.setText(lm.get("adminpage_delete_event"));
        exitButton.setText(lm.get("adminpage_exit"));
    }

    /**
     * Refresh method for the admin overview.
     */
    public void refresh() {
        ifSortByCreationDate = false;
        ifSortByLastActivity = false;
        ifSortByTitle = false;

        sortByTitleAscending = true;
        sortByCreationDateAscending = true;
        sortByLastActivityAscending = true;

        loadEvents();
        setupEventListView();
        setupEventSelection();

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }

        updateLanguage();
    }

    /**
     * This loads the events from the server.
     */
    public void loadEvents() {
        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() throws Exception {
                List<Event> events = server.getEvents();

                if (ifSortByTitle) {
                    Comparator<Event> comparator = Comparator.comparing(Event::getTitle);
                    if (!sortByTitleAscending) {
                        events.sort(comparator);
                    } else {
                        events.sort(comparator.reversed());
                    }
                } else if (ifSortByCreationDate) {
                    Comparator<Event> comparator = Comparator.comparing(Event::getCreationDate);
                    if (!sortByCreationDateAscending) {
                        events.sort(comparator);
                    } else {
                        events.sort(comparator.reversed());
                    }
                } else if (ifSortByLastActivity) {
                    Comparator<Event> comparator = Comparator.comparing(Event::getLastActivity);
                    if (!sortByLastActivityAscending) {
                        events.sort(comparator);
                    } else {
                        events.sort(comparator.reversed());
                    }
                }

                return events;
            }
        };

        task.setOnSucceeded(event -> eventContainer.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(event -> {
            Throwable cause = task.getException();
            cause.printStackTrace();
            showAlert("Error", UIUtils.getLanguageMap()
                    .get("adminpage_error_failed_load_event")
                    + ": " + cause.getMessage());
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
        if (ClientUtils.isHighContrast()) {
            dialogPane.setStyle("-fx-background-color: #000; -fx-text-fill: #FFFFFF;");
            TextArea textArea = new TextArea(formatEventDetails(event));
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setStyle("-fx-control-inner-background: #000; "
                + "-fx-text-fill: #FFFFFF; "
                + "-fx-font-size: 12pt; "
                + "-fx-background-color: #000; "
                + "-fx-border-color: #c30052; ");

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #000;");

            dialogPane.setContent(scrollPane);

            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialogPane.getButtonTypes().add(closeButton);

            Button closeButtonControl = (Button) dialogPane.lookupButton(closeButton);
            closeButtonControl.setStyle("-fx-background-color: #000; "
                + "-fx-text-fill: #FFFFFF; -fx-border-color: #c30052; "
                + "-fx-border-radius: 5; -fx-border-width: 2;");
        } else {
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
        }

        dialog.showAndWait();
    }

    /**
     * This is used to return the
     * event details in a string format.
     *
     * @param event the event
     * @return the string representation of the event
     */
    // to be improved
    public String formatEventDetails(Event event) {
        return "Title: " + event.getTitle() + "\n"
            + "Invite Code: " + event.getInviteCode() + "\n"
            + "Participants: " + event.getParticipants() + "\n"
            + "Tags: " + event.getTags() + "\n"
            + "Expenses: " + event.getExpenses() + "\n"
            + "Payments: " + event.getPayments() + "\n"
            + "Creation Date: " + event.getCreationDate() + "\n"
            + "Last Activity: " + event.getLastActivity() + "\n";
    }

    /**
     * This sorts the events by title.
     */
    @FXML
    public void handleSortByTitle() {
        sort(Comparator.comparing(Event::getTitle), sortByTitleAscending);

        ifSortByTitle = true;
        ifSortByCreationDate = false;
        ifSortByLastActivity = false;

        sortByTitleAscending = !sortByTitleAscending;
        sortByCreationDateAscending = true;
        sortByLastActivityAscending = true;
    }

    /**
     * This sorts the events by creation date.
     */
    @FXML
    public void handleSortByCreationDate() {
        sort(Comparator.comparing(Event::getCreationDate), sortByCreationDateAscending);

        ifSortByTitle = false;
        ifSortByCreationDate = true;
        ifSortByLastActivity = false;

        sortByCreationDateAscending = !sortByCreationDateAscending;
        sortByTitleAscending = true;
        sortByLastActivityAscending = true;
    }

    /**
     * This sorts the events by last activity.
     */
    @FXML
    public void handleSortByLastActivity() {
        sort(Comparator.comparing(Event::getLastActivity), sortByLastActivityAscending);

        ifSortByTitle = false;
        ifSortByCreationDate = false;
        ifSortByLastActivity = true;

        sortByLastActivityAscending = !sortByLastActivityAscending;
        sortByTitleAscending = true;
        sortByCreationDateAscending = true;
    }

    private void sort(Comparator<Event> comparing,  boolean sortAscending) {
        ObservableList<Event> events = eventContainer.getItems();

        if (!sortAscending) {
            events.sort(comparing.reversed());
        } else {
            events.sort(comparing);
        }
        eventContainer.setItems(events);
    }

    @FXML
    private void handleExportEvent() {
        if (selectedEvent == null) {
            showAlert("Error", UIUtils.getLanguageMap().get("adminpage_error_none_selected_export"));
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(selectedEvent.getTitle() + ".json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(eventContainer.getScene().getWindow());

        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                mapper.writeValue(file, selectedEvent);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", UIUtils.getLanguageMap()
                        .get("adminpage_error_failed_export_event")
                        + ": " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleImportEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(eventContainer.getScene().getWindow());

        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                Event importedEvent  = mapper.readValue(file, Event.class);

                ObservableList<Event> events = eventContainer.getItems();
                for (Event event : events) {
                    if (event.getInviteCode().toString().equals(importedEvent.getInviteCode().toString())) {
                        showAlert("Warning", "An event with this invite code already exists in the list.");
                        return;
                    }
                }

                Set<Tag> tags = importedEvent.getTags();
                Set<Tag> newTags = new HashSet<>();
                List<Transaction> transactions = importedEvent.transactions();
                for (Tag tag : tags) {
                    Tag tag1 = server.addNewTag(tag);
                    for (Transaction transaction : transactions) {
                        List<Tag> tags1 = transaction.getTags();
                        if (tags1 != null && tags1.contains(tag)) {
                            tags1.set(tags1.indexOf(tag), tag1);
                        }
                        transaction.setTags(tags1);
                    }

                    newTags.add(tag1);
                }
                importedEvent.setTransactions(transactions);
                importedEvent.setAvailableTags(newTags);

                server.addNewEvent(importedEvent);

                loadEvents();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", UIUtils.getLanguageMap()
                        .get("adminpage_error_failed_import_event")
                        + ": " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeleteEvent() {
        var lm = UIUtils.getLanguageMap();
        if (selectedEvent != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete Event");
            confirmationDialog.setContentText("Are you sure you want to delete the event: "
                + selectedEvent.getTitle() + "?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                UUID uuid = selectedEvent.getInviteCode();
                socket.sendWebSocketMessage("/app/event", uuid.toString());
                selectedEvent = null;
            }
        } else {
            showAlert("Error", lm.get("adminpage_error_none_selected_delete"));
        }
    }

    public void exit() {
        mainCtrl.showHomePage();
    }


    public ListView<Event> getEventContainer() {
        return eventContainer;
    }

    public void setEventContainer(ListView<Event> eventContainer) {
        this.eventContainer = eventContainer;
    }

    public boolean isIfSortByTitle() {
        return ifSortByTitle;
    }

    public void setIfSortByTitle(boolean ifSortByTitle) {
        this.ifSortByTitle = ifSortByTitle;
    }

    public boolean isIfSortByCreationDate() {
        return ifSortByCreationDate;
    }

    public void setIfSortByCreationDate(boolean ifSortByCreationDate) {
        this.ifSortByCreationDate = ifSortByCreationDate;
    }

    public boolean isIfSortByLastActivity() {
        return ifSortByLastActivity;
    }

    public void setIfSortByLastActivity(boolean ifSortByLastActivity) {
        this.ifSortByLastActivity = ifSortByLastActivity;
    }

    public boolean isSortByTitleAscending() {
        return sortByTitleAscending;
    }

    public void setSortByTitleAscending(boolean sortByTitleAscending) {
        this.sortByTitleAscending = sortByTitleAscending;
    }

    public boolean isSortByCreationDateAscending() {
        return sortByCreationDateAscending;
    }

    public void setSortByCreationDateAscending(boolean sortByCreationDateAscending) {
        this.sortByCreationDateAscending = sortByCreationDateAscending;
    }

    public boolean isSortByLastActivityAscending() {
        return sortByLastActivityAscending;
    }

    public void setSortByLastActivityAscending(boolean sortByLastActivityAscending) {
        this.sortByLastActivityAscending = sortByLastActivityAscending;
    }

    public void setTitle(TextField title) {
        this.title = title;
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }

    public void setSortByTitleButton(Button sortByTitleButton) {
        this.sortByTitleButton = sortByTitleButton;
    }

    public void setSortByCreationDateButton(Button sortByCreationDateButton) {
        this.sortByCreationDateButton = sortByCreationDateButton;
    }

    public void setSortByLastActivityButton(Button sortByLastActivityButton) {
        this.sortByLastActivityButton = sortByLastActivityButton;
    }

    public void setExitButton(Button exitButton) {
        this.exitButton = exitButton;
    }

    public void setExportEventButton(Button exportEventButton) {
        this.exportEventButton = exportEventButton;
    }

    public void setImportEventButton(Button importEventButton) {
        this.importEventButton = importEventButton;
    }

    public void setDeleteEventButton(Button deleteEventButton) {
        this.deleteEventButton = deleteEventButton;
    }

}
