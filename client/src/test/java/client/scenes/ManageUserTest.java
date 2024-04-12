package client.scenes;

import client.enums.ManageUserMode;
import client.utils.ServerUtils;
import commons.Event;
import commons.User;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Test for manage user.
 */
public class ManageUserTest extends ApplicationTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    ManageUserMode mode;
    @Mock
    Event event;
    @Mock
    private Text title;
    @Mock
    private TextField nameField;
    @Mock
    private TextField emailField;
    @Mock
    private TextField ibanField;
    @Mock
    private TextField bicField;
    @Mock
    private Pane errorPopup;
    @Mock
    private Button confirmButton;
    @InjectMocks
    private ManageUserCtrl controller;
    @Captor
    private ArgumentCaptor<User> userCaptor;

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
     * This is the setup for testing.
     */
    @BeforeEach
    public void setUpCreate() {
        MockitoAnnotations.initMocks(this);
        controller = new ManageUserCtrl(serverUtils, mainCtrl, null);
        event = new Event("Event");

        this.mode = ManageUserMode.CREATE;

        title = new Text();
        nameField = new TextField();
        emailField = new TextField();
        ibanField = new TextField();
        bicField = new TextField();
        errorPopup = new Pane();

        controller.setTitle(title);
        controller.setNameField(nameField);
        controller.setEmailField(emailField);
        controller.setIbanField(ibanField);
        controller.setBicField(bicField);
        controller.setErrorPopup(errorPopup);
        controller.setEvent(event);
    }

}
