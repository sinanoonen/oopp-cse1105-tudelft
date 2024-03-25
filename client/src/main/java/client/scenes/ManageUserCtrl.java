package client.scenes;

import client.utils.ManageUserMode;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.User;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * A controller for the create-user page, as well as edit-user.
 */
public class ManageUserCtrl {
    private ServerUtils serverUtils;
    private MainCtrl mainCtrl;

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
     * @param mainCtrl mainCtrl
     */
    @Inject
    public ManageUserCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        mode = ManageUserMode.CREATE;
    }

    /**
     * A refresh method for this scene, sets scene back to initial setting.
     */
    public void refresh(ManageUserMode mode, User user, Event event) {
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
    }

    public void cancel() {
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
            HomePageCtrl.displayErrorPopup("Invalid email", errorPopup);
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
}
