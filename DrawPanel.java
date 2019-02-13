import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

public class DrawPanel extends JComponent {
    private PointCollector drawListener;
    private Point lastPoint;
    private Integer minX;
    private Integer minY;
    private Integer maxX;
    private Integer maxY;
    private List<Point> currentDrawing;

    public DrawPanel(Dimension dimension){
        currentDrawing=new LinkedList<>();
        this.setPreferredSize(dimension);
        this.setBackground(Color.DARK_GRAY);
        this.setVisible(true);

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (lastPoint!=null) {
                    notifyDrawListener(currentDrawing, minX, minY, maxX, maxY);
                }
                minX=null;
                minY=null;
                maxX=null;
                maxY=null;
                lastPoint=null;
                currentDrawing=new LinkedList<>();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawLine(e.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

    }



    public void drawLine(Point point){
        if (maxX==null){
            maxX=point.x;
        }
        if (maxY==null){
            maxY=point.y;
        }
        if (minX==null){
            minX=point.x;
        }
        if (minY==null){
            minY=point.y;
        }
        if (maxX<point.x){
            maxX=point.x;
        }
        if (maxY<point.y){
            maxY=point.y;
        }
        if (minX>point.x){
            minX=point.x;
        }
        if (minY>point.y){
            minY=point.y;
        }
        Graphics g=getGraphics();
        g.setColor(Color.WHITE);
        if (lastPoint==null){
            g.drawLine(point.x,point.y,point.x,point.y);
        }
        else{
            g.drawLine(lastPoint.x,lastPoint.y,point.x,point.y);
        }
        lastPoint=point;
        currentDrawing.add(point);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Dimension componentDimension=new Dimension();
        this.getSize(componentDimension);
        //COLOR COMPONENT
        Rectangle background=new Rectangle(componentDimension.width,componentDimension.height);
        g.drawRect(0,0,componentDimension.width,componentDimension.height);
        ((Graphics2D) g).setStroke(new BasicStroke(0));
        g.setColor(this.getBackground());
        ((Graphics2D) g).fill(background);
        g.setColor(Color.WHITE);
    }

    public void setDrawListener(PointCollector pointCollector){
        drawListener=pointCollector;
    }
    public void notifyDrawListener(List<Point> drawing,int minX,int minY,int maxX,int maxY){
        drawListener.update(this,drawing,minX,minY,maxX,maxY);
    }
    /*
    public void finishedDrawing(){
        drawListener.nextClass();
        clearDrawing();

    }
    public void clearDrawing(){
        repaint();
    }
    public void removeLastDrawing(){
        drawListener.deleteLastDrawing();
    }
    public void createDataset(){
        drawListener.createDataset();
    }

    public void trainNetwork(List<Integer> layerSizes,String datasetPath,int batchSize){
        drawListener.trainNetwork(layerSizes,datasetPath,batchSize);
    }
    */
}
