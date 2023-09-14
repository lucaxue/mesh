import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class hotMesh {
    public static void main(String[] args) throws InterruptedException {
        int x_location, y_location;
        hotMeshCommunicator hotMatchNearYou = new hotMeshCommunicator(9005, "137.205.113.36");
        System.out.println("Connected to main server, Waiting for response");

        String location = hotMatchNearYou.read();
        String[] locations = location.split(",");
        x_location = Integer.parseInt(locations[0]);
        y_location = Integer.parseInt(locations[1]);
        System.out.println("Recieved location " + x_location + "," + y_location + ". Waiting for neighbour information");

        String neighbour = hotMatchNearYou.read();
        String[] neighbours = neighbour.split(",");

        hotMeshCommunicator up = null;
        hotMeshCommunicator right = null;
        hotMeshCommunicator down = null;
        hotMeshCommunicator left = null;

        if (!neighbours[0].equals("N")) {
            up = new hotMeshCommunicator(Integer.parseInt(neighbours[1]), neighbours[0]);
            System.out.println(up);
        }
        if (!neighbours[2].equals("N")) {
            right = new hotMeshCommunicator(Integer.parseInt(neighbours[3]), neighbours[2]);
            System.out.println(right);
        }
        if (!neighbours[4].equals("N")) {
            down = new hotMeshCommunicator(Integer.parseInt(neighbours[5]), neighbours[4]);
            System.out.println(down);
        }
        if (!neighbours[6].equals("N")) {
            left = new hotMeshCommunicator(Integer.parseInt(neighbours[7]), neighbours[6]);
            System.out.println(left);
        }

        while (true){
            Thread.sleep(4000);
            if(up != null){
                up.send("Hello cell above me :)");
                System.out.println("up read: " + up.read());
            }
            if(right != null){
                right.send("Hello cell to my right :)");
                System.out.println("right read: " + right.read());
            }
            if(down != null){
                down.send("Hello cell below me :)");
                System.out.println("down read: " + down.read());
            }
            if(left != null){
                left.send("Hello cell to my left :)");
                System.out.println("left read: " + left.read());
            }
        }
    }
}

class hotMeshCommunicator {
    hotMeshCommunicator(int port, String host) {
        this.port = port;
        this.host = host;
        start(port, host);
    }
    int port;
    String host;
    LinkedBlockingQueue<String> queue;
    Server srv;
    Client cli;

    private void start(int port, String host){
        queue = new LinkedBlockingQueue<>();

        srv = new Server(queue, port);
        Thread srv_thread = new Thread(srv);
        srv_thread.start();

        cli = new Client(host, port);
        while (cli.connection == null) {
            cli.connect();
        }
    }

    public void send(String message){
        cli.send(message);
    }

    public String read() {
        try {
            return cli.read();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new String();
        // try {
        //     return queue.take();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // return new String();
    }

    public void close() {
        cli.send("exit"); // close server
        cli.close(); // close client
    }
}

class Client {
    String host;
    int port;
    Socket connection;
    PrintWriter out;
    BufferedReader in;

    Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try {
            connection = new Socket(host, port);
            out = new PrintWriter(connection.getOutputStream());
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String read() throws IOException {
        String inputLine;
        System.out.println("reading");
        while (true) {
            inputLine = in.readLine();
            if (inputLine != null) {
                System.out.println("inside read: " + inputLine);
                return inputLine;
            }
            System.out.println("null");
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
        System.out.println("writing: " + data);
        out.println(data);
        out.flush();
    }
}
