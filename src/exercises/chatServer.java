package exercises;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class chatServer {
    public static void main(String[] args) {

        try{
            ServerSocket ss=new ServerSocket(6666); // listening
            Socket s=ss.accept();//establishes connection with client
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String  str = (String)dis.readUTF();
            System.out.println("message= "+str);
            ss.close();
        }catch(Exception e){System.out.println(e);}

    }
}
