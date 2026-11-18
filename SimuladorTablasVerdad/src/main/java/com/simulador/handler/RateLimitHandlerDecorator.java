package com.simulador.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.simulador.util.HttpResponses;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Limita las peticiones por cliente con una ventana fija de tiempo
public class RateLimitHandlerDecorator implements HttpHandler {

    // Por encima de este numero de clientes se purgan las ventanas expiradas
    private static final int CLEANUP_THRESHOLD = 10_000;

    private final HttpHandler originalHandler;
    private final int maxRequests;
    private final long windowMillis;
    private final boolean trustProxy;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitHandlerDecorator(HttpHandler originalHandler, int maxRequests, long windowMillis, boolean trustProxy) {
        this.originalHandler = originalHandler;
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.trustProxy = trustProxy;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        long now = System.currentTimeMillis();

        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.values().removeIf(w -> now - w.start() >= windowMillis);
        }

        Window window = windows.compute(clientKey(exchange),
                (key, old) -> (old == null || now - old.start() >= windowMillis) ? new Window(now, new AtomicInteger()) : old);

        if (window.count().incrementAndGet() > maxRequests) {
            long retryAfter = Math.max(1, (window.start() + windowMillis - now + 999) / 1000);
            exchange.getResponseHeaders().set("Retry-After", String.valueOf(retryAfter));
            HttpResponses.sendJsonError(exchange, 429, "Demasiadas solicitudes. Intente de nuevo en unos segundos.");
            return;
        }

        originalHandler.handle(exchange);
    }

    // Identifica al cliente por IP; detras de un proxy confiable usa la IP que este añadio a X-Forwarded-For
    private String clientKey(HttpExchange exchange) {
        if (trustProxy) {
            String forwardedFor = exchange.getRequestHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null) {
                String[] hops = forwardedFor.split(",");
                String last = hops[hops.length - 1].trim();
                if (!last.isEmpty()) return last;
            }
        }
        return exchange.getRemoteAddress().getAddress().getHostAddress();
    }

    private record Window(long start, AtomicInteger count) {
    }
}
