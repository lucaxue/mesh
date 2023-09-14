package mesh.src.main.java.mesh;

import mesh.src.main.java.mesh.gui.*;
import mesh.src.main.java.mesh.connection.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;
import java.util.concurrent.*;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main (String[] args) {

        try {

            int width = 250;
            int height = 1000;
            int timesteps = 2000;
            int maxValue = 1;

            int[] meshLocation = {Integer.parseInt(args[0]), Integer.parseInt(args[1])};
            int[] meshBounds = {Integer.parseInt(args[2]), Integer.parseInt(args[3])};

            String IPLeft = args[4].split("\\:")[0];
            String IPRight = args[5].split("\\:")[0];
            // String IPAbove = args[6].split("\\:")[0];
            // String IPBelow = args[7].split("\\:")[0];

            int portLeft = Integer.parseInt(args[4].split("\\:")[1]);
            int portRight = Integer.parseInt(args[5].split("\\:")[1]);
            // int portAbove = Integer.parseInt(args[6].split("\\:")[1]);
            // int portBelow = Integer.parseInt(args[7].split("\\:")[1]);

            Mesh mesh = new Mesh(width, height, maxValue);

            // Create the linkedblockingqueue
            LinkedBlockingQueue<String> queueLeft = new LinkedBlockingQueue<>();
            LinkedBlockingQueue<String> queueRight = new LinkedBlockingQueue<>();
            // LinkedBlockingQueue<String> queueAbove = new LinkedBlockingQueue<>();
            // LinkedBlockingQueue<String> queueBelow = new LinkedBlockingQueue<>();

            Client clientLeft = new Client(IPLeft, portLeft);
            Client clientRight = new Client(IPRight, portRight);
            // Client clientAbove = new Client(IPAbove, portAbove);
            // Client clientBelow = new Client(IPBelow, portBelow);

            // MACHINE TO LEFT
            if (meshLocation[0] > 0) {
                Server serverLeft = new Server(queueLeft, portLeft);
                new Thread(serverLeft).start();
                while (clientLeft.getConnection() == null) {
                    clientLeft.connect();
                }
            }
            // MACHINE TO RIGHT
            if (meshLocation[0] < meshBounds[0]) {
                Server serverRight = new Server(queueRight, portRight);
                new Thread(serverRight).start();
                while (clientRight.getConnection() == null) {
                    clientRight.connect();
                }
            }
            // // MACHINE ABOVE
            // if (meshLocation[1] > 0) {
            //     Server serverAbove = new Server(queueAbove, portAbove);
            //     new Thread(serverAbove).start();
            //     clientAbove.connect();
            // }
            // // MACHINE BELOW
            // if (meshLocation[1] < meshBounds[1]) {
            //     Server serverBelow = new Server(queueBelow, portBelow);
            //     new Thread(serverBelow).start();
            //     clientBelow.connect();
            // }

            // mesh.setPoint(200, 200, 1);
            if (args[8].equals("true")) {
                mesh.setPoint(Integer.parseInt(args[9]), Integer.parseInt(args[10]), Integer.parseInt(args[11]));
            }

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
                            }
                            //else if (meshLocation[1] > 0) {
                                // ABOVE MESH
                                //clientAbove.send(x+"|"+incrValue);
                            //}
                            if (y < height-1) {
                                stagingMesh.incrPoint(x, y+1, incrValue);
                            }
                            //else if (meshLocation[1] < meshBounds[1]) {
                                // BELOW MESH
                                //clientBelow.send(x+"|"+incrValue);
                            //}
                            if (x > 0) {
                                stagingMesh.incrPoint(x-1, y, incrValue);
                            } else if (meshLocation[0] > 0) {
                                // LEFT OF MESH
                                clientLeft.send(y+"|"+incrValue);
                                // System.out.println("Send:"+y+"|"+incrValue);
                            }
                            if (x < width-1) {
                                stagingMesh.incrPoint(x+1, y, incrValue);
                            } else if (meshLocation[0] < meshBounds[0]) {
                                // RIGHT OF MESH
                                clientRight.send(y+"|"+incrValue);
                                // System.out.println("Send:"+y+"|"+incrValue);
                            }
                        }
                    }
                }

                String queuetake;
                if (meshLocation[0] > 0) {
                    clientLeft.send("exit"+ts);
                    do {
                        queuetake = queueLeft.take();
                        if (!queuetake.equals("exit"+ts)) {
                            String[] queueitem = queuetake.split("\\|");
                            stagingMesh.incrPoint(0, Integer.parseInt(queueitem[0]), Double.parseDouble(queueitem[1]));
                        }
                    } while (!queuetake.equals("exit"+ts));
                }
                if (meshLocation[0] < meshBounds[0]) {
                    clientRight.send("exit"+ts);
                    do {
                        queuetake = queueRight.take();
                        if (!queuetake.equals("exit"+ts)) {
                            String[] queueitem = queuetake.split("\\|");
                            stagingMesh.incrPoint(width-1, Integer.parseInt(queueitem[0]), Double.parseDouble(queueitem[1]));
                        }
                    } while (!queuetake.equals("exit"+ts));
                }

                // while (queueAbove.size() > 0) {
                //     String[] queueItem = queueAbove.take().split("\\|");
                //     stagingMesh.incrPoint(Integer.parseInt(queueItem[0]), 0, Double.parseDouble(queueItem[1]));
                // }
                // while (queueBelow.size() > 0) {
                //     String[] queueItem = queueBelow.take().split("\\|");
                //     stagingMesh.incrPoint(Integer.parseInt(queueItem[0]), height-1, Double.parseDouble(queueItem[1]));
                // }

                mesh = new Mesh(stagingMesh);
            }

            // try {
            //     clientLeft.send("exit");
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }
            // try {
            //     clientRight.send("exit");
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }

            if (meshLocation[0] > 0) {
                clientLeft.close();
            }
            if (meshLocation[0] < meshBounds[0]) {
                clientRight.close();
            }
            // clientAbove.close();
            // clientBelow.close();
            // try {
            //     serverLeft.close();
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }
            // try {
            //     serverRight.close();
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }

            long endTime = System.currentTimeMillis();
            System.out.println("Time: " + (endTime - startTime) + "ms");

            FileWriter outFile = new FileWriter("heatMap"+args[0]+"-"+args[1]+".txt");
            double[][] heatMapArray = mesh.getMeshArray();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    heatMapArray[i][j] *= 1;
                    outFile.write("["+i+"]["+j+"] : "+heatMapArray[i][j]);
                }
            }
            outFile.close();
            HeatMapFrame heatMap = new HeatMapFrame(heatMapArray);
            heatMap.createGUIForArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
