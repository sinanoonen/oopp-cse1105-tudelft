package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Event;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * This is used to test the AddEventCtrl.
 * It extends ApplicationTest to use TestFX.
 */
public class AddEventCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private TextField inputField;
    @Mock
    private Pane errorPopUp;
    @InjectMocks
    private AddEventCtrl controller;
    @Captor
    private ArgumentCaptor<Event> eventCaptor;
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
     * This is the setup for testing.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new AddEventCtrl(serverUtils, mainCtrl, uiUtils, clientUtils);

        inputField = new TextField();
        errorPopUp = new Pane();
        controller.setInputField(inputField);
        controller.setErrorPopup(errorPopUp);
    }


    @Test
    public void testSaveEventWithValidInput() {
        Platform.runLater(() -> {
            inputField.setText("Valid Title");
            controller.saveEvent();
        });
        WaitForAsyncUtils.waitForFxEvents();
        verify(serverUtils, times(1)).addNewEvent(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();
        assertEquals("Valid Title", capturedEvent.getTitle());
        assertTrue(capturedEvent.getParticipants().isEmpty(), "Participants list should be empty");
        assertTrue(capturedEvent.getExpenses().isEmpty(), "Transactions list should be empty");
        verify(mainCtrl, times(1)).showEventOverview(any());
    }

    @Test
    public void testSaveEventWithEmptyTitle() {
        Platform.runLater(() -> {
            inputField.setText("");
            controller.saveEvent();
        });
        WaitForAsyncUtils.waitForFxEvents();
        verify(serverUtils, times(0)).addNewEvent(any());
        verify(mainCtrl, times(0)).showEventOverview(any());
    }

    @Test
    public void testSaveEventWithTitleExceedingMaxLength() {
        Platform.runLater(() -> {
            inputField.setText("This is a very long title exceeding the maximum length");
            controller.saveEvent();
        });
        WaitForAsyncUtils.waitForFxEvents();
        verify(serverUtils, times(0)).addNewEvent(any());
        verify(mainCtrl, times(0)).showEventOverview(any());
    }

    @Test
    public void testCancelButton() {
        Platform.runLater(() -> {
            controller.cancel(new ActionEvent());
        });
        WaitForAsyncUtils.waitForFxEvents();
        verify(mainCtrl, times(1)).showHomePage();
    }

    @Test
    public void testInputFieldClickHandler_ClickCountGreaterThanEqualTwo() {
        MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                MouseButton.PRIMARY, 2, false, false, false,
                false, true, false, false, false,
                false, false, null);
        controller.inputFieldClickHandler(mouseEvent);
        assertTrue(inputField.isEditable());
    }

    @Test
    public void testInputFieldTypeHandlerEnterKeyPressed() {
        KeyEvent enterKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER,
            false, false, false, false);
        inputField.setText("Some text");
        controller.inputFieldTypeHandler(enterKeyEvent);
        assertFalse(inputField.isEditable());
    }

    @Test
    public void testInputFieldTypeHandlerEnterKeyPressedEmptyText() {
        KeyEvent enterKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER,
            false, false, false, false);
        inputField.setText("");
        controller.inputFieldTypeHandler(enterKeyEvent);
        assertTrue(inputField.isEditable());
    }

    @Test
    public void testInputFieldTypeHandlerOtherKeyPressed() {
        KeyEvent otherKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE,
            false, false, false, false);
        controller.inputFieldTypeHandler(otherKeyEvent);
        assertTrue(inputField.isEditable());
    }
}
