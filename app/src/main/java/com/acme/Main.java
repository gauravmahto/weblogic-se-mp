package com.acme;

import io.helidon.http.Status;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import java.util.concurrent.CountDownLatch;

public final class Main {

    private static final UserService userService = new UserService();

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getProperty("server.port", "8080"));

        WebServer server = WebServer.builder()
                .port(port)
                .routing(Main::routing)
                .build();

        server.start();

        System.out.println("Helidon SE started: http://localhost:" + port);
        System.out.println("Try: curl http://localhost:" + port + "/hello");
        System.out.println("Try: curl http://localhost:" + port + "/health");

        // Keep JVM alive (useful when running outside Gradle, too)
        waitForever();
    }

    public static void routing(HttpRouting.Builder rules) {
        rules.get("/hello", (req, res) -> res.send("Hello from Helidon SE"));
        rules.get("/health", (req, res) -> res.status(Status.OK_200).send("OK"));
        rules.get("/echo/{msg}", (req, res) -> {
            String msg = req.path().pathParameters().get("msg");
            res.send("echo: " + msg);
        });

        // User CRUD endpoints
        rules.get("/users", (req, res) -> {
            String name = req.query().first("name").orElse(null);
            String email = req.query().first("email").orElse(null);
            var users = (isBlank(name) && isBlank(email))
                    ? userService.getAllUsers()
                    : userService.findUsers(name, email);
            res.send(users);
        });

        rules.get("/users/{id}", (req, res) -> {
            String id = req.path().pathParameters().get("id");
            userService.getUserById(id)
                    .ifPresentOrElse(
                            res::send,
                            () -> sendError(res, Status.NOT_FOUND_404, "User not found", "No user with id: " + id));
        });

        rules.post("/users", (req, res) -> {
            User user = readUser(req, res);
            if (user == null) {
                return;
            }
            String validationError = validateUser(user);
            if (validationError != null) {
                sendError(res, Status.BAD_REQUEST_400, "Validation error", validationError);
                return;
            }
            User created = userService.createUser(user);
            res.status(Status.CREATED_201).send(created);
        });

        rules.put("/users/{id}", (req, res) -> {
            String id = req.path().pathParameters().get("id");
            User user = readUser(req, res);
            if (user == null) {
                return;
            }
            String validationError = validateUser(user);
            if (validationError != null) {
                sendError(res, Status.BAD_REQUEST_400, "Validation error", validationError);
                return;
            }
            userService.updateUser(id, user)
                    .ifPresentOrElse(
                            res::send,
                            () -> sendError(res, Status.NOT_FOUND_404, "User not found", "No user with id: " + id));
        });

        rules.delete("/users/{id}", (req, res) -> {
            String id = req.path().pathParameters().get("id");
            if (userService.deleteUser(id)) {
                res.status(Status.NO_CONTENT_204).send();
            } else {
                sendError(res, Status.NOT_FOUND_404, "User not found", "No user with id: " + id);
            }
        });
    }

    private static User readUser(ServerRequest req, ServerResponse res) {
        try {
            return req.content().as(User.class);
        } catch (RuntimeException ex) {
            sendError(res, Status.BAD_REQUEST_400, "Invalid JSON", "Request body must be valid JSON.");
            return null;
        }
    }

    private static String validateUser(User user) {
        if (user == null) {
            return "User payload is required.";
        }
        if (isBlank(user.getName())) {
            return "name is required.";
        }
        if (isBlank(user.getEmail())) {
            return "email is required.";
        }
        if (!user.getEmail().contains("@")) {
            return "email must contain '@'.";
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void sendError(ServerResponse res, Status status, String message, String details) {
        res.status(status).send(new ErrorResponse(message, details));
    }

    private static void waitForever() {
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Main() {
    }
}
