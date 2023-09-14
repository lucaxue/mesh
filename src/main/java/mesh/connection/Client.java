package mesh.src.main.java.mesh.connection;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.lang.Exception;

public class Client {
    private Socket connection;
    private PrintWriter out;
    private String IP;
    private int port;

    public Client(String IP, int port) {
        this.IP = IP;
        this.port = port;
        connect();
    }

    public void connect() {
        try {
            connection = new Socket(IP, port);
            out = new PrintWriter(connection.getOutputStream());
        } catch (Exception e) {
            connect();
        }
    }

    public void close() {
        try {
            connection.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String data){
        out.println(data);
        out.flush();
    }
}
