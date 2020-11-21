package club.banyuan.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Request {

    private Socket client;

    public Request(Socket client){
        this.client=client;
    }

    public void sendMessage(String s){
        try {
            PrintWriter out=new PrintWriter(client.getOutputStream(),true);
            out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
