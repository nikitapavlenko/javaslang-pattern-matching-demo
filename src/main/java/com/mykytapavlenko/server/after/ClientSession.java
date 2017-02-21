package com.mykytapavlenko.server.after;

import javaslang.control.Try;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.collection.Iterator.continually;

public class ClientSession implements Runnable {

    private static final String DEFAULT_FILES_DIR = "/www";
    public static final String SEPARATOR = System.getProperty("line.separator");

    private Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        int resultCode = Try.of(this::readHeader).map(this::getURIFromHeader).mapTry(this::send)
                .andThen(code -> System.out.println("Result code: " + code + "\n")).onFailure(System.err::println).get();

        Try.run(socket::close).onFailure(System.err::println);

    }

    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return continually(reader.readLine()).takeWhile(Objects::nonNull).foldLeft(new StringBuilder(), (builder, s) -> builder.append(s).append(SEPARATOR))
                .toString();

    }

    private String getURIFromHeader(String header) {
        int from = header.indexOf(" ") + 1;
        int to = header.indexOf(" ", from);
        String uri = header.substring(from, to);
        int paramIndex = uri.indexOf("?");
        if (paramIndex != -1) {
            uri = uri.substring(0, paramIndex);
        }
        return DEFAULT_FILES_DIR + uri;
    }

    private int send(String url) throws IOException {
        PrintStream answer = new PrintStream(out, true, "UTF-8");

        Function<InputStream, Integer> writeNotFound = (InputStream stream) -> {
            answer.print(getHeader(404));
            return 404;
        };

        Function<InputStream, Integer> writeSuccess = (stream) -> {
            answer.print(getHeader(200));
            writeResponse(stream).get();
            return 200;
        };

        return Match(HttpServer.class.getResourceAsStream(url)).of(Case(Objects::isNull, writeNotFound), Case(Objects::nonNull, writeSuccess));
    }

    private Try writeResponse(InputStream stream) {
        return Try.run(() -> {
            int count = 0;
            byte[] buffer = new byte[1024];
            while ((count = stream.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            stream.close();
        });
    }

    private String getHeader(int code) {
        return new StringBuilder().append("HTTP/1.1 ").append(code).append(" ").append(getAnswer(code)).append("\n").append("Date: ")
                .append(new Date().toGMTString()).append("\n").append("Accept-Ranges: none\n").append("Content-Type: " + "\n").append("\n").toString();
    }

    private String getAnswer(int code) {
        return Match(code).of(Case(200, "OK"), Case(404, "Not Found"));

    }

}
