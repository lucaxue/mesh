package mesh.src.main.java.mesh;

import java.util.concurrent.LinkedBlockingQueue;

import java.io.*;
import java.util.concurrent.*;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;
// import java.String.split;

public class Main {

    public static void main (String[] args) {

        try {

            int width = 500;
            int height = 1000;
            int timesteps = 2000;
            int maxValue = 1;

            int[] meshLocation = {Integer.parseInt(args[0]), Integer.parseInt(args[1])};
            int[] meshBounds = {Integer.parseInt(args[2]), Integer.parseInt(args[3])};

            String IPLeft = args[4].split("\\:")[0];
            String IPRight = args[5].split("\\:")[0];
            String IPAbove = args[6].split("\\:")[0];
            String IPBelow = args[7].split("\\:")[0];

            int portLeft = Integer.parseint(args[4].split("\\:")[1]);
            int portRight = Integer.parseint(args[5].split("\\:")[1]);
            int portAbove = Integer.parseint(args[6].split("\\:")[1]);
            int portBelow = Integer.parseint(args[7].split("\\:")[1]);

            Mesh mesh = new Mesh(width, height, maxValue);

            // Create the linkedblockingqueue
            LinkedBlockingQueue<String> queueLeft = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<String> queueRight = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<String> queueAbove = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<String> queueBelow = new LinkedBlockingQueue<>();

            Client clientLeft = new Client(IPLeft, portLeft);
            Client clientRight = new Client(IPRight, portRight);
            Client clientAbove = new Client(IPAbove, portAbove);
            Client clientBelow = new Client(IPBelow, portBelow);

            if (meshLocation[0] > 0) {
                // MACHINE TO LEFT

                // Create and start the server
                Server serverLeft = new Server(queueLeft, portLeft);
                Thread srv_thread = new Thread(serverLeft);
                srv_thread.start();
                // Create and connect the client
                clientLeft = new Client(IPLeft, portLeft);
                while (clientLeft.connection == null) {
                    clientLeft.connect();
                }

            }
            if (meshLocation[0] < meshBounds[0]) {
                // MACHINE TO RIGHT

                // Create and start the server
                Server serverRight = new Server(queueRight, portRight);
                Thread srv_thread = new Thread(serverRight);
                srv_thread.start();
                // Create and connect the client
                clientRight = new Client(IPRight, portRight);
                while (clientRight.connection == null) {
                    clientRight.connect();
                }

            }
            if (meshLocation[1] > 0) {
                // MACHINE ABOVE

                // Create and start the server
                Server serverAbove = new Server(queueAbove, portAbove);
                Thread srv_thread = new Thread(serverAbove);
                srv_thread.start();
                // Create and connect the client
                clientAbove = new Client(IPAbove, portAbove);
                while (clientAbove.connection == null) {
                    clientAbove.connect();
                }
            }
            if (meshLocation[1] < meshBounds[1]) {
                // MACHINE BELOW

                // Create and start the server
                Server serverBelow = new Server(queueBelow, portBelow);
                Thread srv_thread = new Thread(serverBelow);
                srv_thread.start();
                // Create and connect the client
                clientBelow = new Client(IPBelow, portBelow);
                while (clientBelow.connection == null) {
                    clientBelow.connect();
                }

            }

            mesh.setPoint(200, 200, 1);

            // Synchronously
            long startTime = System.currentTimeMillis();

            for (int ts = 0; ts < timesteps; ts++) {

                Mesh stagingMesh = new Mesh(mesh);

                for (int x = 0; x < mesh.getWidth(); x++) {
                    for (int y = 0; y < mesh.getHeight(); y++) {
                        double incrValue = mesh.getPoint(x, y);
                        if (incrValue != 0) {
                            incrValue *= 0.05;
                            if (y > 0) {
                                stagingMesh.incrPoint(x, y-1, incrValue);
                            } else if (meshLocation[1] > 0) {
                                // ABOVE MESH
                                clientAbove.send(x+"|"+incrValue);
                            }
                            if (y < height-1) {
                                stagingMesh.incrPoint(x, y+1, incrValue);
                            } else if (meshLocation[1] < meshBounds[1]) {
                                // BELOW MESH
                                clientBelow.send(x+"|"+incrValue);
                            }
                            if (x > 0) {
                                stagingMesh.incrPoint(x-1, y, incrValue);
                            } else if (meshLocation[0] > 0) {
                                // LEFT OF MESH
                                clientLeft.send(y+"|"+incrValue);
                            }
                            if (x < width-1) {
                                stagingMesh.incrPoint(x+1, y, incrValue);
                            } else if (meshLocation[0] < meshBounds[0]) {
                                // RIGHT OF MESH
                                clientRight.send(y+"|"+incrValue);
                            }
                        }
                    }
                }
                while (queueLeft.size() > 0) {
                    String[] queueitem = queueLeft.take().split("\\|");
                    stagingMesh.incrPoint(width-1, Integer.parseInt(queueitem[0]), Double.parseDouble(queueitem[1]));
                }
                while (queueRight.size() > 0) {
                    String[] queueitem = queueRight.take().split("\\|");
                    stagingMesh.incrPoint(0, Integer.parseInt(queueitem[0]), Double.parseDouble(queueitem[1]));
                }
                while (queueAbove.size() > 0) {
                    String[] queueitem = queueAbove.take().split("\\|");
                    stagingMesh.incrPoint(Integer.parseInt(queueitem[0]), 0, Double.parseDouble(queueitem[1]));
                }
                while (queueBelow.size() > 0) {
                    String[] queueitem = queueBelow.take().split("\\|");
                    stagingMesh.incrPoint(Integer.parseInt(queueitem[0]), height-1, Double.parseDouble(queueitem[1]));
                }
                mesh = new Mesh(stagingMesh);
            }

            try {
                clientLeft.close();
                clientRight.close();
                clientAbove.close();
                clientBelow.close();
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Time: " + (endTime - startTime) + "ms");

            // FileWriter outFile = new FileWriter("heatMap.txt");
            // double[][] heatMapArray = mesh.getMeshArray();
            // for (int i = 0; i < width; i++) {
            //     for (int j = 0; j < height; j++) {
            //         heatMapArray[i][j] *= 1;
            //         outFile.write("["+i+"]["+j+"] : "+heatMapArray[i][j]);
            //     }
            // }
            // outFile.close();
            // HeatMapFrame heatMap = new HeatMapFrame(heatMapArray);
            // heatMap.createGUIForArray();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
