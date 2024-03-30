package client.scenes;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import client.utils.ServerUtils;
import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * This is used to test the AdminLoginCtrl.
 * It extends ApplicationTest to use TestFX.
 */
public class AdminLoginCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private PasswordField passwordField;

    @InjectMocks
    private AdminLoginCtrl controller;

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
     * This is the setup for testing.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new AdminLoginCtrl(serverUtils, mainCtrl);

        passwordField = new PasswordField();
        controller.setPasswordField(passwordField);
    }

    @Test
    public void testSuccessfulLogin() {
        when(serverUtils.authenticate("correctPassword")).thenReturn(true);

        Platform.runLater(() -> passwordField.setText("correctPassword"));
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> controller.loginButtonClicked());
        WaitForAsyncUtils.waitForFxEvents();

        verify(serverUtils, times(1)).authenticate("correctPassword");
        verify(mainCtrl, times(1)).showAdminOverview();
    }

    @Test
    public void testUnsuccessfulLogin() {
        when(serverUtils.authenticate("wrongPassword")).thenReturn(false);

        Platform.runLater(() -> passwordField.setText("wrongPassword"));
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> controller.loginButtonClicked());
        WaitForAsyncUtils.waitForFxEvents();

        verify(serverUtils, times(1)).authenticate("wrongPassword");
    }

    @Test
    public void testCancel() {
        controller.cancel();

        verify(mainCtrl, times(1)).showHomePage();
    }
}