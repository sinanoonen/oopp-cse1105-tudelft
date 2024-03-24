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

import commons.Event;
import commons.Quote;
import commons.transactions.Expense;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import org.glassfish.jersey.client.ClientConfig;

/**
 * Utilities for the server.
 */
public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    /**
     * Get quotes the hard way.
     *
     * @throws IOException may occur.
     * @throws URISyntaxException may occur.
     */
    public void getQuotesTheHardWay() throws IOException, URISyntaxException {
        var url = new URI("http://localhost:8080/api/quotes").toURL();
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Get all events.
     *
     * @return List of all events on DB
     */
    public List<Event> getEvents() {
        return ClientBuilder.newClient(new ClientConfig())
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
     * Get all quotes.
     *
     * @return all quotes
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    /**
     * Add a quote.
     *
     * @param quote a quote
     * @return the quote
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
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
}