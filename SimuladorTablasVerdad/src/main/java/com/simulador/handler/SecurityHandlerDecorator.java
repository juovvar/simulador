package com.simulador.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.simulador.util.HttpResponses;

import java.io.IOException;

public class SecurityHandlerDecorator implements HttpHandler {
    private final HttpHandler originalHandler;

    public SecurityHandlerDecorator(HttpHandler originalHandler) {
        this.originalHandler = originalHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // set() en lugar de add() evita cabeceras duplicadas si se anidan decoradores
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self'; " +
                        "style-src 'self'; " +
                        "img-src 'self' data:; " +
                        "connect-src 'self'; " +
                        "object-src 'none'; " +
                        "frame-ancestors 'none'; " +
                        "form-action 'self'; " +
                        "base-uri 'self';"
        );
        headers.set("X-Frame-Options", "DENY");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("Referrer-Policy", "no-referrer");
        headers.set("Permissions-Policy",
                "camera=(), microphone=(), geolocation=(), payment=(), usb=()");
        headers.set("Cross-Origin-Opener-Policy", "same-origin");
        headers.set("Cross-Origin-Resource-Policy", "same-origin");
        headers.set("Cache-Control", "no-store, no-cache, must-revalidate");

        // HSTS solo tiene efecto sobre HTTPS; el TLS lo termina el proxy inverso
        if (isHttps(exchange)) {
            headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }

        try {
            originalHandler.handle(exchange);
        } catch (Exception e) {
            // Registra el detalle en el servidor y devuelve un mensaje generico al cliente
            System.err.println("Error inesperado en " + exchange.getRequestURI().getPath() + ": " + e);
            e.printStackTrace();
            if (exchange.getResponseCode() == -1) {
                HttpResponses.sendJsonError(exchange, 500, "Error interno del servidor.");
            }
        } finally {
            exchange.close();
        }
    }

    private boolean isHttps(HttpExchange exchange) {
        String forwardedProto = exchange.getRequestHeaders().getFirst("X-Forwarded-Proto");
        return "https".equalsIgnoreCase(forwardedProto != null ? forwardedProto.trim() : null);
    }
}
