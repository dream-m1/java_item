package club.banyuan.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

//接收服务器发来的信息
public class Response {

    private Socket client;

    public Response(Socket client){
        this.client=client;
    }

    public String getMessage(){
        Scanner scan =null;
        try {
            scan=new Scanner(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String msg=scan.nextLine();
        return msg;
    }

}
