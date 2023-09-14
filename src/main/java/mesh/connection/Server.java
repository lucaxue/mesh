package mesh.src.main.java.mesh.connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {

    private LinkedBlockingQueue<String> queue;
    private int port;

    public Server(LinkedBlockingQueue<String> queue, int port) {
        this.queue = queue;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
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
