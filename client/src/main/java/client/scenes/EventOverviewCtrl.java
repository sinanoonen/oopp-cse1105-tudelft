package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Controller for the EventOverview scene.
 */
public class EventOverviewCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField title;
    @FXML
    private Pane titleBox;
    @FXML
    private Button inviteCodeButton;
    @FXML
    private Pane buttonDarkener;
    @FXML
    private StackPane clipboardPopup;

    @Inject
    public EventOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Method to refresh the scene. This is needed for some reason.
     */
    public void refresh() {
        title.setEditable(false);
        titleBox.setVisible(false);
        buttonDarkener.setVisible(false);
        clipboardPopup.setOpacity(0);
    }

    /**
     * Function for allowing user to edit the title on double-click.
     *
     * @param event MouseEvent from user
     */
    @FXML
    public void onTitleFieldClicked(MouseEvent event) {
        if (!(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)) {
            return;
        }
        title.setEditable(true);
        changeBackgroundColor(title, "#2b2b2b");
        titleBox.setVisible(true);
    }

    /**
     * Handles deselecting the title text box.
     *
     * @param event the key that was just pressed
     */
    @FXML
    public void onTitleFieldDeselect(KeyEvent event) {
        if (!event.getCode().equals(KeyCode.ENTER)
                && !event.getCode().equals(KeyCode.ESCAPE)
                || title.getText().isEmpty()) {
            return;
        }

        titleBox.setVisible(false);
        changeBackgroundColor(title, "#333333");
        title.setEditable(false);
    }

    /**
     * Onclick event to copy the event's invite code to the user's clipboard.
     *
     * @param event MouseEvent
     */
    @FXML
    public void copyInviteCode(MouseEvent event) {
        String inviteCode = "i am an invite code";
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(inviteCode);
        clipboard.setContent(content);
    }

    /**
     * Darkens the invite code button on click, and un-darkens on release.
     * Also handles the "copied to clipboard" popup animation.
     *
     * @param event MouseEvent
     */
    @FXML
    public void toggleDarkenedButton(MouseEvent event) {
        buttonDarkener.setVisible(!buttonDarkener.isVisible());
        if (!buttonDarkener.isVisible()) {
            clipboardPopup.toFront();
            FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(0.5), clipboardPopup);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);

            FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(0.5), clipboardPopup);
            fadeOutTransition.setFromValue(1.0);
            fadeOutTransition.setToValue(0.0);
            fadeOutTransition.setDelay(Duration.seconds(1));

            fadeInTransition.setOnFinished(finished -> fadeOutTransition.play());

            fadeInTransition.play();
        }
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
}
