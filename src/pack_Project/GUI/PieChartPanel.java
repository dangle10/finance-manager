
package pack_Project.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class PieChartPanel extends JPanel {
    private String[] names;
    private double[] values;
    private Color[] colors;


    private static final Color[] DEFAULT_COLORS = {
            new Color(255, 99, 132),
            new Color(54, 162, 235),
            new Color(255, 206, 86),
            new Color(75, 192, 192),
            new Color(153, 102, 255),
            new Color(255, 159, 64),
            new Color(199, 199, 199),
            new Color(83, 102, 255),
            new Color(10, 200, 100),
            new Color(250, 50, 150)
    };

    public PieChartPanel() {
        setBackground(Color.WHITE);
        this.names = new String[0];
        this.values = new double[0];
        this.colors = DEFAULT_COLORS;
    }

    public void setChartData(String[] names, double[] values) {
        this.names = names;
        this.values = values;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int minDim = Math.min(width, height);
        int chartSize = (int) (minDim * 0.7);
        int x = (width - chartSize) / 2;
        int y = (height - chartSize) / 2 - 20;

        double total = Arrays.stream(values).sum();

        FontMetrics fm = g2d.getFontMetrics();

        if (names.length == 0 || total == 0) {
            g2d.setColor(Color.DARK_GRAY);
            String noDataMsg = "No Expense Data Available";
            int stringWidth = fm.stringWidth(noDataMsg);
            g2d.drawString(noDataMsg, (width - stringWidth) / 2, height / 2);
            return;
        }

        int startAngle = 0;
        int colorIndex = 0;

        for (int i = 0; i < values.length; i++) {
            int arcAngle = (int) Math.round((values[i] / total) * 360);
            g2d.setColor(colors[colorIndex % colors.length]);
            g2d.fillArc(x, y, chartSize, chartSize, startAngle, arcAngle);


            g2d.setColor(Color.WHITE);
            g2d.drawArc(x, y, chartSize, chartSize, startAngle, arcAngle);



            double midAngle = Math.toRadians(startAngle + arcAngle / 2);
            int labelX = (int) (x + chartSize / 2 + (chartSize / 2 * 0.7) * Math.cos(midAngle));
            int labelY = (int) (y + chartSize / 2 + (chartSize / 2 * 0.7) * Math.sin(midAngle));

            g2d.setColor(Color.BLACK);
            String percentage = String.format("%.1f%%", (values[i] / total) * 100);

            int textWidth = fm.stringWidth(percentage);
            g2d.drawString(percentage, labelX - textWidth / 2, labelY + fm.getAscent() / 2);

            startAngle += arcAngle;
            colorIndex++;
        }


        int legendX = x + chartSize + 20;
        int legendY = y;
        int legendBoxSize = 15;
        int legendSpacing = 5;

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        colorIndex = 0;
        for (int i = 0; i < names.length; i++) {
            g2d.setColor(colors[colorIndex % colors.length]);
            g2d.fillRect(legendX, legendY, legendBoxSize, legendBoxSize);

            g2d.setColor(Color.DARK_GRAY);
            String legendText = String.format("%s (%,.0f VND)", names[i], values[i]);

            g2d.drawString(legendText, legendX + legendBoxSize + legendSpacing, legendY + fm.getAscent());
            legendY += legendBoxSize + legendSpacing + 5;
            colorIndex++;
        }
    }
}
