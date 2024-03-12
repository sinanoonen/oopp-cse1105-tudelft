/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.scenes;

import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Controller for the main scene.
 */
public class MainCtrl {

    private Stage primaryStage;

    private HomePageCtrl homePageCtrl;
    private Scene homePage;

    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverview;

    private AddEventCtrl addEventCtrl;
    private Scene addEvent;

    /**
     * Initialize the main controller.
     *
     * @param primaryStage Stage
     * @param homePage home page on which the app opens
     * @param eventOverview the eventOverview scene
     * @param addEvent the addEvent page
     */
    public void initialize(Stage primaryStage,
                           Pair<HomePageCtrl, Parent> homePage,
                           Pair<EventOverviewCtrl, Parent> eventOverview,
                           Pair<AddEventCtrl, Parent> addEvent
    ) {
        this.primaryStage = primaryStage;

        this.homePageCtrl = homePage.getKey();
        this.homePage = new Scene(homePage.getValue());

        this.eventOverviewCtrl = eventOverview.getKey();
        this.eventOverview = new Scene(eventOverview.getValue());

        this.addEventCtrl = addEvent.getKey();
        this.addEvent = new Scene(addEvent.getValue());

        showHomePage();
        primaryStage.show();
    }

    public void showHomePage() {
        primaryStage.setTitle("Home");
        primaryStage.setScene(homePage);
        homePageCtrl.refresh();
    }

    public void showAddEvent() {
        primaryStage.setTitle("New Event");
        primaryStage.setScene(addEvent);
        addEventCtrl.refresh();
    }

    /**
     * Show the overview.
     */
    public void showEventOverview(Event event) {
        primaryStage.setTitle(event.getTitle());
        primaryStage.setScene(eventOverview);
        eventOverviewCtrl.refresh(event);
    }

}