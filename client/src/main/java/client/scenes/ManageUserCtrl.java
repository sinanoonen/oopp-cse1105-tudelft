package client.scenes;

import client.utils.ManageUserMode;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.User;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * A controller for the create-user page, as well as edit-user.
 */
public class ManageUserCtrl {
    private ServerUtils serverUtils;
    private MainCtrl mainCtrl;

    ManageUserMode mode;

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
    public void refresh(ManageUserMode mode, User user) {
        this.mode = mode;
        if (mode == ManageUserMode.CREATE) {
            title.setText("CREATE NEW USER");
            nameField.setText("");
            emailField.setText("");
            ibanField.setText("");
            bicField.setText("");

            emailField.setEditable(true);
            changeBackgroundColor(emailField, "white");
            changeTextFill(emailField, "black");

            confirmButton.setText("CREATE");
        } else {
            title.setText("EDIT USER");
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            ibanField.setText(user.getIban());
            bicField.setText(user.getBic());

            emailField.setEditable(false);
            changeBackgroundColor(emailField, "#2b2b2b");
            changeTextFill(emailField, "#8e8e8e");

            confirmButton.setText("SAVE");
        }
    }

    public void cancel() {
        mainCtrl.showHomePage();
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
        User user = new User(name, email, iban, bic);

        User saved = serverUtils.createUser(user);
        mainCtrl.showHomePage();
    }

    /**
     * Checks inputs and saves changes to a user.
     */
    public void confirm() {
        if (!validateInputs()) {
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        String iban = ibanField.getText();
        String bic = bicField.getText();
        User updated = new User(name, email, iban, bic);

        User saved = serverUtils.updateUser(updated);
        mainCtrl.showHomePage();
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
        if (mode == ManageUserMode.CREATE && serverUtils.getUsers()
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
     * Changes the background color of an FXML node.
     * Will add color if not already present.
     *
     * @param node node whose color to change
     * @param color color string
     */
    private void changeBackgroundColor(Node node, String color) {
        String currentStyle = node.getStyle();
        String newColor = "-fx-background-color: " + color + ";";

        if (currentStyle.contains("-fx-background-color")) {
            // remove existing background color
            int startIndex = currentStyle.indexOf("-fx-background-color");
            int endIndex = currentStyle.indexOf(";", startIndex);
            currentStyle = currentStyle.substring(0, startIndex) + currentStyle.substring(endIndex + 1);
        }

        node.setStyle(currentStyle + newColor);
    }

    /**
     * Changes the text fill color of an FXML node.
     * Will add color if not already present.
     *
     * @param node node whose text fill to change
     * @param color color string
     */
    private void changeTextFill(Node node, String color) {
        String currentStyle = node.getStyle();
        String newColor = "-fx-text-fill: " + color + ";";

        if (currentStyle.contains("-fx-text-fill")) {
            // remove existing background color
            int startIndex = currentStyle.indexOf("-fx-text-fill");
            int endIndex = currentStyle.indexOf(";", startIndex);
            currentStyle = currentStyle.substring(0, startIndex) + currentStyle.substring(endIndex + 1);
        }

        node.setStyle(currentStyle + newColor);
    }
}
