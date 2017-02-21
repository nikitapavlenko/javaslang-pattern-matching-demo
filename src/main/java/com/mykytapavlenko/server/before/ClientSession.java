package com.mykytapavlenko.server.before;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * Обрабатывает запрос клиента.
 */
public class ClientSession implements Runnable {

    @Override
    public void run() {
        try {
            /* Получаем заголовок сообщения от клиента */
            String header = readHeader();
            System.out.println(header + "\n");
            /* Получаем из заголовка указатель на интересующий ресурс */
            String url = getURIFromHeader(header);
            System.out.println("Resource: " + url + "\n");
            /* Отправляем содержимое ресурса клиенту */
            int code = send(url);
            System.out.println("Result code: " + code + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        initialize();
    }

    private void initialize() throws IOException {
        /* Получаем поток ввода, в который помещаются сообщения от клиента */
        in = socket.getInputStream();
        /* Получаем поток вывода, для отправки сообщений клиенту */
        out = socket.getOutputStream();
    }

    /**
     * Считывает заголовок сообщения от клиента.
     *
     * @return строка с заголовком сообщения от клиента.
     * @throws IOException
     */
    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln = null;
        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty()) {
                break;
            }
            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    /**
     * Вытаскивает идентификатор запрашиваемого ресурса из заголовка сообщения от клиента.
     *
     * @param header
     *            заголовок сообщения от клиента.
     * @return идентификатор ресурса.
     */
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

    /**
     * Отправляет ответ клиенту. В качестве ответа отправляется http заголовок и содержимое указанного ресурса. Если ресурс не указан, отправляется перечень
     * доступных ресурсов.
     *
     * @param url
     *            идентификатор запрашиваемого ресурса.
     * @return код ответа. 200 - если ресурс был найден, 404 - если нет.
     * @throws IOException
     */
    private int send(String url) throws IOException {
        InputStream strm = HttpServer.class.getResourceAsStream(url);
        int code = (strm != null) ? 200 : 404;
        String header = getHeader(code);
        PrintStream answer = new PrintStream(out, true, "UTF-8");
        answer.print(header);
        if (code == 200) {
            int count = 0;
            byte[] buffer = new byte[1024];
            while ((count = strm.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            strm.close();
        }
        return code;
    }

    /**
     * Возвращает http заголовок ответа.
     *
     * @param code
     *            код результата отправки.
     * @return http заголовок ответа.
     */
    private String getHeader(int code) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("HTTP/1.1 " + code + " " + getAnswer(code) + "\n");
        buffer.append("Date: " + new Date().toGMTString() + "\n");
        buffer.append("Accept-Ranges: none\n");
        buffer.append("Content-Type: text/plain\n");
        buffer.append("\n");
        return buffer.toString();
    }

    /**
     * Возвращает комментарий к коду результата отправки.
     *
     * @param code
     *            код результата отправки.
     * @return комментарий к коду результата отправки.
     */
    private String getAnswer(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            default:
                return "Internal Server Error";
        }
    }

    private Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    private static final String DEFAULT_FILES_DIR = "/www";
}
