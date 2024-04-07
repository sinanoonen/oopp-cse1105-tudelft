package client.scenes;

import client.utils.ClientUtils;
import client.utils.ManageUserMode;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.User;
import commons.WebSocketMessage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * A controller for the create-user page, as well as edit-user.
 */
public class ManageUserCtrl implements Initializable {
    private ServerUtils serverUtils;
    private MainCtrl mainCtrl;
    private final WebSocketServerUtils socket;


    ManageUserMode mode;
    Event event;

    @FXML
    private AnchorPane root;
    @FXML
    private Text title;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField ibanField;
    @FXML
    private TextField bicField;
    @FXML
    private Pane errorPopup;
    @FXML
    private Button confirmButton;

    /**
     * Constructor for the ManageUser controller.
     *
     * @param serverUtils serverUtils
     * @param mainCtrl    mainCtrl
     * @param socket      socket
     */
    @Inject
    public ManageUserCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, WebSocketServerUtils socket) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.socket = socket;
        mode = ManageUserMode.CREATE;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }

        root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                cancel();
                return;
            }
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                confirm();
                return;
            }
        });
    }

    /**
     * A refresh method for this scene, sets scene back to initial setting.
     */
    public void refresh(ManageUserMode mode, User user, Event event) {
        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }
        this.mode = mode;
        this.event = event;
        if (mode == ManageUserMode.CREATE) {
            title.setText("CREATE NEW USER");
            nameField.setText("");
            emailField.setText("");
            ibanField.setText("");
            bicField.setText("");

            emailField.setEditable(true);
            changeStyleAttribute(emailField, "-fx-background-color", "white");
            changeStyleAttribute(emailField, "-fx-text-fill", "black");

            confirmButton.setText("CREATE");
        } else {
            title.setText("EDIT USER");
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            ibanField.setText(user.getIban());
            bicField.setText(user.getBic());

            emailField.setEditable(false);
            changeStyleAttribute(emailField, "-fx-background-color", "#2b2b2b");
            changeStyleAttribute(emailField, "-fx-text-fill", "#8e8e8e");

            confirmButton.setText("SAVE");
        }

        socket.registerForMessages("/topic/eventsUpdated", WebSocketMessage.class, message -> {
            Platform.runLater(() -> {
                UUID uuid = UUID.fromString(message.getContent().substring(15));
                if (event != null && uuid.equals(event.getInviteCode())) {
                    UIUtils.showEventDeletedWarning(event.getTitle());
                    mainCtrl.showHomePage();
                }
            });
        });
    }

    public void cancel() {
        onExit();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Method to decide what happens when clicking the confirm button.
     */
    public void confirm() {
        if (mode == ManageUserMode.CREATE) {
            create();
        } else {
            save();
        }
    }

    /**
     * Validates the inputs and if all are valid creates a new user.
     */
    public void create() {
        if (!validateInputs()) {
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        String iban = ibanField.getText();
        String bic = bicField.getText();
        User user = new User(name, email, iban, bic, event.getInviteCode());
        onExit();
        User saved = serverUtils.createUser(user);
        mainCtrl.showEventOverview(event);
    }

    /**
     * Checks inputs and saves changes to a user.
     */
    public void save() {
        if (!validateInputs()) {
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        String iban = ibanField.getText();
        String bic = bicField.getText();
        User updated = new User(name, email, iban, bic, event.getInviteCode());

        User saved = serverUtils.updateUser(updated);
        onExit();
        mainCtrl.showEventOverview(serverUtils.getEventByUUID(event.getInviteCode()));
    }

    private boolean validateInputs() {
        // NAME CHECKING
        if (isNullOrEmpty(nameField.getText())) {
            HomePageCtrl.displayErrorPopup("Name cannot be empty", errorPopup);
            return false;
        }
        // EMAIL CHECKING
        if (isNullOrEmpty(emailField.getText())) {
            HomePageCtrl.displayErrorPopup("Email cannot be empty", errorPopup);
            return false;
        }
        if (!validateEmail(emailField.getText())) {
            HomePageCtrl.displayErrorPopup("Invalid email format", errorPopup);
            return false;
        }
        if (mode == ManageUserMode.CREATE && event.getParticipants()
                .stream()
                .anyMatch(user -> user.getEmail().equals(emailField.getText()))
        ) {
            HomePageCtrl.displayErrorPopup("Email already registered", errorPopup);
            return false;
        }
        // IBAN CHECKING
        if (isNullOrEmpty(ibanField.getText())) {
            HomePageCtrl.displayErrorPopup("Iban cannot be empty", errorPopup);
            return false;
        }
        // BIC CHECKING
        if (isNullOrEmpty(bicField.getText())) {
            HomePageCtrl.displayErrorPopup("Bic cannot be empty", errorPopup);
            return false;
        }

        return true;
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private static boolean validateEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
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
     * Unsubscribe from sockets and any other clean-up code.
     */
    public void onExit() {
        socket.unregisterFromMessages("/topic/eventsUpdated");
    }
}
