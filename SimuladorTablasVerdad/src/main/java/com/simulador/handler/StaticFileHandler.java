package com.simulador.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.simulador.util.HttpResponses;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class StaticFileHandler implements HttpHandler {

    // Solo segmentos alfanumericos, '-' o '_' y una extension conocida: impide "..", "\", "%00", etc.
    private static final Pattern SAFE_PATH =
            Pattern.compile("^(/[A-Za-z0-9_-]+)+\\.(html|css|js|png|jpg|jpeg|ico)$");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        boolean isHead = "HEAD".equalsIgnoreCase(method);
        if (!"GET".equalsIgnoreCase(method) && !isHead) {
            HttpResponses.sendMethodNotAllowed(exchange, "GET, HEAD");
            return;
        }

        String path = exchange.getRequestURI().getPath();

        // Si se accede a la raiz, sirve el index.html por defecto
        if ("/".equals(path)) {
            path = "/index.html";
        }

        // Rechaza cualquier ruta fuera de la lista blanca (Prevencion de path traversal)
        if (path == null || !SAFE_PATH.matcher(path).matches()) {
            sendNotFound(exchange);
            return;
        }

        // Busca el archivo dentro de src/main/resources/static
        String resourcePath = "static" + path;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                sendNotFound(exchange);
                return;
            }

            // Determina el MIME Type correcto
            exchange.getResponseHeaders().set("Content-Type", getMimeType(path));

            byte[] bytes = inputStream.readAllBytes();
            if (isHead) {
                exchange.getResponseHeaders().set("Content-Length", String.valueOf(bytes.length));
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private void sendNotFound(HttpExchange exchange) throws IOException {
        HttpResponses.sendText(exchange, 404, "404 - Archivo no encontrado");
    }

    private String getMimeType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
}
