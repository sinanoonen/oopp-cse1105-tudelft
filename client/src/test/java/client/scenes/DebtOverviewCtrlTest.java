package client.scenes;

import algorithms.DebtSettler;
import client.utils.ServerUtils;
import client.utils.WebSocketServerUtils;
import commons.Currency;
import commons.Event;
import commons.User;
import commons.transactions.Expense;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * This is used to test the DebtOverviewCtrl.
 */
public class DebtOverviewCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private WebSocketServerUtils socket;

    @InjectMocks
    private DebtOverviewCtrl controller;

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
        controller = new DebtOverviewCtrl(serverUtils, mainCtrl, socket);
        when(mainCtrl.getPrimaryStage()).thenReturn(mockStage);
        controller.setRoot(new AnchorPane());
        controller.setDebtSettleButton(new Button());
        controller.setBackLink(new Hyperlink());
        controller.setEvent(new Event("Test"));
        controller.setBalanceText(new Text("BalanceText"));
        controller.setParticipantsDebtContainer(new ListView<>());
        controller.setButtonDarkener(new Pane());
        controller.setDebtSettler(new DebtSettler(controller.getEvent()));
    }

    @Test
    public void testInitialization() {
        Platform.runLater(() -> {
            controller.initialize(null, null);

            assertNotNull(controller.getDebtSettleButton());
            assertNotNull(controller.getBalanceText());
            assertNotNull(controller.getParticipantsDebtContainer());
            assertEquals("BalanceText", controller.getBalanceText().getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testCleanUpOnExit() {
        controller.onExit();

        verify(socket, times(1)).unregisterFromMessages("/topic/eventsUpdated");
    }

    @Test
    public void testDebtSettleButton() {
        Button debtSettleButton = mock(Button.class);
        controller.setDebtSettleButton(debtSettleButton);

        controller.showDebtSettler();

        verify(mainCtrl, times(1)).showDebtPaymentOverview(controller.getEvent(),
                controller.getDebtSettler());
    }
}
