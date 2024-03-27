package client.scenes;

import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * Controller for ServerSelect scene.
 */
public class ServerSelectCtrl implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane root;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Pane errorPopup;

    @Inject
    public ServerSelectCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }
    }

    public void refresh() {
        ipField.setText(ServerUtils.getIp());
        portField.setText(ServerUtils.getPort());
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
        try {
            mainCtrl.showHomePage();
        } catch (Exception e) {
            mainCtrl.showServerSelect();
            HomePageCtrl.displayErrorPopup("Could not connect to server.", errorPopup);
            ServerUtils.setServer(prevIp, prevPort);
        }
    }

    @FXML
    public void onCancelClicked() {
        mainCtrl.showHomePage();
    }

    private boolean validateFields() {
        boolean emptyFields = ipField.getText().isEmpty() || portField.getText().isEmpty();
        if (emptyFields) {
            HomePageCtrl.displayErrorPopup("Please fill in all fields.", errorPopup);
            return false;
        }
        boolean validIP = validIP(ipField.getText());
        if (!validIP) {
            HomePageCtrl.displayErrorPopup("Invalid IP.", errorPopup);
            return false;
        }
        boolean validPort = validPort(portField.getText());
        if (!validPort) {
            HomePageCtrl.displayErrorPopup("Invalid port (0 <= port <= 65535).", errorPopup);
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
