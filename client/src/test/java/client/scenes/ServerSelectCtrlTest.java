package client.scenes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * This is used to test the ServerSelectCtrlTest.
 */
public class ServerSelectCtrlTest extends ApplicationTest {

    private ServerUtils serverUtils;
    private MainCtrl mainCtrl;
    private ServerSelectCtrl serverSelectCtrl;
    @Mock
    private UIUtils uiUtils;
    @Mock
    private ClientUtils clientUtils;

    /**
     * This is the setup for headless tests.
     *
     * @throws Exception the exception that is thrown
     */
    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * This is the setUp for testing.
     */
    @BeforeEach
    public void setUp() {
        serverUtils = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);

        serverSelectCtrl = new ServerSelectCtrl(serverUtils,
                mainCtrl, uiUtils, clientUtils, null);

        serverSelectCtrl.setIpField(new TextField());
        serverSelectCtrl.setPortField(new TextField());
        serverSelectCtrl.setConnectButton(new Button());
        serverSelectCtrl.setCancelButton(new Button());
        serverSelectCtrl.setErrorPopup(new Pane());
    }

    @Test
    public void testConnectButtonFailure() {
        serverSelectCtrl.getIpField().setText("invalid_ip");
        serverSelectCtrl.getPortField().setText("8080");

        serverSelectCtrl.getConnectButton().fire();

        assertTrue(serverSelectCtrl.getErrorPopup().isVisible());
    }


    @Test
    public void testOnEscapeKeyPress() {
        serverSelectCtrl.onCancelClicked();

        verify(mainCtrl).showHomePage();
    }


    @Test
    public void testValidIP() {
        ServerSelectCtrl serverSelectCtrl = new ServerSelectCtrl(null, null, null,
                null, null);
        assertTrue(serverSelectCtrl.validIP("localhost"));
        assertTrue(serverSelectCtrl.validIP("192.168.0.1"));
        assertFalse(serverSelectCtrl.validIP(""));
        assertFalse(serverSelectCtrl.validIP("256.256.256.256"));
        assertFalse(serverSelectCtrl.validIP("abc.def.ghi.jkl"));
    }

    @Test
    public void testValidPort() {
        ServerSelectCtrl serverSelectCtrl = new ServerSelectCtrl(null,
                null, null, null, null);
        assertTrue(serverSelectCtrl.validPort("80"));
        assertTrue(serverSelectCtrl.validPort("65535"));
        assertFalse(serverSelectCtrl.validPort("-1"));
        assertFalse(serverSelectCtrl.validPort("65536"));
        assertFalse(serverSelectCtrl.validPort("abc"));
    }

    @Test
    public void testValidateFields() {
        serverSelectCtrl.getIpField().setText("");
        serverSelectCtrl.getPortField().setText("");

        UIUtils uiUtils1 = new UIUtils();
        serverSelectCtrl.setUiUtils(uiUtils1);
        assertFalse(serverSelectCtrl.validateFields());

        serverSelectCtrl.getIpField().setText("localhost");
        serverSelectCtrl.getPortField().setText("8080");

        assertTrue(serverSelectCtrl.validateFields());

        serverSelectCtrl.getIpField().setText("invalid_ip");
        assertFalse(serverSelectCtrl.validateFields());

        serverSelectCtrl.getIpField().setText("invalid_ip");
        serverSelectCtrl.getPortField().setText("abc");
        assertFalse(serverSelectCtrl.validateFields());
    }
}
