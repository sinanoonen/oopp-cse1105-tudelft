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

import client.utils.ClientUtils;
import client.utils.Currency;
import client.utils.Language;
import commons.Event;
import commons.User;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Set;

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

    private SettingsCtrl settingsCtrl;
    private Scene settings;

    private Scene adminOverview;
    private AdminOverviewCtrl adminOverviewCtrl;
    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;

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
                           Pair<AddEventCtrl, Parent> addEvent,
                            Pair<SettingsCtrl, Parent> settings,
                            Pair<AdminOverviewCtrl, Parent> adminOverview,
                           Pair<AddExpenseCtrl, Parent> addExpense
    ) {
        this.primaryStage = primaryStage;

        this.homePageCtrl = homePage.getKey();
        this.homePage = new Scene(homePage.getValue());

        this.eventOverviewCtrl = eventOverview.getKey();
        this.eventOverview = new Scene(eventOverview.getValue());

        this.addEventCtrl = addEvent.getKey();
        this.addEvent = new Scene(addEvent.getValue());

        this.settingsCtrl = settings.getKey();
        this.settings = new Scene(settings.getValue());

        this.adminOverviewCtrl = adminOverview.getKey();
        this.adminOverview = new Scene(adminOverview.getValue());

        //Set default language and currency
        ClientUtils.setCurrency(Currency.EUR);
        ClientUtils.setLanguage(Language.ENGLISH);

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpense = new Scene(addExpense.getValue());
        showHomePage();
        primaryStage.show();
    }

    /**
     * Displays the home page.
     */
    public void showHomePage() {
        primaryStage.setTitle("Home");
        primaryStage.setScene(homePage);
        homePageCtrl.refresh();
    }

    /**
     * Displays the page to add a new event.
     */
    public void showAddEvent() {
        primaryStage.setTitle("New Event");
        primaryStage.setScene(addEvent);
        addEventCtrl.refresh();
    }

    /**
     * Show the overview page for an event.
     */
    public void showEventOverview(Event event) {
        primaryStage.setTitle(event.getTitle());
        primaryStage.setScene(eventOverview);
        eventOverviewCtrl.refresh(event);
    }


    /**
     * Show the settings page.
     */
    public void showSettings() {
        primaryStage.setTitle("Settings");
        primaryStage.setScene(settings);
        settingsCtrl.refresh();
    }

    /**
     * Shows the admin page.
     */
    private void showAdminOverview() {
        primaryStage.setTitle("Admin Overview");
        primaryStage.setScene(adminOverview);
        adminOverviewCtrl.initialize();
    }

    private void showAddExpense(Event event) {
        primaryStage.setTitle("New Expense");
        primaryStage.setScene(addExpense);
        addExpenseCtrl.refresh(event);
    }
}