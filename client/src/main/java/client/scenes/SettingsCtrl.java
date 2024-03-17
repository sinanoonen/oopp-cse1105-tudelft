package client.scenes;

import client.utils.ClientUtils;
import client.utils.Currency;
import client.utils.Language;
import client.utils.ServerUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javax.inject.Inject;


/**
 * Controller for the settings scene.
 */
public class SettingsCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private Text settingsTitle;
    @FXML
    private ChoiceBox<Currency> currencyChoiceBox;
    @FXML
    private ChoiceBox<Language> languageChoiceBox;
    @FXML
    private Hyperlink backLink;
    @FXML
    private CheckBox highContrastCheckBox;

    /**
     * Initialize the settings controller.
     *
     * @param serverUtils ServerUtils
     * @param mainCtrl MainCtrl
     */
    @Inject
    public SettingsCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currencyChoiceBox.getItems().addAll(Arrays.asList(Currency.values()));
        languageChoiceBox.getItems().addAll(Arrays.asList(Language.values()));


        //add listener to the currency choice box
        currencyChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldCurrency, newCurrency) -> {
                    System.out.println("Currency changed from " + oldCurrency + " to " + newCurrency);
                    ClientUtils.setCurrency(newCurrency);
                });

        //add listener to the language choice box
        languageChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldLanguage, newLanguage) -> {
                    System.out.println("Language changed from " + oldLanguage + " to " + newLanguage);
                    ClientUtils.setLanguage(newLanguage);
                });

        //add listener to the high contrast checkbox
        highContrastCheckBox.selectedProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    System.out.println("High contrast changed from " + oldValue + " to " + newValue);
                    ClientUtils.setHighContrast(newValue);
                });
    }

    /**
     * Go back to the home page.
     */
    public void goBack() {
        mainCtrl.showHomePage();
    }


    /**
     * Refresh the settings page.
     */
    public void refresh() {
        // reset backlink at default state
        backLink.setVisited(false);
        settingsTitle.requestFocus();

        currencyChoiceBox.setValue(ClientUtils.getCurrency());
        languageChoiceBox.setValue(ClientUtils.getLanguage());

        highContrastCheckBox.setSelected(ClientUtils.isHighContrast());
    }


}
