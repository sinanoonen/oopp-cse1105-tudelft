package client.scenes;

import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import com.google.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Controller for ServerSelect scene.
 */
public class ServerSelectCtrl implements Initializable, LanguageInterface {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final UIUtils uiUtils;
    private final ClientUtils clientUtils;

    @FXML
    private AnchorPane root;
    @FXML
    private Text title;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Pane errorPopup;
    @FXML
    private Button cancelButton;
    @FXML
    private Button connectButton;

    @Inject
    public ServerSelectCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, UIUtils uiUtils, ClientUtils clientUtils) {
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

        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                onConnectClicked();
                return;
            }
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                onCancelClicked();
                return;
            }
        });
    }

    @Override
    public void updateLanguage() {
        var lm = uiUtils.getLanguageMap();
        title.setText(lm.get("homepage_server_select").toUpperCase());
        cancelButton.setText(lm.get("general_cancel"));
        connectButton.setText(lm.get("serverselect_connect"));
    }

    /**
     * Refresh method for ServerSelect scene.
     */
    public void refresh() {
        ipField.setText(ServerUtils.getIp());
        portField.setText(ServerUtils.getPort());

        if (clientUtils.isHighContrast()) {
            uiUtils.activateHighContrastMode(root);
        } else {
            uiUtils.deactivateHighContrastMode(root);
        }

        updateLanguage();
    }

    /**
     * Handles changing the server in serverUtils and validating information.
     */
    @FXML
    public void onConnectClicked() {
        if (!validateFields()) {
            return;
        }
        String prevIp = ServerUtils.getIp();
        String prevPort = ServerUtils.getPort();
        ServerUtils.setServer(ipField.getText(), portField.getText());
        WebSocketServerUtils.setSession(ipField.getText(), portField.getText());
        try {
            mainCtrl.showHomePage();
        } catch (Exception e) {
            mainCtrl.showServerSelect();
            HomePageCtrl.displayErrorPopup(
                    uiUtils.getLanguageMap().get("serverselect_error_failed_connection"),
                    errorPopup
            );
            ServerUtils.setServer(prevIp, prevPort);
            WebSocketServerUtils.setSession(prevIp, prevPort);
        }
    }

    @FXML
    public void onCancelClicked() {
        mainCtrl.showHomePage();
    }

    private boolean validateFields() {
        var lm = uiUtils.getLanguageMap();
        boolean emptyFields = ipField.getText().isEmpty() || portField.getText().isEmpty();
        if (emptyFields) {
            HomePageCtrl.displayErrorPopup(lm.get("serverselect_error_empty_fields"), errorPopup);
            return false;
        }
        boolean validIP = validIP(ipField.getText());
        if (!validIP) {
            HomePageCtrl.displayErrorPopup(lm.get("serverselect_error_invalid_ip"), errorPopup);
            return false;
        }
        boolean validPort = validPort(portField.getText());
        if (!validPort) {
            HomePageCtrl.displayErrorPopup(
                    lm.get("serverselect_error_invalid_port") + " (0 <= port <= 65535).",
                    errorPopup
            );
            return false;
        }
        return true;
    }

    private boolean validIP(String ip) {
        String regex = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        Pattern pattern = Pattern.compile(regex);
        return ip.equals("localhost") || pattern.matcher(ip).matches();
    }

    private boolean validPort(String port) {
        int p = Integer.parseInt(port);
        return p > 0 && p < 65536;
    }
}
