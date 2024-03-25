import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class server {
    static volatile int activeConnections = 0;
    static final Object lock = new Object();
    static Map<Integer, FileHandler> fileHandlers = new HashMap<>();

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        int[] ports = {5000, 5001, 5002, 5003, 5004};
        // int[] ports = {5000,5001};

        try {
            for (int port : ports) {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);

                // Create a FileHandler for this port
                FileHandler fileHandler = new FileHandler("D:\\Learning\\syslog\\logfiles\\" + "log_port_" + port + ".log");
                fileHandler.setFormatter(new SimpleFormatter());
                fileHandlers.put(port, fileHandler);

                Thread clientThread = new Thread(() -> {
                    while (true) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            System.out.println("Connection accepted from " + clientSocket.getInetAddress() + " on port " + port);
                            incrementActiveConnections();

                            // Start a thread to handle each client connection
                            Thread clientHandlerThread = new Thread(() -> handleClient(clientSocket, port,fileHandler));
                            clientHandlerThread.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, int port,FileHandler file) {
        try {
            // Get the FileHandler for this port
            FileHandler fileHandler = fileHandlers.get(port);
            if (fileHandler == null) {
                System.err.println("FileHandler not found for port " + port);
                return;
            }
            DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            Logger logger = Logger.getLogger("port" + port);
            logger.addHandler(file);
            // Code to handle communication with the client goes here
            // BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine="";
            while (!inputLine.equals("over")){
                // Log the received message
                try{
                    inputLine = in.readUTF();
                    // Level severityLevel = getSeverityLevel(inputLine);
                    logger.log(getSeverityLevel(inputLine), inputLine);
                }
                catch(Exception e){
                    System.out.println(e);
                }
            }

            // Close resources
            in.close();
            clientSocket.close();

            // Decrement active connections
            decrementActiveConnections();

            // Check if all connections are closed
            synchronized (lock) {
                if (activeConnections == 0) {
                    System.out.println("All client connections terminated. Shutting down server...");
                    closeFileHandlers();
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void incrementActiveConnections() {
        synchronized (lock) {
            activeConnections++;
        }
    }

    private static void decrementActiveConnections() {
        synchronized (lock) {
            activeConnections--;
        }
    }

    private static Level getSeverityLevel(String message) {
        // Determine severity level based on message content
        if (message.contains("ERROR")) {
            return Level.SEVERE;
        } else if (message.contains("WARN")) {
            return Level.WARNING;
        } else {
            return Level.INFO;
        }
    }

    private static void closeFileHandlers() {
        for (FileHandler fileHandler : fileHandlers.values()) {
            if (fileHandler != null) {
                fileHandler.close();
            }
        }
    }
}