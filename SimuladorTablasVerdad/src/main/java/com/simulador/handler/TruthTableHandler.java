package com.simulador.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.simulador.logic.TableGenerator;
import com.simulador.util.HttpResponses;
import com.simulador.util.Json;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class TruthTableHandler implements HttpHandler {

    // Tamaño maximo del cuerpo (la expresion admite 50 caracteres; 1 KB deja margen al JSON)
    private static final int MAX_BODY_BYTES = 1024;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Permite solo peticiones POST
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpResponses.sendMethodNotAllowed(exchange, "POST");
            return;
        }

        // Exige JSON como tipo de contenido
        if (!isJsonContentType(exchange.getRequestHeaders().getFirst("Content-Type"))) {
            HttpResponses.sendJsonError(exchange, 415, "El tipo de contenido debe ser application/json.");
            return;
        }

        // Rechaza cuerpos demasiado grandes antes de leerlos (Prevencion DoS)
        if (declaredLength(exchange) > MAX_BODY_BYTES) {
            HttpResponses.sendJsonError(exchange, 413, "La petición excede el tamaño máximo permitido.");
            return;
        }

        // Lee como maximo MAX_BODY_BYTES + 1 bytes, sin importar lo que declare el cliente
        byte[] body;
        try (InputStream is = exchange.getRequestBody()) {
            body = is.readNBytes(MAX_BODY_BYTES + 1);
        }
        if (body.length > MAX_BODY_BYTES) {
            HttpResponses.sendJsonError(exchange, 413, "La petición excede el tamaño máximo permitido.");
            return;
        }

        try {
            String expression = Json.parseFlatObject(new String(body, StandardCharsets.UTF_8)).get("expresion");

            // Genera la tabla usando nuestro motor logico
            HttpResponses.sendJson(exchange, 200, TableGenerator.generateJsonTable(expression));
        } catch (IllegalArgumentException e) {
            // Solo los errores de validacion controlados se devuelven al cliente
            HttpResponses.sendJsonError(exchange, 400, e.getMessage());
        }
    }

    private boolean isJsonContentType(String contentType) {
        if (contentType == null) return false;
        String mediaType = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return mediaType.equals("application/json");
    }

    // Devuelve el Content-Length declarado, o -1 si no existe (p. ej. chunked)
    private long declaredLength(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Content-Length");
        if (header == null) return -1;
        try {
            return Long.parseLong(header.trim());
        } catch (NumberFormatException e) {
            return Long.MAX_VALUE;
        }
    }
}
