package client.scenes;

import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * This is a controller for the admin login page.
 */
public class AdminLoginCtrl implements Initializable, LanguageInterface {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane root;
    @FXML
    private TextField title;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button cancelButton;
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
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                loginButtonClicked();
            }
        });
    }

    @FXML
    public void initialize() {
        loginButton.setDefaultButton(true);
    }

    @Override
    public void updateLanguage() {
        var lm = UIUtils.getLanguageMap();
        title.setText(lm.get("adminlogin"));
        passwordField.setPromptText(lm.get("adminlogin_enter_password"));
        cancelButton.setText(lm.get("general_cancel"));
        loginButton.setText(lm.get("admin_submit"));
    }

    /**
     * This refreshes the admin login page.
     */
    public void refresh() {
        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }
        updateLanguage();
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
