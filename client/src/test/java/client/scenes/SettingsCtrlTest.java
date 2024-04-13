package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import client.enums.Language;
import client.utils.ClientUtils;
import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Currency;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


/**
 * This is used to test the SettingsCtrl class.
 */
public class SettingsCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;

    @InjectMocks
    private SettingsCtrl settingsCtrl;
    private UIUtils uiUtils;

    private ClientUtils clientUtils;
    private List<Language> languageList = new ArrayList<>();

    /**
     * This is the setup for headless tests.
     *
     * @throws Exception the exception that is thrown
     */
    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * This is the setUp for testing.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        uiUtils = new UIUtils();
        clientUtils = new ClientUtils(uiUtils);
        settingsCtrl = new SettingsCtrl(serverUtils, mainCtrl, uiUtils, clientUtils);
        languageList.addAll(Arrays.asList(Language.values()));
        Platform.runLater(() -> {
            settingsCtrl.setCurrencyChoiceBox(new ChoiceBox<>());
            settingsCtrl.setLanguageChoiceBox(new ChoiceBox<>());
            settingsCtrl.setBackLink(new Hyperlink());
            settingsCtrl.setHighContrastCheckBox(new CheckBox());
            settingsCtrl.setSettingsTitle(new Text());
            settingsCtrl.setCurrencyText(new Text());
            settingsCtrl.setLanguageText(new Text());
            settingsCtrl.setRoot(new AnchorPane());
            settingsCtrl.setGeneralText(new Text());
            settingsCtrl.setHighContrastText(new Text());
            settingsCtrl.setAccessibilityText(new Text());
        });

        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testInitialization() {
        settingsCtrl.initialize(null, null);

        assertNotNull(settingsCtrl.getCurrencyChoiceBox());
        assertNotNull(settingsCtrl.getLanguageChoiceBox());
    }

    @Test
    void testGoBack() {
        settingsCtrl.goBack();

        verify(mainCtrl, times(1)).showHomePage();
    }

    @Test
    void testRefresh() {
        settingsCtrl.getCurrencyChoiceBox().setValue(Currency.EUR);
        settingsCtrl.getLanguageChoiceBox().setValue(Language.ENGLISH);
        settingsCtrl.getHighContrastCheckBox().setSelected(true);

        clientUtils.setCurrency(Currency.USD);
        clientUtils.setLanguage(Language.ENGLISH);
        clientUtils.setHighContrast(false);

        settingsCtrl.refresh();

        assertEquals(Currency.USD, settingsCtrl.getCurrencyChoiceBox().getValue());
        assertEquals(Language.ENGLISH, settingsCtrl.getLanguageChoiceBox().getValue());
        assertFalse(settingsCtrl.getHighContrastCheckBox().isSelected());
    }

}
