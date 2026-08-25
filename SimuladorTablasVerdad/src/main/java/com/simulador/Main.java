package com.simulador;

import com.sun.net.httpserver.HttpServer;
import com.simulador.handler.StaticFileHandler;
import com.simulador.handler.TruthTableHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // Crea el servidor en el puerto 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Enruta las peticiones
            server.createContext("/", new StaticFileHandler());
            server.createContext("/api/evaluar", new TruthTableHandler());

            // Asigna un executor con pool de hilos para gestionar peticiones concurrentes
            server.setExecutor(Executors.newFixedThreadPool(10));

            server.start();

            System.out.println("==================================================");
            System.out.println(" Servidor iniciado exitosamente.");
            System.out.println(" Abre tu navegador en: http://localhost:" + PORT);
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor HTTP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
