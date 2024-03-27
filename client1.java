import java.io.*;
import java.net.*;
import java.util.Random;

public class client1 {
    Socket socket = null;
    DataInputStream in = null;
    DataOutputStream out = null ;

    // @SuppressWarnings("deprecation")
    public client1(String address, int port){
        try {
            socket = new Socket(address,port);
            System.out.println("Socket Connected");
            in = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            System.out.println(e);
        }
        // String line="";
        // while(!line.equals("over")){
        //     try {
        //         line = in.readLine();
        //         out.writeUTF(line);
        //     } catch (Exception e) {
        //         System.out.println(e);
        //     }
        // }
        String[] inputs = {"error occur while running","warning process still in buildup","urgent call the clients","immediate action need to be taken","error occur while handling script","warning file is not opened","immediate switch the network as public","Hi normal message","urgent help needed from the team","urgent help needed to the file server","warning the file is opened","error handle the exception","error process the thread sequentially","warning this takes too much time","urgent modify server to function efficiently","immediate alert the process thread","error occur in the client","immediate alert the file","error handle the thread correctly","immediate solve the problem","urgent call the clients"};  
        Random random = new Random();
        while(true){
            int index = random.nextInt(21);
            
            System.out.println(index);
            if(inputs[index].equals("over")){
                break;
            }
            else{
                try {
                    out.writeUTF(inputs[index]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            @SuppressWarnings("deprecation")
            String line=in.readLine();
            out.writeUTF(line);
        } catch (IOException e) {
            e.printStackTrace();
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
