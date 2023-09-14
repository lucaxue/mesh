package mesh.src.main.java.mesh.gui;

import mesh.src.main.java.mesh.HeatMap;
import javax.swing.*;
import java.awt.*;

class HeatMapFrame extends JFrame
{
    HeatMap panel;

    public HeatMapFrame() throws Exception
    {

        super("Heat Map Frame");
        double[][] data = HeatMap.generateSinCosData(100);
        boolean useGraphicsYAxis = true;

        // you can use a pre-defined gradient:

        panel = new HeatMap(data, useGraphicsYAxis, Gradient.GRADIENT_BLUE_TO_RED);

        // or you can also make a custom gradient:

        Color[] gradientColors = new Color[]{Color.blue,Color.green,Color.yellow};
        Color[] customGradient = Gradient.createMultiGradient(gradientColors, 500);
        panel.updateGradient(customGradient);

        // set miscellaneous settings

        panel.setDrawLegend(true);

        panel.setTitle("Height (m)");
        panel.setDrawTitle(true);

        panel.setXAxisTitle("X-Distance (m)");
        panel.setDrawXAxisTitle(true);

        panel.setYAxisTitle("Y-Distance (m)");
        panel.setDrawYAxisTitle(true);

        panel.setCoordinateBounds(0, 6.28, 0, 6.28);

        panel.setDrawXTicks(true);
        panel.setDrawYTicks(true);

        this.getContentPane().add(panel);
    }

    public HeatMapFrame(double[][] dataDisplay) throws Exception
    {

        super("Heat Map Frame");
        double[][] data = dataDisplay;
        boolean useGraphicsYAxis = true;

        // you can use a pre-defined gradient:

        panel = new HeatMap(data, useGraphicsYAxis, Gradient.GRADIENT_GREEN_YELLOW_ORANGE_RED);

        // set miscellaneous settings

        panel.setDrawLegend(true);

        panel.setTitle("Temperature");
        panel.setDrawTitle(true);

        panel.setXAxisTitle("X");
        panel.setDrawXAxisTitle(true);

        panel.setYAxisTitle("Y");
        panel.setDrawYAxisTitle(true);

        panel.setCoordinateBounds(0, 1000, 1000, 0);

        panel.setDrawXTicks(true);
        panel.setDrawYTicks(true);

        this.getContentPane().add(panel);
    }

    // this function will be run from the EDT

    private static void createAndShowGUI() throws Exception
    {

        HeatMapFrame hmf = new HeatMapFrame();
        hmf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hmf.setSize(500,500);
        hmf.setVisible(true);

    }

    public void createGUIForArray() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,500);
        this.setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    createAndShowGUI();
                }
                catch (Exception e)
                {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        });
    }
}
