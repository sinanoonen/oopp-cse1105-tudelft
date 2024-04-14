package client.scenes;

import client.enums.Language;
import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ConfigReader;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import com.google.inject.Inject;
import commons.EmailConfig;
import commons.EmailRequest;
import commons.Event;
import commons.WebSocketMessage;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * A controller for the home page scene.
 */
public class HomePageCtrl implements Initializable, LanguageInterface {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final WebSocketServerUtils socket;
    private final UIUtils uiUtils;
    private final ClientUtils clientUtils;

    private List<Event> events;
    private Map<Language, Image> flags;

    @FXML
    private AnchorPane root;
    @FXML
    private ListView<Node> eventsList;
    @FXML
    private Circle addButton;
    @FXML
    private Pane addEventOverlay;
    @FXML
    private Button newEventButton;
    @FXML
    private Button joinButton;
    @FXML
    private Pane screenDarkener;
    @FXML
    private TextField codeInput;
    @FXML
    private Pane errorPopup;
    @FXML
    private Circle optionsButton;
    @FXML
    private Pane settingsOverlay;
    @FXML
    private Pane settingClickArea;
    @FXML
    private Pane quitClickArea;
    @FXML
    private Pane adminClickArea;
    @FXML
    private Text inviteCodeText;
    @FXML
    private Text eventsText;
    @FXML
    private Text settingsText;
    @FXML
    private Text adminText;
    @FXML
    private Text serverSelectText;
    @FXML
    private Text mailText;
    @FXML
    private Text closeText;
    @FXML
    private Text serverText;
    @FXML
    private ComboBox<Language> languageDropdown;

    public void setConfigReader(ConfigReader configReader) {
        this.configReader = configReader;
    }

    @Inject
    private ConfigReader configReader;

    /**
     * Constructor for the HomePage controller.
     *
     * @param serverUtils serverUtils
     * @param mainCtrl    mainCtrl
     * @param socket      socket
     * @param uiUtils     uiUtils
     * @param clientUtils  clientUtils
     */
    @Inject
    public HomePageCtrl(ServerUtils serverUtils, MainCtrl mainCtrl,
                        WebSocketServerUtils socket, UIUtils uiUtils, ClientUtils clientUtils) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.socket = socket;
        this.uiUtils = uiUtils;
        this.clientUtils = clientUtils;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
        } else {
            uiUtils.deactivateHighContrastMode(root);
        }

        uiUtils.addTooltip(addButton, "CTRL + N: Add event");
        uiUtils.addTooltip(newEventButton, "CTRL + N: Create new event");
        uiUtils.addTooltip(joinButton, "Enter: Join event");

        socket.registerForMessages("/topic/event", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                events = serverUtils.getEvents();
                reloadEventsList();
            });
        });

        root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                if (settingsOverlay.isVisible() || addEventOverlay.isVisible()) {
                    hidePopUps();
                } else {
                    showSettingsOverlay();
                }
                return;
            }
            if (!addEventOverlay.isVisible()
                    && keyEvent.isControlDown()
                    && keyEvent.getCode().equals(KeyCode.N)) {
                showEventOverlay();
                return;
            }
            if (addEventOverlay.isVisible()) {
                if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.N)) {
                    createEvent();
                }
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    joinEvent();
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
        eventsText.setText(lm.get("homepage_events"));
        settingsText.setText(lm.get("homepage_settings"));
        adminText.setText(lm.get("homepage_admin_login"));
        serverSelectText.setText(lm.get("homepage_server_select"));
        mailText.setText(lm.get("homepage_test_mail"));
        closeText.setText(lm.get("general_close"));
        serverText.setText(lm.get("homepage_current_server") + ": " + ServerUtils.getServer());

        inviteCodeText.setText(lm.get("homepage_code"));
        joinButton.setText(lm.get("homepage_join"));
        newEventButton.setText(lm.get("general_new"));

        uiUtils.addTooltip(addButton, "CTRL + N: " + lm.get("homepage_tooltip_add_event"));
        uiUtils.addTooltip(newEventButton, "CTRL + N: " + lm.get("homepage_tooltip_create_event"));
        uiUtils.addTooltip(joinButton, "ENTER: " + lm.get("homepage_tooltip_join_event"));
    }

    /**
     * Refresh method for this scene.
     */
    public void refresh() {
        events = serverUtils.getEvents();

        addEventOverlay.setVisible(false);
        screenDarkener.setVisible(false);
        addEventOverlay.setMouseTransparent(true);
        screenDarkener.setMouseTransparent(true);
        settingsOverlay.setVisible(false);

        screenDarkener.setPrefWidth(root.getPrefWidth());
        screenDarkener.setPrefHeight(root.getPrefHeight());

        codeInput.setText("");

        reloadEventsList();

        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
        } else {
            uiUtils.deactivateHighContrastMode(root);
        }

        serverUtils.longPollEvents(e -> refresh()); // register for polling; on update, refresh

        languageDropdown.setValue(clientUtils.getLanguage());
        updateLanguage();
    }

    public void showSettings() {
        mainCtrl.showSettings();
    }

    /**
     * Shows the add event overlay.
     */
    public void showEventOverlay() {
        screenDarkener.toFront();
        screenDarkener.setVisible(true);
        screenDarkener.setMouseTransparent(false);
        addEventOverlay.toFront();
        addEventOverlay.setVisible(true);
        addEventOverlay.setMouseTransparent(false);
    }

    /**
     * Shows the settings overlay.
     */
    public void showSettingsOverlay() {
        screenDarkener.toFront();
        screenDarkener.setVisible(true);
        settingsOverlay.toFront();
        settingsOverlay.setVisible(true);
        settingsOverlay.setMouseTransparent(false);
        screenDarkener.setMouseTransparent(false);
    }

    /**
     * Hides all popups.
     */
    public void hidePopUps() {
        addEventOverlay.setVisible(false);
        settingsOverlay.setVisible(false);
        screenDarkener.setVisible(false);
        addEventOverlay.setMouseTransparent(true);
        settingsOverlay.setMouseTransparent(true);
        screenDarkener.setMouseTransparent(true);
    }

    public void onAdminAreaClicked() {
        mainCtrl.showAdminLogin();
    }

    public void onServerAreaClicked() {
        mainCtrl.showServerSelect();
    }

    public void createEvent() {
        mainCtrl.showAddEvent();
    }

    /**
     * Takes the text stored in the InputField, fetches the event, and displays said event.
     */
    public void joinEvent() {
        String input = codeInput.getText();
        UUID uuid;
        try {
            uuid = UUID.fromString(input);
        } catch (Exception e) {
            displayInputError(uiUtils.getLanguageMap().get("homepage_error_invalid_code"));
            return;
        }
        try {
            Event event = serverUtils.getEventByUUID(uuid);
            mainCtrl.showEventOverview(event);
        } catch (Exception e) {
            displayInputError(uiUtils.getLanguageMap().get("homepage_error_event_not_found"));
        }
    }

    Node eventCellFactory(Event event) {
        Pane base = new Pane();

        final double width = eventsList.getPrefWidth() - 20;
        final double height = eventsList.getPrefHeight() / 4;

        base.setPrefWidth(width);
        base.setPrefHeight(height);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-color: black;"
                + " -fx-border-width: 3;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text eventTitle = new Text(event.getTitle());
        final double titleTopPadding = base.getPrefHeight() / 2 + 5;
        final double titleLeftPadding = base.getPrefWidth() / 8;
        eventTitle.setLayoutX(base.getLayoutX() + titleLeftPadding);
        eventTitle.setLayoutY(base.getLayoutY() + titleTopPadding);
        eventTitle.setFill(Paint.valueOf("#FFFFFF"));
        eventTitle.setFont(Font.font("SansSerif"));
        eventTitle.setMouseTransparent(true);

        base.getChildren().addAll(eventTitle);
        base.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                mainCtrl.showEventOverview(event);
            }
        });

        return base;
    }

    private void reloadEventsList() {
        eventsList.getItems().removeAll(eventsList.getItems());
        List<Node> items = events.stream().map(this::eventCellFactory).toList();
        eventsList.getItems().addAll(items);
    }

    private void displayInputError(String message) {
        displayErrorPopup(message, errorPopup);
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

    static void fadeInOutPopup(Pane errorPopup) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), errorPopup);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), errorPopup);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(1));

        fadeIn.setOnFinished(finished -> fadeOut.play());
        fadeIn.play();
    }


    /**
     * Changes the style attribute of an FXML node.
     * Will add attribute if not already present.
     *
     * @param node node whose style attribute to change
     * @param attribute style attribute to change
     * @param value value of the style attribute
     */
    private void changeStyleAttribute(Node node, String attribute, String value) {
        String currentStyle = node.getStyle();
        String newAttribute = attribute + ": " + value + ";";

        if (currentStyle.contains(attribute)) {
            // remove existing attribute
            int startIndex = currentStyle.indexOf(attribute);
            int endIndex = currentStyle.indexOf(";", startIndex);
            currentStyle = currentStyle.substring(0, startIndex) + currentStyle.substring(endIndex + 1);
        }

        node.setStyle(currentStyle + newAttribute);
    }

    /**
     * This method handles the sending of a test mail.
     */
    public void onTestEmailClicked() {
        var lm = uiUtils.getLanguageMap();
        showInfo(lm.get("homepage_info_loading"), lm.get("homepage_info_email_sent"));

        EmailConfig emailConfig = configReader.getEmailConfig();
        if (!emailConfig.isComplete()) {
            showAlert(lm.get("homepage_info_email_config"), lm.get("homepage_info_email_setup"));
            return;
        }

        EmailRequest emailRequest = new EmailRequest(emailConfig, emailConfig.getUsername(),
            lm.get("homepage_info_test_subject"), lm.get("homepage_info_test_message"));

        boolean isSuccess = serverUtils.sendMail(emailRequest);

        if (isSuccess) {
            showInfo("Success", lm.get("homepage_info_email_success"));
        } else {
            showAlert("Error", lm.get("homepage_error_email_failed"));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void stop() {
        serverUtils.stop();
    }

    public TextField getCodeInput() {
        return codeInput;
    }

    public void setCodeInput(TextField codeInput) {
        this.codeInput = codeInput;
    }


    public ListView<Node> getEventsList() {
        return eventsList;
    }

    public void setEventsList(ListView<Node> eventsList) {
        this.eventsList = eventsList;
    }

    public Pane getErrorPopup() {
        return errorPopup;
    }

    public void setErrorPopup(Pane errorPopup) {
        this.errorPopup = errorPopup;
    }

    public Pane getSettingsOverlay() {
        return settingsOverlay;
    }

    public void setSettingsOverlay(Pane settingsOverlay) {
        this.settingsOverlay = settingsOverlay;
    }


    public void setScreenDarkener(Pane screenDarkener) {
        this.screenDarkener = screenDarkener;
    }

    public void setAddEventOverlay(Pane addEventOverlay) {
        this.addEventOverlay = addEventOverlay;
    }
}
