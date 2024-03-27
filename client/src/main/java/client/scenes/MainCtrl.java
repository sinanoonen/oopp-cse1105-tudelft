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

import algorithms.DebtSettler;
import client.utils.ClientUtils;
import client.utils.Currency;
import client.utils.Language;
import client.utils.ManageUserMode;
import commons.Event;
import commons.User;
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

    private DebtOverviewCtrl debtOverviewCtrl;
    private Scene debtOverview;

    private DebtPaymentOverviewCtrl debtPaymentOverviewCtrl;
    private Scene debtPaymentOverview;

    private AddEventCtrl addEventCtrl;
    private Scene addEvent;

    private SettingsCtrl settingsCtrl;
    private Scene settings;

    private Scene adminOverview;
    private AdminOverviewCtrl adminOverviewCtrl;
    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;

    private Scene manageUser;
    private ManageUserCtrl manageUserCtrl;

    private Scene adminLogin;
    private AdminLoginCtrl adminLoginCtrl;

    /**
     * Initialize the main controller.
     *
     * @param primaryStage  Stage
     * @param homePage      home page on which the app opens
     * @param eventOverview the eventOverview scene
     * @param addEvent      the addEvent page
     * @param manageUser    the manageUser scene
     */
    public void initialize(Stage primaryStage,
                           Pair<HomePageCtrl, Parent> homePage,
                           Pair<EventOverviewCtrl, Parent> eventOverview,
                           Pair<DebtOverviewCtrl, Parent> debtOverview,
                           Pair<DebtPaymentOverviewCtrl, Parent> debtPaymentOverview,
                           Pair<AddEventCtrl, Parent> addEvent,
                           Pair<SettingsCtrl, Parent> settings,
                           Pair<AdminOverviewCtrl, Parent> adminOverview,
                           Pair<AddExpenseCtrl, Parent> addExpense,
                           Pair<ManageUserCtrl, Parent> manageUser,
                           Pair<AdminLoginCtrl, Parent> adminLogin
    ) {
        this.primaryStage = primaryStage;

        this.homePageCtrl = homePage.getKey();
        this.homePage = new Scene(homePage.getValue());

        this.eventOverviewCtrl = eventOverview.getKey();
        this.eventOverview = new Scene(eventOverview.getValue());

        this.debtOverviewCtrl = debtOverview.getKey();
        this.debtOverview = new Scene(debtOverview.getValue());

        this.debtPaymentOverviewCtrl = debtPaymentOverview.getKey();
        this.debtPaymentOverview = new Scene(debtPaymentOverview.getValue());

        this.addEventCtrl = addEvent.getKey();
        this.addEvent = new Scene(addEvent.getValue());

        this.settingsCtrl = settings.getKey();
        this.settings = new Scene(settings.getValue());

        this.adminOverviewCtrl = adminOverview.getKey();
        this.adminOverview = new Scene(adminOverview.getValue());

        this.manageUserCtrl = manageUser.getKey();
        this.manageUser = new Scene(manageUser.getValue());

        this.adminLoginCtrl = adminLogin.getKey();
        this.adminLogin = new Scene(adminLogin.getValue());

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
     * Shows the debt overview.
     *
     * @param  event the event that triggered the debt overview display
     */
    public void showDebtOverview(Event event) {
        primaryStage.setTitle("Debt Overview");
        primaryStage.setScene(debtOverview);
        debtOverviewCtrl.refresh(event);
    }

    /**
     * Shows the debt payment overview.
     *
     * @param event the event from the debt overview, required to go back
     * @param debtSettler the debtSettler from debt overview, contains the payment instructions
     */
    public void showDebtPaymentOverview(Event event, DebtSettler debtSettler) {
        primaryStage.setTitle("Debt Payment Overview");
        primaryStage.setScene(debtPaymentOverview);
        debtPaymentOverviewCtrl.refresh(event, debtSettler);
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
    public void showAdminOverview() {
        primaryStage.setTitle("Admin Overview");
        primaryStage.setScene(adminOverview);
        adminOverviewCtrl.refresh();
    }

    /**
     * Redirects client to a page to create a new expense.
     *
     * @param event event to which the expense is to be added
     */
    public void showAddExpense(Event event) {
        primaryStage.setTitle("New Expense");
        primaryStage.setScene(addExpense);
        addExpenseCtrl.refresh(event);
    }

    /**
     * Shows the page to create a new user.
     *
     * @param event event for which the user is to be created
     */
    public void showCreateUser(Event event) {
        primaryStage.setTitle("New User");
        primaryStage.setScene(manageUser);
        manageUserCtrl.refresh(ManageUserMode.CREATE, null, event);
    }

    /**
     * Shows the page to edit a user.
     *
     * @param user user to be edited
     * @param event event for which the user is to be edited
     */
    public void showEditUser(User user, Event event) {
        primaryStage.setTitle("Edit " + user.getEmail());
        primaryStage.setScene(manageUser);
        manageUserCtrl.refresh(ManageUserMode.EDIT, user, event);
    }

    /**
     * Shows the admin login.
     */
    public void showAdminLogin() {
        primaryStage.setTitle("Admin Login");
        primaryStage.setScene(adminLogin);
    }
}