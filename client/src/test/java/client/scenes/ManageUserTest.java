package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import client.enums.ManageUserMode;
import client.utils.ServerUtils;
import commons.Event;
import commons.User;
import java.util.UUID;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    public void testInvalidEmail() {
        controller.getNameField().setText("John Doe");
        controller.getEmailField().setText("invalid_email");
        controller.create();
        verify(serverUtils, never()).createUser(any());
    }

    @Test
    public void testNullEmail() {
        controller.getNameField().setText("John Doe");
        controller.getEmailField().setText(null);
        controller.create();
        verify(serverUtils, never()).createUser(any());
    }

    @Test
    public void testInvalidIban() {
        controller.getNameField().setText("John Doe");
        controller.getEmailField().setText("john.doe@example.com");
        controller.getIbanField().setText("");
        controller.create();
        verify(serverUtils, never()).createUser(any());
    }

    @Test
    public void testInvalidBic() {
        controller.getNameField().setText("John Doe");
        controller.getEmailField().setText("john.doe@example.com");
        controller.getIbanField().setText("123456789");
        controller.getBicField().setText("");
        controller.create();
        verify(serverUtils, never()).createUser(any());
    }

    @Test
    public void testSaveWithValidInputs() {
        controller.getNameField().setText("John Doe");
        controller.getEmailField().setText("john.doe@example.com");
        controller.getIbanField().setText("123456789");
        controller.getBicField().setText("ABCDEF");
        String inviteCode = UUID.randomUUID().toString();
        when(event.getInviteCode()).thenReturn(UUID.fromString(inviteCode));
        User updatedUser = new User("John Doe",
                "john.doe@example.com",
                "123456789",
                "ABCDEF",
                UUID.fromString(inviteCode));
        when(serverUtils.updateUser(any(User.class))).thenReturn(updatedUser);
        controller.save();
        verify(serverUtils).updateUser(updatedUser);
        verify(mainCtrl).showEventOverview(any());
    }

    @Test
    public void testSaveWithInvalidInputs() {
        controller.getNameField().setText("");
        controller.getEmailField().setText("john.doe@example.com");
        controller.getIbanField().setText("123456789");
        controller.getBicField().setText("ABCDEF");
        controller.save();
        verify(serverUtils, never()).updateUser(any());
        verify(mainCtrl, never()).showEventOverview(any());
    }

    @Test
    public void testCancelInCreateMode() {
        controller.setMode(ManageUserMode.CREATE);
        controller.cancel();
        verify(mainCtrl).showEventOverview(any());
    }

    @Test
    public void testCancelInEditMode() {
        controller.setMode(ManageUserMode.EDIT);
        controller.cancel();
        verify(mainCtrl).showEventOverview(any());
    }

    @Test
    public void testChangeStyleAttribute() {
        Node mockNode = new Node() {};
        String initialStyle = "-fx-font-size: 12px; -fx-background-color: white;";
        mockNode.setStyle(initialStyle);

        String attribute = "-fx-font-size";
        String value = "16px";
        controller.changeStyleAttribute(mockNode, attribute, value);
        String expectedStyle = " -fx-background-color: white;-fx-font-size: 16px;";
        assertEquals(expectedStyle, mockNode.getStyle(), "Style attribute should be updated");
    }

}
