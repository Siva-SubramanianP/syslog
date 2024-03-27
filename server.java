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
        FileWriter file = new FileWriter("D:\\Learning\\syslog\\test.log");
        try{
            for(int port : ports){
                ServerSocket server = new ServerSocket(port);
                System.out.println("Server Started for the port "+port);
                
                Thread clientThread = new Thread(() -> {
                    while (true) {
                        try {
                            Socket socket = server.accept();
                            System.out.println("Connection accepted from " + socket.getInetAddress() + " on port " + port);
                            incrementActiveConnections();

                            Thread clientHandlerThread = new Thread(() -> {
                                try {
                                    web(socket,port,file);
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
            // while(!input.equals("over")){
            //     try {
            //         input = in.readUTF();
            //         // System.out.println(input);
            //         String inputLine= format(input,client,port);
            //         file.write(inputLine);
            //     } catch (Exception e) {
            //         System.out.println(e);
            //     }
            // }
            while(true){
                input = in.readUTF();
                if(input.equals("stop")){
                    break;
                }
                else{
                    String inputLine= format(input,client,port);
                    file.write(inputLine);
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
        
        // int pv;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String pri[] = priority(msg);
        String output ="<"+pri[1]+">"+ String.valueOf(dtf.format(now))+" "+client.getInetAddress()+" "+port+" "+"dameon."+pri[0]+": "+ msg + "\n";
        return output;
    }
    public static String[] priority(String msg){
        String pri="";
        int pv;
        String ans[] = new String[2];
        if(msg.toLowerCase().indexOf("error") != -1){
            pri = pri + "ERROR";
            pv=3;
        }
        else if(msg.toLowerCase().indexOf("warning") != -1){
            pri = pri + "WARNING";
            pv=4;
        }
        else if(msg.toLowerCase().indexOf("immediate") != -1){
            pri = pri + "ALERTS";
            pv=1;
        }
        else if(msg.toLowerCase().indexOf("urgent") != -1){
            pri = pri + "EMERGENCY";
            pv=0;
        }
        else{
            pri = pri + "INFO";
            pv=6;
        }
        ans[0] = pri;
        ans[1] = String.valueOf(pv);

        return ans;
    }
}

