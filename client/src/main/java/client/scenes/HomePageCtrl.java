package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.User;
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
    @FXML
    private Circle optionsButton;
    @FXML
    Pane settingsOverlay;
    @FXML
    Pane settingClickArea;
    @FXML
    Pane quitClickArea;


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
        settingsOverlay.setVisible(false);

        screenDarkener.setPrefWidth(root.getWidth());
        screenDarkener.setPrefHeight(root.getHeight());
        screenDarkener.setLayoutX(root.getLayoutX());
        screenDarkener.setLayoutY(root.getLayoutY());

        codeInput.setText("");

        reloadEventsList();
    }



    public void showSettings() {
        mainCtrl.showSettings();
    }


    /**
     * Shows the add event overlay.
     */
    public void showEventOverlay() {
        addEventOverlay.toFront();
        addEventOverlay.setVisible(true);
        screenDarkener.setVisible(true);
        addEventOverlay.setMouseTransparent(false);
        screenDarkener.setMouseTransparent(false);
    }

    /**
     * Shows the settings overlay.
     */
    public void showSettingsOverlay() {
        settingsOverlay.toFront();
        settingsOverlay.setVisible(true);
        screenDarkener.setVisible(true);
        settingsOverlay.setMouseTransparent(false);
        screenDarkener.setMouseTransparent(false);
    }

    /**
     * Hides all popups.
     */
    public void hidePopUps() {
        addEventOverlay.setVisible(false);
        settingsOverlay.setVisible(false);
        screenDarkener.setVisible(false);
        addEventOverlay.setMouseTransparent(true);
        settingsOverlay.setMouseTransparent(true);
        screenDarkener.setMouseTransparent(true);
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
        displayErrorPopup(message, errorPopup);
    }

    static void displayErrorPopup(String message, Pane errorPopup) {
        if (errorPopup.getOpacity() != 0) {
            return; // avoids spamming the error popup
        }
        errorPopup.toFront();
        Text error = (Text) errorPopup.getChildren().getFirst();
        error.setText(message);

        fadeInOutPopup(errorPopup);
    }

    static void fadeInOutPopup(Pane errorPopup) {
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
