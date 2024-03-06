package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class EventOverviewCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField title;
    @FXML
    private Button inviteCodeButton;

    @Inject
    public EventOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    public void refresh() {
        title.setEditable(false);
    }

    /**
     * Function for allowing user to edit the title on double-click.
     *
     * @param event MouseEvent from user
     */
    @FXML
    public void onTitleFieldClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            title.setEditable(true);
        }
    }

    @FXML
    public void copyInviteCode(MouseEvent event) {
        String inviteCode = new String("Temp");
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(inviteCode);
        clipboard.setContent(content);
    }
}
