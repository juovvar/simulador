package com.simulador.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

// Envio uniforme de respuestas HTTP con Content-Type y longitud correctos
public final class HttpResponses {

    private HttpResponses() {
    }

    public static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        send(exchange, statusCode, "application/json; charset=UTF-8", json);
    }

    public static void sendJsonError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJson(exchange, statusCode, "{\"error\": \"" + Json.escape(message) + "\"}");
    }

    public static void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        send(exchange, statusCode, "text/plain; charset=UTF-8", text);
    }

    // Responde 405 indicando los metodos permitidos
    public static void sendMethodNotAllowed(HttpExchange exchange, String allowed) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowed);
        sendText(exchange, 405, "405 - Método no permitido");
    }

    private static void send(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        boolean isHead = "HEAD".equalsIgnoreCase(exchange.getRequestMethod());
        exchange.sendResponseHeaders(statusCode, isHead ? -1 : bytes.length);
        if (!isHead) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
