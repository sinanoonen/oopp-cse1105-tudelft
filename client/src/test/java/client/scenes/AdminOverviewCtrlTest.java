package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import client.utils.ServerUtils;
import commons.Event;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * This class is used to test the Admin Overview
 * It uses Mockito and TestFX.
 */
public class AdminOverviewCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;

    @InjectMocks
    private AdminOverviewCtrl controller;

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

        controller = new AdminOverviewCtrl(serverUtils, mainCtrl, null);



        Platform.runLater(() -> {
            controller.setEventContainer(new ListView<>());
            controller.getEventContainer().setItems(FXCollections.observableArrayList());
            controller.setSortByTitleAscending(true);
            controller.setSortByCreationDateAscending(true);
            controller.setSortByLastActivityAscending(true);
            controller.setIfSortByCreationDate(false);
            controller.setIfSortByLastActivity(false);
            controller.setIfSortByTitle(false);
            controller.setTitle(new TextField());
            controller.setRoot(new AnchorPane());
            controller.setExitButton(new Button());
            controller.setSortByTitleButton(new Button());
            controller.setSortByCreationDateButton(new Button());
            controller.setSortByLastActivityButton(new Button());
            controller.setExportEventButton(new Button());
            controller.setImportEventButton(new Button());
            controller.setDeleteEventButton(new Button());
        });

        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testRefresh() {
        controller.refresh();
        assertFalse(controller.isIfSortByTitle());
        assertFalse(controller.isIfSortByCreationDate());
        assertFalse(controller.isIfSortByLastActivity());
        assertTrue(controller.isSortByTitleAscending());
        assertTrue(controller.isSortByCreationDateAscending());
        assertTrue(controller.isSortByLastActivityAscending());
    }

    @Test
    public void testHandleSortByTitle() {
        Event event1 = mock(Event.class);
        when(event1.getTitle()).thenReturn("B Event");
        Event event2 = mock(Event.class);
        when(event2.getTitle()).thenReturn("A Event");

        controller.getEventContainer().setItems(FXCollections.observableArrayList(event1, event2));

        controller.handleSortByTitle();

        List<Event> sortedEvents = new ArrayList<>(controller.getEventContainer().getItems());

        assertTrue(sortedEvents.indexOf(event2) < sortedEvents.indexOf(event1));
        assertTrue(controller.isIfSortByTitle());
        assertFalse(controller.isIfSortByCreationDate());
        assertFalse(controller.isIfSortByLastActivity());
        assertFalse(controller.isSortByTitleAscending());
        assertTrue(controller.isSortByCreationDateAscending());
        assertTrue(controller.isSortByLastActivityAscending());
    }

    @Test
    public void testHandleSortByCreationDate() {
        Event event1 = mock(Event.class);
        when(event1.getCreationDate()).thenReturn(LocalDateTime.of(2024, 3, 29, 2,  1, 1));
        Event event2 = mock(Event.class);
        when(event2.getCreationDate()).thenReturn(LocalDateTime.of(2024, 3, 28, 2, 2, 2));

        controller.getEventContainer().setItems(FXCollections.observableArrayList(event1, event2));

        controller.handleSortByCreationDate();

        List<Event> sortedEvents = new ArrayList<>(controller.getEventContainer().getItems());

        assertTrue(sortedEvents.indexOf(event2) < sortedEvents.indexOf(event1));
        assertFalse(controller.isIfSortByTitle());
        assertTrue(controller.isIfSortByCreationDate());
        assertFalse(controller.isIfSortByLastActivity());
        assertFalse(controller.isSortByCreationDateAscending());
        assertTrue(controller.isSortByTitleAscending());
        assertTrue(controller.isSortByLastActivityAscending());
    }

    @Test
    public void testHandleSortByLastActivity() {
        Event event1 = mock(Event.class);
        when(event1.getLastActivity()).thenReturn(LocalDateTime.of(2024, 3, 29, 22, 0));
        Event event2 = mock(Event.class);
        when(event2.getLastActivity()).thenReturn(LocalDateTime.of(2024, 3, 28, 22, 0));

        controller.getEventContainer().setItems(FXCollections.observableArrayList(event1, event2));

        controller.handleSortByLastActivity();

        List<Event> sortedEvents = new ArrayList<>(controller.getEventContainer().getItems());

        assertTrue(sortedEvents.indexOf(event2) < sortedEvents.indexOf(event1));
        assertFalse(controller.isIfSortByTitle());
        assertFalse(controller.isIfSortByCreationDate());
        assertTrue(controller.isIfSortByLastActivity());
        assertFalse(controller.isSortByLastActivityAscending());
        assertTrue(controller.isSortByTitleAscending());
        assertTrue(controller.isSortByCreationDateAscending());
    }

    @Test
    public void testFormatEventDetails() {
        Event event = new Event("Test");
        String uuid = event.getInviteCode().toString();
        String date1 = event.getCreationDate().toString();
        String date2 = event.getLastActivity().toString();


        String expected = "Title: Test\n"
            + "Invite Code: " + uuid + "\n"
            + "Participants: []\n"
            + "Tags: [commons.transactions.Tag{name='Food', color=-7093123}, "
            + "commons.transactions.Tag{name='Entrance Fees', color=-11893016}, "
            + "commons.transactions.Tag{name='Travel', color=-2070938}]\n"
            + "Expenses: []\n"
            + "Payments: []\n"
            + "Creation Date: " + date1 + "\n"
            + "Last Activity: " + date2 + "\n";

        assertEquals(expected, controller.formatEventDetails(event));
    }

    @Test
    public void testCancel() {
        controller.exit();

        verify(mainCtrl, times(1)).showHomePage();
    }
}