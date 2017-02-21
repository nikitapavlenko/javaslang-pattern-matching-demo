package com.mykytapavlenko.server.before;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Обрабатывает запросы от клиентов, возвращая файлы, указанные в url-path или ответ с кодом 404, если такой файл не найден.
 *
 */
public class HttpServer {

    /**
     * Первым аргументом может идти номер порта.
     */
    public static void main(String[] args) {
        /* Если аргументы отсутствуют, порт принимает значение поумолчанию */
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        /* Создаем серверный сокет на полученном порту */
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + serverSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println("Port " + port + " is blocked.");
            System.exit(-1);
        }
        /*
         * Если порт был свободен и сокет был успешно создан, можно переходить к следующему шагу - ожиданию клинтов
         */
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                /*
                 * Для обработки запроса от каждого клиента создается отдельный объект и отдельный поток
                 */
                ClientSession session = new ClientSession(clientSocket);
                new Thread(session).start();
            } catch (IOException e) {
                System.out.println("Failed to establish connection.");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }

    private static final int DEFAULT_PORT = 9999;
}
