import java.io.FileWriter;
import java.io.IOException;

public class writeTofile extends Thread{
    static String name = Thread.currentThread().getName();
    public void run(){
        try (FileWriter file = new FileWriter("D:\\Learning\\syslog\\test1.log")) {
            while(true){                
                try {
                    String addFile = server.queue.take();
                    file.write(addFile);
                    // System.out.println(addFile);
                } catch (IOException | InterruptedException e) {   
                    e.printStackTrace();
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

