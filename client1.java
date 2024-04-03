import java.io.*;
import java.net.*;

public class client1 extends Thread {

    server server;
    
    public client1(server server) {
        this.server = server;
    }
    public void run() {
        Socket socket = null;
        DataOutputStream out = null;

        try {
            socket = new Socket("127.0.0.1", 5000);
            System.out.println("Socket Connected Client 1");
            out = new DataOutputStream(socket.getOutputStream());
            
            String[] inputs = {"error occur while running","warning process still in buildup","urgent call the clients","immediate action need to be taken","error occur while handling script","warning file is not opened","immediate switch the network as public","Hi normal message","urgent help needed from the team","urgent help needed to the file server","warning the file is opened","error handle the exception","error process the thread sequentially","warning this takes too much time","urgent modify server to function efficiently","immediate alert the process thread","error occur in the client","immediate alert the file","error handle the thread correctly","immediate solve the problem","urgent call the clients","over"};  
            int index = 0;
            while(true){
                if(index > 4){
                    index =0;
                }
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
                index++;
            }
            System.out.println("Client 1 Finished sending message");
        } catch (IOException e) {
            e.printStackTrace();
        } 
        try {
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
