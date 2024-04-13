package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import client.utils.ClientUtils;
import client.utils.ConfigReader;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.EmailRequest;
import commons.Event;
import java.util.UUID;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * This is used to test the HomePageCtrlTest.
 */
public class HomePageCtrlTest extends ApplicationTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @InjectMocks
    private HomePageCtrl controller;
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
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new HomePageCtrl(serverUtils, mainCtrl, null, uiUtils, clientUtils);

        Pane settingsOverlay = new Pane();
        Pane screenDarkener = new Pane();
        TextField codeInput = new TextField();
        Pane errorPopup = new Pane(new Text(""));
        ListView<Node> eventsList = new ListView<>();
        ConfigReader cg = new ConfigReader();
        controller.setConfigReader(cg);

        controller.setSettingsOverlay(settingsOverlay);
        controller.setScreenDarkener(screenDarkener);
        controller.setCodeInput(codeInput);
        Text errorMessage = new Text();
        errorPopup.getChildren().add(errorMessage);
        controller.setErrorPopup(errorPopup);
        controller.setEventsList(eventsList);
        when(serverUtils.getEventByUUID(any(UUID.class))).thenReturn(new Event("Event Name"));
    }

    @Test
    public void testCreateEvent() {
        Platform.runLater(() -> {
            controller.createEvent();
        });
        WaitForAsyncUtils.waitForFxEvents();
        verify(mainCtrl, times(1)).showAddEvent();
    }

    @Test
    public void testShowSettingsOverlay() {
        Platform.runLater(() -> {
            controller.showSettingsOverlay();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(controller.getSettingsOverlay().isVisible());
    }

    @Test
    public void testInvalidEventJoin() {
        String invalidCode = "invalid-code";
        when(serverUtils.getEventByUUID(any(UUID.class))).thenThrow(new IllegalArgumentException("Invalid UUID"));

        Platform.runLater(() -> {
            controller.getCodeInput().setText(invalidCode);
            controller.joinEvent();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(controller.getErrorPopup().isVisible());
    }

    @Test
    public void testNavigateToAdminArea() {
        Platform.runLater(() -> {
            controller.onAdminAreaClicked();
        });
        WaitForAsyncUtils.waitForFxEvents();

        verify(mainCtrl, times(1)).showAdminLogin();
    }

    @Test
    public void testServerSelection() {
        Platform.runLater(() -> {
            controller.onServerAreaClicked();
        });
        WaitForAsyncUtils.waitForFxEvents();

        verify(mainCtrl, times(1)).showServerSelect();
    }

    @Test
    public void testEmailSendingError() {
        when(serverUtils.sendMail(any(EmailRequest.class))).thenReturn(false);

        Platform.runLater(() -> {
            controller.onTestEmailClicked();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(controller.getErrorPopup().isVisible());
    }

    @Test
    public void testUIElementInitialization() {
        assertNotNull(controller.getSettingsOverlay());
        assertNotNull(controller.getEventsList());
        assertNotNull(controller.getCodeInput());
    }

    @Test
    public void testLogout() {
        Platform.runLater(() -> {
            controller.stop();
        });
        WaitForAsyncUtils.waitForFxEvents();

        verify(serverUtils, times(1)).stop();
    }

    @Test
    public void testEventCellFactory() {
        Event testEvent = new Event("Test Event");
        controller.setEventsList(new ListView<>());
        controller.getEventsList().setPrefWidth(200);
        controller.getEventsList().setPrefHeight(400);

        Platform.runLater(() -> {
            Node node = controller.eventCellFactory(testEvent);
            Pane base = (Pane) node;

            assertEquals(180, base.getPrefWidth());
            assertEquals(100, base.getPrefHeight());
            assertTrue(base.getStyle().contains("-fx-background-color: #444444;"));

            Text eventTitle = (Text) base.getChildren().get(0);
            assertEquals("Test Event", eventTitle.getText());
            assertTrue(eventTitle.isMouseTransparent());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testShowEventOverlay() {
        Pane screenDarkener = new Pane();
        Pane addEventOverlay = new Pane();
        controller.setScreenDarkener(screenDarkener);
        controller.setAddEventOverlay(addEventOverlay);

        Platform.runLater(() -> {
            controller.showEventOverlay();

            assertTrue(screenDarkener.isVisible());
            assertFalse(screenDarkener.isMouseTransparent());
            assertTrue(addEventOverlay.isVisible());
            assertFalse(addEventOverlay.isMouseTransparent());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

}