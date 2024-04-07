package client.scenes;

import client.interfaces.LanguageInterface;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

/**
 * This is a controller for the admin login page.
 */
public class AdminLoginCtrl implements LanguageInterface {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    /**
     * The constructor for the controller.
     *
     * @param server the server utils
     * @param mainCtrl the main controller
     */
    @Inject
    public AdminLoginCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void updateLanguage() {
        // TODO
    }

    @FXML
    public void initialize() {
        loginButton.setDefaultButton(true);
    }

    /**
     * This is triggered when the login button is clicked.
     */
    @FXML
    public void loginButtonClicked() {
        String password = passwordField.getText();
        boolean result = server.authenticate(password);
        if (result) {
            mainCtrl.showAdminOverview();
        } else {
            showError("Invalid password.");
        }
    }


    // to be improved
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void cancel() {
        mainCtrl.showHomePage();
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

}
