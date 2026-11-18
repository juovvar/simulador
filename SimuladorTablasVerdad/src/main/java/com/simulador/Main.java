package com.simulador;

import com.sun.net.httpserver.HttpServer;
import com.simulador.handler.RateLimitHandlerDecorator;
import com.simulador.handler.SecurityHandlerDecorator;
import com.simulador.handler.StaticFileHandler;
import com.simulador.handler.TruthTableHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_RATE_LIMIT_PER_MINUTE = 120;

    public static void main(String[] args) {
        try {
            int port = resolvePort();
            int rateLimit = resolvePositiveInt("RATE_LIMIT_PER_MINUTE", DEFAULT_RATE_LIMIT_PER_MINUTE);
            boolean trustProxy = Boolean.parseBoolean(System.getenv("TRUST_PROXY"));

            // Tiempos maximos del servidor HTTP (Prevencion Slowloris); se pueden sobrescribir con -D
            setDefaultProperty("sun.net.httpserver.maxReqTime", "10");   // segundos para recibir la peticion
            setDefaultProperty("sun.net.httpserver.maxRspTime", "10");   // segundos para enviar la respuesta
            setDefaultProperty("sun.net.httpserver.maxReqHeaders", "50");

            // Esto permite trafico externo al simulador
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            // Enruta las peticiones aplicando las cabeceras de seguridad
            server.createContext("/", new SecurityHandlerDecorator(new StaticFileHandler()));
            server.createContext("/api/evaluar", new SecurityHandlerDecorator(
                    new RateLimitHandlerDecorator(new TruthTableHandler(), rateLimit, 60_000, trustProxy)));

            // Pool de hilos para gestionar peticiones concurrentes
            server.setExecutor(Executors.newFixedThreadPool(10));

            server.start();

            System.out.println(" Servidor iniciado exitosamente en el puerto: " + port);

        } catch (IllegalStateException e) {
            System.err.println("Configuración inválida: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor HTTP: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static int resolvePort() {
        String portEnv = System.getenv("PORT");
        if (portEnv == null || portEnv.isBlank()) return DEFAULT_PORT;
        try {
            int port = Integer.parseInt(portEnv.trim());
            if (port >= 1 && port <= 65535) return port;
        } catch (NumberFormatException ignored) {
            // Se reporta abajo con un mensaje claro
        }
        throw new IllegalStateException("PORT debe ser un número entre 1 y 65535 (valor recibido: '" + portEnv + "').");
    }

    private static int resolvePositiveInt(String envName, int defaultValue) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed > 0) return parsed;
        } catch (NumberFormatException ignored) {
            // Se reporta abajo con un mensaje claro
        }
        throw new IllegalStateException(envName + " debe ser un entero positivo (valor recibido: '" + value + "').");
    }

    private static void setDefaultProperty(String key, String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }
}
