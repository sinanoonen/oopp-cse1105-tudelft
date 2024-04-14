package client.scenes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import client.utils.WebSocketServerUtils;
import commons.Event;
import java.util.UUID;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * This is used to test the EventOverviewCtrl.
 */
public class EventOverviewCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private WebSocketServerUtils socket;
    @Mock
    private UIUtils uiUtils;
    @Mock
    private ClientUtils clientUtils;

    @InjectMocks
    private EventOverviewCtrl controller;

    @Mock
    private Stage mockStage;

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
        MockitoAnnotations.initMocks(this);
        controller = new EventOverviewCtrl(serverUtils, mainCtrl, socket, uiUtils, clientUtils);
        when(mainCtrl.getPrimaryStage()).thenReturn(mockStage);
        controller.setParticipantsMenu(new Pane());
        controller.setTitle(new TextField());
        controller.setInviteCodeButton(new Button());
        controller.setTitleBox(new Pane());
        controller.setButtonDarkener(new Pane());
        controller.setClipboardPopup(new StackPane());
        Text initialText = new Text("Initial text");
        controller.getClipboardPopup().getChildren().add(initialText);
        controller.setRoot(new AnchorPane());
        controller.setParticipantsDarkener(new Pane());
        controller.setAddParticipantsDarkener(new Pane());
        controller.setExpenseDarkener(new Pane());
        controller.setExpenseDetailsDarkener(new Pane());
        controller.setAddParticipantsMenu(new Pane());
        controller.setExpenseMenu(new Pane());
        controller.setExpenseDetails(new Pane());
        controller.setTransactionContainer(new ListView<>());
        controller.setTagFilterChoiceBox(new ChoiceBox<>());
        controller.setNewParticipantsList(new ListView<>());
        controller.setParticipantsList(new ListView<>());
        controller.setLanguageDropdown(new ComboBox<>());
        controller.setBackLink(new Hyperlink());
        controller.setParticipantsButton(new Button());
        controller.setDebtsButton(new Button());
        controller.setFilterTextField(new TextField());
        controller.setAddParticipantButton(new Button());
        controller.setCloseButton(new Button());
        controller.setConfirmButton(new Button());
        controller.setNewParticipantButton(new Button());
        controller.setEditExpense(new Button());
        controller.setRemoveExpense(new Button());
        controller.setErrorPopup(new Pane());
        controller.setExpenseIncludes(new Text());
        controller.setExpenseDetailsButton(new Button());
        controller.setExpenseDetailsText(new Text());
        controller.setTitleDescription(new Text());
        controller.setTitleDate(new Text());
        controller.setTitleOwner(new Text());
        controller.setTitleAmount(new Text());
        controller.setTitleParticipants(new Text());
        controller.setTitleTags(new Text());
    }

    @Test
    public void testInitialization() {
        Platform.runLater(() -> {
            controller.initialize(null, null);

            assertNotNull(controller.getParticipantsMenu());
            assertNotNull(controller.getTitle());
            assertNotNull(controller.getInviteCodeButton());
            assertNotNull(controller.getParticipantsList());
            assertEquals("", controller.getTitle().getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testLoadEventDetails() {
        Event mockEvent = new Event("Sample Event");
        when(serverUtils.getEventByUUID(any(UUID.class))).thenReturn(mockEvent);
        controller.refresh(mockEvent);
        verify(socket, times(1)).registerForMessages(any(), any(), any());
        assertEquals("Sample Event", controller.getTitle().getText());
    }

    @Test
    public void testCleanupOnExit() {
        controller.onExit();

        verify(socket, times(1)).unregisterFromMessages("/topic/event");
    }

    @Test
    public void testRefreshUpdatesUIProperly() {
        Event mockEvent = new Event("Sample Event");
        mockEvent.setInviteCode(UUID.randomUUID());
        when(serverUtils.getEventByUUID(any(UUID.class))).thenReturn(mockEvent);

        controller.refresh(mockEvent);

        assertEquals("Sample Event", controller.getTitle().getText());

    }

    @Test
    public void testErrorHandlingDuringDataLoad() {
        when(serverUtils.getEventByUUID(any(UUID.class))).thenThrow(new RuntimeException("Failed to load data"));

        assertDoesNotThrow(() -> controller.refresh(new Event("Failed Event")));

        assertNotNull(controller.getErrorPopup().isVisible());
    }

    @Test
    public void testAddExpenseButtonAction() {
        Circle addExpenseButton = mock(Circle.class);
        controller.setAddExpense(addExpenseButton);

        controller.onNewExpenseClicked();

        verify(mainCtrl, times(1)).showAddExpense(controller.getEvent());
    }

}