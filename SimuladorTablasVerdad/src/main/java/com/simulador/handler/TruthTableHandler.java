package com.simulador.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.simulador.logic.TableGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TruthTableHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Permite solo peticiones POST
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(451, -1);
            return;
        }

        // Lee el cuerpo de la peticion HTTP (JSON)
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            jsonBuilder.append(line);
        }

        String requestBody = jsonBuilder.toString();
        // Extrae el valor del parametro "expresion" sin librerias externas
        String expression = extractJsonValue(requestBody, "expresion");

        String jsonResponse;
        int statusCode = 200;

        try {
            // Genera la tabla usando nuestro motor logico
            jsonResponse = TableGenerator.generateJsonTable(expression);
        } catch (Exception e) {
            statusCode = 400;
            jsonResponse = "{\"error\": \"" + escapeJson(e.getMessage()) + "\"}";
        }

        // Envia la respuesta HTTP
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int startKey = json.indexOf(search);
        if (startKey == -1) return "";
        int startColon = json.indexOf(":", startKey);
        int startQuote = json.indexOf("\"", startColon);
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (startQuote != -1 && endQuote != -1) {
            return json.substring(startQuote + 1, endQuote);
        }
        return "";
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}