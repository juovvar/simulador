package com.simulador;

import com.sun.net.httpserver.HttpServer;
import com.simulador.handler.StaticFileHandler;
import com.simulador.handler.TruthTableHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try {
            String portEnv = System.getenv("PORT");
            int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 8080;

            // Esto permite trafico externo al simulador
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            // Enruta las peticiones
            server.createContext("/", new StaticFileHandler());
            server.createContext("/api/evaluar", new TruthTableHandler());

            // Pool de hilos para gestionar peticiones concurrentes
            server.setExecutor(Executors.newFixedThreadPool(10));

            server.start();

            System.out.println(" Servidor iniciado exitosamente en el puerto: " + port);

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor HTTP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}