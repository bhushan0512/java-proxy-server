import java.io.*;
import java.net.*;

class MainServer {
    public static void main(String[] args) {
        try {
            ServerSocket mainServerSocket = new ServerSocket(8080);
            System.out.println("MainServer is running...");
            while (true) {
                Socket connectionSocket = mainServerSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                String request = inFromClient.readLine();
                if (request != null && request.contains("GET /index.html")) {
                    System.out.println("MainServer received request for index.html");
                    File file = new File("index.html");
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] bytes = new byte[(int) file.length()];
                    fileInputStream.read(bytes);
                    outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
                    outToClient.writeBytes("Content-Length: " + bytes.length + "\r\n");
                    outToClient.writeBytes("Content-Type: text/html\r\n"); // Add content type header
                    outToClient.writeBytes("\r\n");
                    outToClient.write(bytes, 0, bytes.length);
                    fileInputStream.close();
                }
                connectionSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
