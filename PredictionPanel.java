import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PredictionPanel extends JPanel {
    private List<Double> currentPrediction;

    public PredictionPanel(Dimension parentDimension){
        this.setPreferredSize(new Dimension(parentDimension.width,100));
        this.setVisible(true);

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (currentPrediction!=null) {
            g.setColor(Color.ORANGE);
            for(int i=0;i<currentPrediction.size();i++){
                g.fillRect(i*this.getWidth()/currentPrediction.size(),(int)Math.round((1-currentPrediction.get(i))*this.getHeight()),(int)Math.round(this.getWidth()/(double)currentPrediction.size()),(int)Math.round(this.getHeight()*currentPrediction.get(i)));
            }
        }
    }
    public void draw(List<Double> prediction){
        currentPrediction=prediction;
        repaint();
    }
}
