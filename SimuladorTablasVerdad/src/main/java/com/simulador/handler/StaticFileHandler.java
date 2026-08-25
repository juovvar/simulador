package com.simulador.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            // Si se accede a la raiz, sirve el index.html por defecto
            if (path.equals("/")) {
                path = "/index.html";
            }

            // Busca el archivo dentro de src/main/resources/static
            String resourcePath = "static" + path;
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

            if (inputStream == null) {
                String response = "404 - Archivo no encontrado";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            // Determina el MIME Type correcto
            String mimeType = getMimeType(path);
            exchange.getResponseHeaders().set("Content-Type", mimeType);

            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
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
