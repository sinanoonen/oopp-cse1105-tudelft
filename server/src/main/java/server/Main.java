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

package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main class for the server.
 */
@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main {

    /**
     * The main method that starts the server.
     * It also loads the password service and prints the password.
     *
     * @param args any arguments passed at runtime
     */
    public static void main(String[] args) {
        //SpringApplication.run(Main.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        AdminPasswordService passwordService = context.getBean(AdminPasswordService.class);
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_RED + "Admin Password: " + passwordService.getAdminPassword() + ANSI_RESET);
    }
}