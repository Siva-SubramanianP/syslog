import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

public class server {
    static int activeConnections = 0;
    static BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
    public static void main(String[] args) throws IOException {
        int[] ports = {5000, 5001, 5002, 5003, 5004};
        ExecutorService exe = Executors.newFixedThreadPool(5);

        server ser = new server();
        client1 client1 = new client1(ser);
        client2 client2 = new client2(ser);
        client3 client3 = new client3(ser);
        client4 client4 = new client4(ser);
        client5 client5 = new client5(ser);

        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        writeTofile t1 = new writeTofile();
        t1.start();

        for (int port : ports) {
            startServer(port,exe);
        }
    }

    public static void startServer(int port,ExecutorService exe) {
        exe.execute(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("Server Started for the port " + port);
                    try {
                        Socket socket = server.accept();
                        System.out.println("Connection accepted from " + socket.getInetAddress() + " on port " + port);
                        incrementActiveConnections();                            
                        exe.execute(() -> {
                            try {
                                if(port == 5000){
                                    Thread.currentThread().setName("Client1");
                                }
                                else if(port == 5001){
                                    Thread.currentThread().setName("Client2");
                                }
                                else if(port == 5002){
                                    Thread.currentThread().setName("Client3");
                                }
                                else if(port == 5003){
                                    Thread.currentThread().setName("Client4");
                                }
                                else{
                                    Thread.currentThread().setName("Client5");
                                }
                                String name=Thread.currentThread().getName();
                                handleClient(socket, port,name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void handleClient(Socket client, int port,String name) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()))) {
        String input;
        while (true) {
            input = in.readUTF();
            if (input.equals("stop")) {
                break;
            } else {
                String formattedInput = format(input, client, port, name);
                queue.offer(formattedInput);
            }
        }
        in.close();
        client.close();
        decrementActiveConnections();
        
        if (activeConnections == 0) {
            System.out.println("Server Terminated...");
            System.exit(0);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    public static void incrementActiveConnections() {
        activeConnections++;
    }

    public static void decrementActiveConnections() {
        activeConnections--;
    }

    public static String format(String msg, Socket client, int port, String name) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String[] pri = priority(msg);
        // return "<" + pri[1] + ">" + dtf.format(now) + " " + client.getInetAddress() + " " + port + " " +"Received from "+name+" Writing "+writeTofile.name + " " + "dameon." + pri[0] + ": " + msg + "\n";
        return "<" + pri[1] + "> " + dtf.format(now) + " " + client.getInetAddress() + " " + port + " " +writeTofile.name + " " + "dameon." + pri[0] + ": " + msg + "\n";
    }

    public static String[] priority(String msg){
        String pri = "";
        int pv;
        String[] ans = new String[2];
        if (msg.toLowerCase().contains("error")) {
            pri = "ERROR";
            pv = 3;
        } else if (msg.toLowerCase().contains("warning")) {
            pri = "WARNING";
            pv = 4;
        } else if (msg.toLowerCase().contains("immediate")) {
            pri = "ALERTS";
            pv = 1;
        } else if (msg.toLowerCase().contains("urgent")) {
            pri = "EMERGENCY";
            pv = 0;
        } else {
            pri = "INFO";
            pv = 6;
        }
        ans[0] = pri;
        ans[1] = String.valueOf(pv);
        return ans;
    }
}
