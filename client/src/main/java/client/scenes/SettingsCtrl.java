package client.scenes;

import client.enums.Language;
import client.interfaces.LanguageInterface;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Currency;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javax.inject.Inject;


/**
 * Controller for the settings scene.
 */
public class SettingsCtrl implements Initializable, LanguageInterface {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane root;
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
    @FXML
    private Text currencyText;
    @FXML
    private Text languageText;
    @FXML
    private Button addLanguageButton;
    @FXML
    private Text highContrastText;
    @FXML
    private Text generalText;
    @FXML
    private Text accessibilityText;

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
    public void updateLanguage() {
        var lm = UIUtils.getLanguageMap();
        backLink.setText(lm.get("general_back"));
        settingsTitle.setText(lm.get("homepage_settings").toUpperCase());
        currencyText.setText(lm.get("settings_currency"));
        languageText.setText(lm.get("settings_language"));
        highContrastText.setText(lm.get("settings_high_contrast"));
        generalText.setText(lm.get("settings_general"));
        accessibilityText.setText(lm.get("settings_accessibility"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currencyChoiceBox.getItems().addAll(Arrays.asList(Currency.values()));
        languageChoiceBox.getItems().addAll(Arrays.asList(Language.values()));


        //add listener to the currency choice box
        currencyChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldCurrency, newCurrency) -> {
                    ClientUtils.setCurrency(newCurrency);
                    refresh();
                });

        //add listener to the language choice box
        languageChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldLanguage, newLanguage) -> {
                    ClientUtils.setLanguage(newLanguage);
                    refresh();
                });

        //add listener to the high contrast checkbox
        highContrastCheckBox.selectedProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    ClientUtils.setHighContrast(newValue);
                    refresh();
                });

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }

        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.ESCAPE)) {
                goBack();
                return;
            }
        });

        UIUtils.addTooltip(addLanguageButton, "Download language template");
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

        if (ClientUtils.isHighContrast()) {
            UIUtils.activateHighContrastMode(root);
        } else {
            UIUtils.deactivateHighContrastMode(root);
        }

        updateLanguage();
    }

    /**
     * Downloads the language template json.
     */
    public void downloadLanguageTemplate() {
        final String PATH = "src/main/resources/client/languages/template.json";
        File template = new File(PATH);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("template.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());

        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                mapper.writeValue(file, template);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
