package com.mykytapavlenko.server.after;

import javaslang.collection.List;
import javaslang.control.Try;

import java.net.ServerSocket;

public class HttpServer {

    private static final int DEFAULT_PORT = 9999;

    public static void main(String[] args) {
        int port = List.of(args).getOption().map(Integer::parseInt).getOrElse(DEFAULT_PORT);

        ServerSocket serverSocket = Try.of(() -> new ServerSocket(port))
                .andThen(socket -> System.out.println("Server started on port: " + socket.getLocalPort() + "\n")).onFailure(System.err::println)
                .getOrElseThrow(() -> new RuntimeException("Server has not been started"));

        while (true) {
            Thread thread = Try.of(serverSocket::accept).mapTry(ClientSession::new).map(Thread::new).andThen(Thread::start).onFailure(System.err::println)
                    .getOrElseThrow(() -> new RuntimeException("Client is not created"));
        }

    }

}
