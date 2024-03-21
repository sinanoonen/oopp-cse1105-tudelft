package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * A controller for the AddEvent scene.
 */
public class AddEventCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField inputField;
    @FXML
    private Pane errorPopup;

    @Inject
    public AddEventCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Refresh method for this scene.
     */
    public void refresh() {
        inputField.setText("");
        inputField.setEditable(false);
        errorPopup.setOpacity(0);
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

    /**
     * Handles checking if any specific keys are pressed while editing the textField.
     *
     * @param keyEvent keyEvent
     */
    public void inputFieldTypeHandler(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER) || inputField.getText().isEmpty()) {
            return;
        }
        inputField.setEditable(false);
    }

    public void cancel(ActionEvent actionEvent) {
        mainCtrl.showHomePage();
    }

    /**
     * Handles the create button logic.
     */
    public void saveEvent() {
        if (inputField.getText().isEmpty()) {
            displayInputError("Title cannot be empty!");
            return;
        }
        if (inputField.getText().length() > 20) {
            displayInputError("Title exceeds max length (20)");
            return;
        }

        Event newEvent = new Event(inputField.getText());
        Event saved = serverUtils.addNewEvent(newEvent);
        mainCtrl.showEventOverview(saved);
    }

    private void displayInputError(String message) {
        errorPopup.toFront();
        Text error = (Text) errorPopup.getChildren().getFirst();
        error.setText(message);

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
}
