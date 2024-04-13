package client.scenes;

import client.enums.ManageUserMode;
import client.utils.ServerUtils;
import client.utils.WebSocketServerUtils;
import commons.Event;
import commons.User;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    public void testUIInitialization() {
        assertNotNull(controller.getNameField());
        assertNotNull(controller.getEmailField());
        assertNotNull(controller.getIbanField());
        assertNotNull(controller.getBicField());
    }

    @Test
    public void testUserCreation() {
        controller.getNameField().setText("John Doe");
        controller.getEmailField().setText("john.doe@example.com");
        controller.getIbanField().setText("123456789");
        controller.getBicField().setText("ABCDEF");

        controller.create();

        verify(serverUtils).createUser(userCaptor.capture());
        assertEquals("John Doe", userCaptor.getValue().getName());
        assertEquals("john.doe@example.com", userCaptor.getValue().getEmail());
        assertEquals("123456789", userCaptor.getValue().getIban());
        assertEquals("ABCDEF", userCaptor.getValue().getBic());
    }

}
