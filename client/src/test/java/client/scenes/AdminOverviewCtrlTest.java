package client.scenes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import client.utils.ServerUtils;
import commons.Event;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
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
     * This is the setUp for testing.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        controller = new AdminOverviewCtrl(serverUtils, mainCtrl);

        Platform.runLater(() -> {
            controller.setEventContainer(new ListView<>());
            controller.getEventContainer().setItems(FXCollections.observableArrayList());
            controller.setSortByTitleAscending(true);
            controller.setSortByCreationDateAscending(true);
            controller.setSortByLastActivityAscending(true);
            controller.setIfSortByCreationDate(false);
            controller.setIfSortByLastActivity(false);
            controller.setIfSortByTitle(false);
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

}