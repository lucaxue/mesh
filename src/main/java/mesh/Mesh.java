package mesh.src.main.java.mesh;

import java.lang.Math;

public class Mesh {

    private double[][] mesh;
    private int width;
    private int height;
    private int maxValue;

    public Mesh(int width, int height, int maxValue) {
        this.mesh = new double[width][height];
        this.width = width;
        this.height = height;
        this.maxValue = maxValue;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.mesh[x][y] = 0;
            }
        }

    }

    public Mesh(Mesh mesh) {
        this.mesh = mesh.getMeshArray();
        this.width = mesh.getWidth();
        this.height = mesh.getHeight();
        this.maxValue = mesh.getMaxValue();
    }

    public double getPoint(int x, int y) {
        return this.mesh[x][y];
    }

    public void setPoint(int x, int y, double value) {
        this.mesh[x][y] = value;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public double[][] getMeshArray() {
        return this.mesh;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public void incrPoint(int x, int y, double value) {
        // if (x >= 0 && y >= 0 && x < width && y < height) {
        //     this.mesh[x][y] = Math.min(this.mesh[x][y]+value, 1);
        // }
        this.mesh[x][y] = Math.min(this.mesh[x][y]+value, 1);
    }

}
