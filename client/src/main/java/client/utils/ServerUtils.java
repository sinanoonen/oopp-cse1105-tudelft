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

package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import commons.EmailRequest;
import commons.Event;
import commons.User;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Tag;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.application.Platform;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.http.HttpStatus;

/**
 * Utilities for the server.
 */
public class ServerUtils {

    private static String ip = "localhost";
    private static String port = "8080";
    private static String SERVER = "http://localhost:8080/";
    private static final ExecutorService EXEC = Executors.newFixedThreadPool(10);

    /**
     * Sets the server.
     *
     * @param ip ip of server to connect to
     * @param port port to connect to
     */
    public static void setServer(String ip, String port) {
        ServerUtils.ip = ip;
        ServerUtils.port = port;
        SERVER = "http://" + ip + ":" + port + "/";
        System.out.println("SERVER UPDATED TO: " + SERVER);
    }

    public static String getServer() {
        return SERVER;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        ServerUtils.ip = ip;
        ServerUtils.setServer(ip, port);
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        ServerUtils.port = port;
        ServerUtils.setServer(ip, port);
    }

    /**
     * Get all events.
     *
     * @return List of all events on DB
     */
    public List<Event> getEvents() {
        return ClientBuilder.newClient(new ClientConfig()
                        .property(ClientProperties.CONNECT_TIMEOUT, 5000)
                        .property(ClientProperties.READ_TIMEOUT, 5000))
                .target(SERVER).path("api/events")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Gets an event from the database by its UUID.
     *
     * @param uuid uuid to search for
     * @return the event
     */
    public Event getEventByUUID(UUID uuid) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + uuid.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Uses long-polling to continuously check if events are updated or deleted.
     *
     * @param callback function to perform on end of poll
     */
    public void longPollEvents(Consumer<String> callback) {
        EXEC.submit(() -> { // use new thread so that application is not stuck waiting for response
            while (!Thread.interrupted()) {
                var res = ClientBuilder.newClient(new ClientConfig()
                                .property(ClientProperties.CONNECT_TIMEOUT, 10000)
                                .property(ClientProperties.READ_TIMEOUT, 10000))
                        .target(SERVER).path("api/events/poll")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);

                var status = res.getStatus();
                if (status == HttpStatus.NO_CONTENT.value()) {
                    continue;
                }
                String updatedEventInviteCode = res.readEntity(String.class);

                //Execute the UI Update on the
                // thread that is responsible for the JavaFX application
                Platform.runLater(() -> {
                    callback.accept(updatedEventInviteCode);
                });
            }
        });
    }

    /**
     * Sends HTTP request to add payment to db.
     *
     * @param uuid uuid of event to add expense to
     * @param payment expense to be added
     * @return added expense
     */
    public Payment addPayment(UUID uuid, Payment payment) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + uuid.toString() + "/transactions/payments")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(payment, APPLICATION_JSON), Payment.class);
    }

    /**
     * Sends HTTP request to add expense to db.
     *
     * @param uuid uuid of event to add expense to
     * @param expense expense to be added
     * @return added expense
     */
    public Expense addExpense(UUID uuid, Expense expense) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + uuid.toString() + "/transactions/expenses")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    /**
     * Updates expense in the db.
     *
     * @param uuid of the event
     * @param expense expense to be updated
     * @return updated expense
     */
    //  @PutMapping("/{uuid}/transactions/expenses/{id}")
    public Expense updateExpense(UUID uuid, Expense expense) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + uuid.toString() + "/transactions/expenses/" + expense.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    /**
     * Removes expense from the server.
     *
     * @param uuid event id
     * @param expense expense to be removed
     */
    //"/{uuid}/transactions/{id}"
    public void removeExpense(UUID uuid, Expense expense) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + uuid.toString() + "/transactions/" + expense.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();

    }



    /**
     * Returns all users stored in the database.
     */
    public List<User> getUsers() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Creates a new user by sending a POST request to the server's API endpoint for users.
     *
     * @param user the User object containing the user details to be created
     * @return the created User object returned by the server
     */
    public User createUser(User user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), User.class);
    }

    /**
     * Updates an existing user by sending a PUT request to the server's API endpoint for users.
     *
     * @param updated the User object containing the user details to be updated
     * @return the updated User object returned by the server
     */
    public User updateUser(User updated) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/users/" + updated.getEmail())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(updated, APPLICATION_JSON), User.class);
    }

    /**
     * Adds a user to the provided event.
     *
     * @param event event to which the user should be added
     * @param user user to be added to event
     * @return updated event
     */
    public Event addUserToEvent(Event event, User user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + event.getInviteCode() + "/users")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), Event.class);
    }

    /**
     * Removes a participant from an event.
     *
     * @param uuid uuid of event to remove from
     * @param email email of user to remove
     */
    public void removeUserFromEvent(UUID uuid, String email) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + uuid.toString() + "/users/" + email)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    /**
     * Adds a new event to the database.
     *
     * @param event event to be added
     * @return added event
     */
    public Event addNewEvent(Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    /**
     * Method to update an event.
     *
     * @param event updated event
     * @return the updated event
     */
    public Event updateEvent(Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + event.getInviteCode())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    /**
     * Deletes an event.
     *
     * @param uuid the uuid of the event
     */
    public void deleteEvent(UUID uuid) {
        ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/events/" + uuid)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .delete();
    }

    /**
     * This sends a post request to the admin controller.
     *
     * @param password the password (in string representation)
     * @return true iff the entered password is the correct one
     */
    public boolean authenticate(String password) {
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/auth")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.entity(password, APPLICATION_JSON));
        return response.getStatus() == 200;
    }

    /**
     * Sends the email request to the server.
     *
     * @param emailRequest the data
     * @return true iff the mail was successfully sent
     */
    public boolean sendMail(EmailRequest emailRequest) {
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/email/send")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.entity(emailRequest, APPLICATION_JSON));

        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    /**
     * This adds a new tag.
     *
     * @param tag the tag to be added
     * @return the added tag
     */
    public Tag addNewTag(Tag tag) {
        return ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("/api/tags")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }


    public void stop() {
        EXEC.shutdownNow();
    }
}
