package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpServer {

    private static final int PORT = 8000;
    private final Logger logger;

    public HttpServer() {
        logger = Logger.getLogger(HttpServer.class.getName());
        logger.log(java.util.logging.Level.INFO, "The web server is starting up...");
        init();
    }

    public void init() {
        try {

            ServerSocket serverSocket = new ServerSocket(PORT);
            int i = 0;
            while (i < Integer.MAX_VALUE) {
                Socket socket = serverSocket.accept();
                handleRequest(socket);
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleRequest(Socket socket) throws Exception {

        try (socket;
             BufferedReader fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter toNetwork = new PrintWriter(socket.getOutputStream(), true)) {

            String requestLine = fromNetwork.readLine();

            System.out.println("requestLine = " + requestLine);

            if (requestLine != null) {
                String[] requestTokens = requestLine.split(" ");
                if (requestTokens.length >= 2 && requestTokens[0].equals("GET")) {

                    String requestedResource = requestTokens[1].substring(1); // Remove leading "/"
                    File file = new File(requestedResource);

                    if (file.exists() && file.isFile() && file.getName().contains(".html")) {

                        logger.log(Level.FINE, "File html {0} was found ", file.getName());
                        sendHtml(socket, toNetwork, file);

                    }

                    if (file.exists() && file.isFile() && file.getName().contains(".png")) {

                        logger.log(Level.FINE, "File png {0} was found ", file.getName());
                        getPng(socket, toNetwork, file);

                    }

                    else error(toNetwork, "HTTP/1.1 404 Not Found", "404 Not Found - The requested resource does not exist.");


                } else {
                    error(toNetwork, "HTTP/1.1 400 Bad Request", "400 Bad Request - Invalid request.");
                }
            }
        }
    }


    /**
     * This method is used to print the error message about the http protocol
     *
     * @param toNetwork socket
     * @param header    header
     * @param body      body
     */
    private void error(PrintWriter toNetwork, String header, String body) {

        toNetwork.println(header);
        toNetwork.println("\r\n");
        toNetwork.println(body);

    }

    /**
     * This method is used to get the png file
     *
     * @param socket
     * @param toNetwork
     * @param file
     */
    private void getPng(Socket socket, PrintWriter toNetwork, File file) {

        logger.log(Level.INFO, "File {0} was found ", file);
        StringBuilder stringBuilder = infoHttp(socket, file);
        stringBuilder.append("Cache-Control: max-age=3600, public\r\n");
        stringBuilder.append("Content-Type: image/png\r\n");
        stringBuilder.append("Accept-Ranges: bytes\r\n");
        stringBuilder.append("Content-Length: ").append(file.length()).append("\r\n");
        stringBuilder.append("Connection: close\r\n\r\n");

        toNetwork.print(stringBuilder);
        toNetwork.flush();

        try (BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                socket.getOutputStream().write(buffer, 0, bytesRead);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * This method is used to get the general information of the http protocol
     *
     * @param socket socket
     * @param file   file
     * @return stringBuilder
     */
    private StringBuilder infoHttp(Socket socket, File file) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 200 OK");
        stringBuilder.append("Server:").append(socket.getLocalAddress().getHostName());
        stringBuilder.append("Date:").append(new java.util.Date());
        stringBuilder.append("Last-Modified:").append(new java.util.Date(file.lastModified()));

        return stringBuilder;
    }

    /**
     * This method is used to get the html file
     *
     * @param socket    socket
     * @param toNetwork toNetwork
     * @param file      file
     * @throws IOException IOException
     */
    private void sendHtml(Socket socket, PrintWriter toNetwork, File file) throws IOException {
        StringBuilder stringBuilder = infoHttp(socket, file);
        stringBuilder.append("Cache-Control: max-age=3600, public\r\n");
        stringBuilder.append("Content-Length: ").append(file.length()).append("\r\n");
        stringBuilder.append("Content-Type: text/html\r\n");
        stringBuilder.append("Connection: close\r\n\r\n");

        toNetwork.print(stringBuilder.toString());
        toNetwork.flush();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                toNetwork.println(line);
            }
        }
    }


}
