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
import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Person;
import commons.Quote;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

/**
 * Controller for the AddQuote scene.
 */
public class AddQuoteCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final UIUtils uiUtils;
    private final ClientUtils clientUtils;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField quote;

    /**
     * Constructor.
     *
     * @param server      ServerUtils
     * @param mainCtrl    MainCtrl
     * @param uiUtils
     * @param clientUtils
     */
    @Inject
    public AddQuoteCtrl(ServerUtils server, MainCtrl mainCtrl, UIUtils uiUtils, ClientUtils clientUtils) {
        this.mainCtrl = mainCtrl;
        this.server = server;

        this.uiUtils = uiUtils;
        this.clientUtils = clientUtils;
    }

    /**
     * Cancel the operation and go back to the overview.
     */
    public void cancel() {
        clearFields();
    }

    /**
     * Add the quote to the server.
     */
    public void ok() {
        try {
            server.addQuote(getQuote());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
    }

    /**
     * Get the quote from the input fields.
     *
     * @return Quote
     */
    private Quote getQuote() {
        var p = new Person(firstName.getText(), lastName.getText());
        var q = quote.getText();
        return new Quote(p, q);
    }

    /**
     * Clear the input fields.
     */
    private void clearFields() {
        firstName.clear();
        lastName.clear();
        quote.clear();
    }

    /**
     * Handle key events.
     *
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }
}