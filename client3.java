import java.io.*;
import java.net.*;

public class client3 {
    Socket socket = null;
    DataInputStream in = new DataInputStream(System.in);
    DataOutputStream out = null ;

    @SuppressWarnings("deprecation")
    public client3(String address, int port){
        try {
            socket = new Socket(address,port);
            System.out.println("Socket Connected");
            out = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            System.out.println(e);
        }
        String line="";
        while(!line.equals("over")){
            try {
                line = in.readLine();
                out.writeUTF(line);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        try {
            socket.close();
            in.close();
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        client3 client = new client3("localhost",5002);
    }


}
