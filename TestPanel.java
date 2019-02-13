import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TestPanel extends JPanel {
    DrawPanel testDrawPanel;
    PredictionPanel predictionPanel;

    public TestPanel(DrawPanel testDrawPanel,PointCollector pointCollector){
        pointCollector.setPredictionListener(this);

        this.testDrawPanel=testDrawPanel;

        this.setLayout(new BorderLayout());
        this.add(testDrawPanel,BorderLayout.CENTER);

        predictionPanel=new PredictionPanel(new Dimension(1200,this.getHeight()));
        this.add(predictionPanel,BorderLayout.SOUTH);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void update(List<Double> prediction){
        predictionPanel.draw(prediction);
    }
}
