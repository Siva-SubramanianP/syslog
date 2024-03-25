import java.io.*;
import java.net.*;

public class client1 {
    Socket socket = null;
    DataInputStream in = null;
    DataOutputStream out = null ;

    @SuppressWarnings("deprecation")
    public client1(String address, int port){
        try {
            socket = new Socket(address,port);
            System.out.println("Socket Connected");
            in = new DataInputStream(System.in);
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
        client1 client = new client1("127.0.0.1",5000);
    }


}
