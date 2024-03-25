import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class server {
    static volatile int ActiveConnections=0;
    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException{
        int ports[] = {5000,5001,5002,5003,5004};
        // int ports[] = {5000,5001};
        FileWriter file = new FileWriter("D:\\Learning\\syslog\\logfile.log");
        try{
            for(int port : ports){
                ServerSocket server = new ServerSocket(port);
                System.out.println("Server Started for the port "+port);
                
                Thread clientThread = new Thread(() -> {
                    while (true) {
                        try {
                            Socket clientSocket = server.accept();
                            System.out.println("Connection accepted from " + clientSocket.getInetAddress() + " on port " + port);
                            incrementActiveConnections();

                            Thread clientHandlerThread = new Thread(() -> {
                                try {
                                    web(clientSocket, port,file);
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            });
                            clientHandlerThread.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                clientThread.start();
                
                
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // file.close();
    }
    @SuppressWarnings("resource")
    public static void web(Socket client,int port,FileWriter file) throws IOException{
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            
            String input="";
            while(!input.equals("over")){
                try {
                    input = in.readUTF();
                    String inputLine= format(input,client,port);
                    file.write(inputLine);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            in.close();
            client.close();

            decrementActiveConnections();

            if(ActiveConnections == 0){
                System.out.println("Server Terminated...");
                file.close();
                System.exit(0);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        // file.close();

    }
    public static void incrementActiveConnections(){
        ActiveConnections++;
    }

    public static void decrementActiveConnections(){
        ActiveConnections--;
    }
    public static String format(String msg,Socket client,int port){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String pri = priority(msg);
        String output = String.valueOf(dtf.format(now))+" "+client.getInetAddress()+" "+port+" "+pri+" "+ msg + "\n";
        return output;
    }
    public static String priority(String msg){
        String pri="";
        if(msg.toLowerCase().indexOf("ERROR".toLowerCase()) != -1){
            pri = pri + "ERROR";
        }
        else if(msg.toLowerCase().indexOf("Warning".toLowerCase()) != -1){
            pri = pri + "WARNING";
        }
        else if(msg.toLowerCase().indexOf("Immediate".toLowerCase()) != -1){
            pri = pri + "ALERTS";
        }
        else if(msg.toLowerCase().indexOf("Urgent".toLowerCase()) != -1){
            pri = pri + "EMERGENCY";
        }
        else{
            pri = pri + "INFO";
        }

        return pri;
    }
}
