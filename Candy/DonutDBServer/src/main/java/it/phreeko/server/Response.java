package it.phreeko.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Response {

    public static void send(String response, HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("content-type", "application/json");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }

    public static void notFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
        exchange.getResponseBody().close();
    }

    public static void created(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_CREATED, 0);
        exchange.getResponseBody().close();
    }

    public static void ok(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
    }

    public static void conflict(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_CONFLICT, 0);
        exchange.getResponseBody().close();
    }

}
