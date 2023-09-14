package mesh.src.main.java.mesh;

import java.io.*;
import java.util.concurrent.*;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main (String[] args) {

        try {

            int width = 1000;
            int height = 1000;
            int timesteps = 2000;
            int maxValue = 1;

            Mesh mesh = new Mesh(width, height, maxValue);
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
                            stagingMesh.incrPoint(x+1, y, incrValue);
                            stagingMesh.incrPoint(x-1, y, incrValue);
                            stagingMesh.incrPoint(x, y+1, incrValue);
                            stagingMesh.incrPoint(x, y-1, incrValue);
                        }
                    }
                }
                mesh = new Mesh(stagingMesh);
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
