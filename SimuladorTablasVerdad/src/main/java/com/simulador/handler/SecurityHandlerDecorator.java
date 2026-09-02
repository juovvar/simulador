package com.simulador.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class SecurityHandlerDecorator implements HttpHandler {
    private final HttpHandler originalHandler;

    public SecurityHandlerDecorator(HttpHandler originalHandler) {
        this.originalHandler = originalHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self'; " +
                        "style-src 'self'; " +
                        "img-src 'self' data:; " +
                        "object-src 'none'; " +
                        "frame-ancestors 'none'; " +
                        "form-action 'self'; " +
                        "base-uri 'self';"
        );
        exchange.getResponseHeaders().add("X-Frame-Options", "DENY");
        exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        exchange.getResponseHeaders().add("Cache-Control", "no-store, no-cache, must-revalidate");

        originalHandler.handle(exchange);
    }
}
