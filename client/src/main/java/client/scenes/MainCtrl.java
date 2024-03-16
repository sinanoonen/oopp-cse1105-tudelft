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

    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverview;

    private AddEventCtrl addEventCtrl;
    private Scene addEvent;

    private Scene adminOverview;
    private AdminOverviewCtrl adminOverviewCtrl;

    /**
     * Initialize the main controller.
     *
     * @param primaryStage Stage
     * @param eventOverview the eventOverview scene
     */
    public void initialize(Stage primaryStage,
                           Pair<EventOverviewCtrl, Parent> eventOverview,
                           Pair<AddEventCtrl, Parent> addEvent,
                           Pair<AdminOverviewCtrl, Parent> adminOverview
    ) {
        this.primaryStage = primaryStage;

        this.eventOverviewCtrl = eventOverview.getKey();
        this.eventOverview = new Scene(eventOverview.getValue());

        this.addEventCtrl = addEvent.getKey();
        this.addEvent = new Scene(addEvent.getValue());

        this.adminOverviewCtrl = adminOverview.getKey();
        this.adminOverview = new Scene(adminOverview.getValue());

        showAddEvent();
        primaryStage.show();
    }

    private void showAddEvent() {
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

    private void showAdminOverview() {
        primaryStage.setTitle("Admin Overview");
        primaryStage.setScene(adminOverview);
        adminOverviewCtrl.initialize();
    }

}