package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.regex.Pattern;

/**
 * A controller for the create-user page.
 */
public class CreateUserCtrl {
    private ServerUtils serverUtils;
    private MainCtrl mainCtrl;

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

    @Inject
    public CreateUserCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * A refresh method for this scene, sets scene back to initial setting.
     */
    public void refresh() {
        nameField.setText("");
        emailField.setText("");
        ibanField.setText("");
        bicField.setText("");
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

    private boolean validateInputs() {

        if (isNullOrEmpty(nameField.getText())) {
            HomePageCtrl.displayErrorPopup("Name cannot be empty", errorPopup);
            return false;
        }
        if (isNullOrEmpty(emailField.getText())) {
            HomePageCtrl.displayErrorPopup("Email cannot be empty", errorPopup);
            return false;
        }
        if (!validateEmail(emailField.getText())) {
            HomePageCtrl.displayErrorPopup("Invalid email", errorPopup);
            return false;
        }
        if (isNullOrEmpty(ibanField.getText())) {
            HomePageCtrl.displayErrorPopup("Iban cannot be empty", errorPopup);
            return false;
        }
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
}
