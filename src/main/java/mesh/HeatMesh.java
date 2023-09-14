package mesh.src.main.java.mesh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

class Server implements Runnable {

    LinkedBlockingQueue<String> queue;

    Server(LinkedBlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(9002));
            serverSocketChannel.configureBlocking(false);

            Boolean should_exit = false;

            while (true) {
                if (should_exit) {
                    break;
                }

                SocketChannel socketChannel = serverSocketChannel.accept();

                if (socketChannel != null) {
                    Socket socket = socketChannel.socket();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        queue.put(inputLine);
                        should_exit = inputLine.equals("exit");
                        if (should_exit) {
                            break;
                        }
                    }
                    in.close();
                    socket.close();
                    socketChannel.close();
                }
            }
            serverSocketChannel.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}

class Client {
    Socket connection;
    PrintWriter out;

    public void connect() {
        try {
            connection = new Socket("137.205.113.36", 9002);
            out = new PrintWriter(connection.getOutputStream());
        } catch (Exception e) {}
    }

    public void close() {
        try {
            connection.close();
            out.close();
        } catch (Exception e) {}
    }

    public void send(String data){
        out.println(data);
        out.flush();
    }
}

class HeatMesh {

    public static void main(String[] args) throws InterruptedException {

        // Create the linkedblockingqueue
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

        // Create and start the server
        Server srv = new Server(queue);
        Thread srv_thread = new Thread(srv);
        srv_thread.start();

        // Create and connect the client
        Client cli = new Client();
        while (cli.connection == null) {
            cli.connect();
        }

        while (true) {
            cli.send("testing data send");
            Thread.sleep(1000);
            System.out.println(queue.take());
        }
    }
}
