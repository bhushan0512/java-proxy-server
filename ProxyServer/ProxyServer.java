import java.io.*;
import java.net.*;

class ProxyServer {
    static boolean firstAccess = true;

    public static void main(String[] args) {
        try {
            ServerSocket proxyServerSocket = new ServerSocket(8888);
            System.out.println("ProxyServer is running...");
            while (true) {
                Socket connectionSocket = proxyServerSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                String request = inFromClient.readLine();
                if (request != null && request.contains("GET /index.html")) {
                    System.out.println("ProxyServer received request for index.html");
                    if (firstAccess) {
                        System.out.println("First access, getting page from MainServer...");
                        Socket mainServerSocket = new Socket("localhost", 8080);
                        BufferedReader inFromMainServer = new BufferedReader(new InputStreamReader(mainServerSocket.getInputStream()));
                        DataOutputStream outToMainServer = new DataOutputStream(mainServerSocket.getOutputStream());
                        outToMainServer.writeBytes("GET /index.html HTTP/1.1\r\n");
                        outToMainServer.writeBytes("\r\n");
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = inFromMainServer.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }
                        firstAccess = false;
                        System.out.println("ProxyServer caching index.html...");
                        FileWriter fileWriter = new FileWriter("cached_pages/index.html");
                        fileWriter.write(responseBuilder.toString());
                        fileWriter.close();
                        // outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
                        // outToClient.writeBytes("Content-Length: " + responseBuilder.length() + "\r\n");
                        // outToClient.writeBytes("Content-Type: text/html\r\n"); // Add content type header
                        // outToClient.writeBytes("\r\n");
                        outToClient.writeBytes(responseBuilder.toString());
                        mainServerSocket.close();
                    } else {
                        System.out.println("Returning cached index.html...");
                        BufferedReader cachedPageReader = new BufferedReader(new FileReader("cached_pages/index.html"));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = cachedPageReader.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }
                        // outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
                        // outToClient.writeBytes("Content-Length: " + responseBuilder.length() + "\r\n");
                        // outToClient.writeBytes("Content-Type: text/html\r\n"); // Add content type header
                        // outToClient.writeBytes("\r\n");
                        outToClient.writeBytes(responseBuilder.toString());
                        cachedPageReader.close();
                    }
                }
                connectionSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
