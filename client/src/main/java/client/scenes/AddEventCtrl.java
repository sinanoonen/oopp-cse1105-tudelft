package client.scenes;

import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Event;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * A controller for the AddEvent scene.
 */
public class AddEventCtrl implements Initializable, LanguageInterface {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final UIUtils uiUtils;
    private final ClientUtils clientUtils;

    @FXML
    private AnchorPane root;
    @FXML
    private Text title;
    @FXML
    private Text prompt;
    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;
    @FXML
    private TextField inputField;
    @FXML
    private Pane errorPopup;

    @Inject
    public AddEventCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, UIUtils uiUtils, ClientUtils clientUtils) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
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

        root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                saveEvent();
                return;
            }
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                cancel(null);
            }
        });
    }

    @Override
    public void updateLanguage() {
        var languageMap = uiUtils.getLanguageMap();
        title.setText(languageMap.get("addevent"));
        prompt.setText(languageMap.get("addevent_event_name") + ":");
        cancelButton.setText(languageMap.get("general_cancel"));
        createButton.setText(languageMap.get("general_create"));
    }

    /**
     * Refresh method for this scene.
     */
    public void refresh() {
        inputField.setText("");
        inputField.setEditable(false);
        errorPopup.setOpacity(0);

        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
        } else {
            uiUtils.deactivateHighContrastMode(root);
        }

        updateLanguage();
    }

    /**
     * Handles click events from the textField.
     *
     * @param mouseEvent mouseEvent
     */
    public void inputFieldClickHandler(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() < 2) {
            return;
        }
        inputField.setEditable(true);
    }

    public void cancel(ActionEvent actionEvent) {
        mainCtrl.showHomePage();
    }

    /**
     * Handles the create button logic.
     */
    public void saveEvent() {
        if (inputField.getText().isEmpty()) {
            displayInputError(uiUtils.getLanguageMap().get("addevent_error_empty_title"));
            return;
        }
        if (inputField.getText().length() > 20) {
            displayInputError(uiUtils.getLanguageMap().get("addevent_error_max_length") + " (20)");
            return;
        }

        Event newEvent = new Event(inputField.getText());
        Event saved = serverUtils.addNewEvent(newEvent);
        mainCtrl.showEventOverview(saved);
    }

    private void displayInputError(String message) {
        HomePageCtrl.displayErrorPopup(message, errorPopup);
    }

    public void setInputField(TextField inputField) {
        this.inputField = inputField;
    }

    public void setErrorPopup(Pane errorPopup) {
        this.errorPopup = errorPopup;
    }
}
