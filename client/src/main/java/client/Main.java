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

package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.AddEventCtrl;
import client.scenes.AdminOverviewCtrl;
import client.scenes.ManageUserCtrl;
import client.scenes.EventOverviewCtrl;
import client.scenes.HomePageCtrl;
import client.scenes.MainCtrl;
import client.scenes.SettingsCtrl;
import com.google.inject.Injector;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class for the client.
 */
public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        var homePage = FXML.load(HomePageCtrl.class, "client", "scenes", "HomePage.fxml");
        var eventOverview = FXML.load(EventOverviewCtrl.class, "client", "scenes", "EventOverview.fxml");
        var addEvent = FXML.load(AddEventCtrl.class, "client", "scenes", "AddEvent.fxml");
        var settings = FXML.load(SettingsCtrl.class, "client", "scenes", "Settings.fxml");
        var adminOverview = FXML.load(AdminOverviewCtrl.class, "client", "scenes", "AdminOverview.fxml");
        var manageUser = FXML.load(ManageUserCtrl.class, "client", "scenes", "ManageUser.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage,
                homePage,
                eventOverview,
                addEvent,
                settings,
                adminOverview,
                manageUser);
    }
}