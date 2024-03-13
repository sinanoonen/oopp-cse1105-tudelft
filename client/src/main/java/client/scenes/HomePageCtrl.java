package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import java.util.List;
import java.util.UUID;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * A controller for the home page scene.
 */
public class HomePageCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    private List<Event> events;

    @FXML
    private AnchorPane root;
    @FXML
    private ListView<Node> eventsList;
    @FXML
    private Circle addButton;
    @FXML
    private Pane addEventOverlay;
    @FXML
    private Pane screenDarkener;
    @FXML
    private TextField codeInput;
    @FXML
    private Pane errorPopup;

    @Inject
    public HomePageCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Refresh method for this scene.
     */
    public void refresh() {
        events = serverUtils.getEvents();

        addEventOverlay.setVisible(false);
        screenDarkener.setVisible(false);
        addEventOverlay.setMouseTransparent(true);
        screenDarkener.setMouseTransparent(true);
        screenDarkener.setPrefWidth(root.getPrefWidth());
        screenDarkener.setPrefHeight(root.getPrefHeight());
        screenDarkener.setLayoutX(root.getLayoutX());
        screenDarkener.setLayoutY(root.getLayoutY());

        codeInput.setText("");

        reloadEventsList();
    }

    /**
     * Handler method to toggle the overlay for adding a new event (hitting the + button).
     */
    public void toggleEventOverlay() {
        screenDarkener.toFront();
        addEventOverlay.toFront();
        addEventOverlay.setVisible(!addEventOverlay.isVisible());
        screenDarkener.setVisible(!screenDarkener.isVisible());
        addEventOverlay.setMouseTransparent(!addEventOverlay.isMouseTransparent());
        screenDarkener.setMouseTransparent(!screenDarkener.isMouseTransparent());

    }

    public void createEvent() {
        mainCtrl.showAddEvent();
    }

    /**
     * Takes the text stored in the InputField, fetches the event, and displays said event.
     */
    public void joinEvent() {
        String input = codeInput.getText();
        UUID uuid;
        try {
            uuid = UUID.fromString(input);
        } catch (Exception e) {
            displayInputError("Invalid invite code");
            return;
        }
        Event event = serverUtils.getEventByUUID(uuid);
        if (event == null) {
            displayInputError("Cannot find event");
            return;
        }
        mainCtrl.showEventOverview(event);
    }

    private Node eventCellFactory(Event event) {
        Pane base = new Pane();

        final double width = eventsList.getPrefWidth() - 20;
        final double height = eventsList.getPrefHeight() / 4;

        base.setPrefWidth(width);
        base.setPrefHeight(height);
        base.setStyle("-fx-background-color: #444444;"
                + " -fx-border-color: black;"
                + " -fx-border-width: 3;"
                + " -fx-background-radius: 5;"
                + " -fx-border-radius: 5;"
        );

        Text eventTitle = new Text(event.getTitle());
        final double titleTopPadding = base.getPrefHeight() / 2 + 5;
        final double titleLeftPadding = base.getPrefWidth() / 8;
        eventTitle.setLayoutX(base.getLayoutX() + titleLeftPadding);
        eventTitle.setLayoutY(base.getLayoutY() + titleTopPadding);
        eventTitle.setFill(Paint.valueOf("white"));
        eventTitle.setFont(Font.font("SansSerif"));
        eventTitle.setMouseTransparent(true);

        base.getChildren().addAll(eventTitle);
        base.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                mainCtrl.showEventOverview(event);
            }
        });

        return base;
    }

    private void reloadEventsList() {
        eventsList.getItems().removeAll(eventsList.getItems());
        List<Node> items = events.stream().map(this::eventCellFactory).toList();
        eventsList.getItems().addAll(items);
    }

    private void displayInputError(String message) {
        if (errorPopup.getOpacity() != 0) {
            return; // avoids spamming the error popup
        }
        errorPopup.toFront();
        Text error = (Text) errorPopup.getChildren().getFirst();
        error.setText(message);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), errorPopup);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), errorPopup);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(1));

        fadeIn.setOnFinished(finished -> fadeOut.play());

        fadeIn.play();
    }
}
