package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import client.utils.ServerUtils;
import commons.Event;
import javafx.application.Platform;
import javafx.scene.control.TextField;
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

    @InjectMocks
    private AddEventCtrl controller;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;



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
        inputField = new TextField();
        controller = new AddEventCtrl(serverUtils, mainCtrl);
        controller.setInputField(inputField);
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
}
