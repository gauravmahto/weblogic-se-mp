package com.acme;

import io.helidon.http.Status;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpRules;

import java.util.concurrent.CountDownLatch;

public final class Main {

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

    private static void routing(HttpRouting.Builder rules) {
        rules.get("/hello", (req, res) -> res.send("Hello from Helidon SE"));
        rules.get("/health", (req, res) -> res.status(Status.OK_200).send("OK"));
        rules.get("/echo/{msg}", (req, res) -> {
            String msg = req.path().pathParameters().get("msg");
            res.send("echo: " + msg);
        });
    }

    private static void waitForever() {
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Main() {}
}
