import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GUI extends JFrame{
    public GUI(){

        this.setLayout(new BorderLayout());

        DrawPanel trainDrawPanel=new DrawPanel(new Dimension(600,600));
        DrawPanel testDrawPanel=new DrawPanel(new Dimension(600,600));

        JTabbedPane tabbedPane=new JTabbedPane();

        PointCollector pointCollector=new PointCollector(trainDrawPanel,testDrawPanel);

        JPanel trainPanel=new TrainPanel(trainDrawPanel,pointCollector);
        JPanel testPanel=new TestPanel(testDrawPanel,pointCollector);

        tabbedPane.addTab("TRAIN",trainPanel);
        tabbedPane.addTab("TEST",testPanel);


        this.add(tabbedPane);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

}
