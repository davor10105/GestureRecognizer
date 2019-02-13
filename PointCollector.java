import javax.sound.sampled.Line;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

public class PointCollector {
    private List<List<List<List<Double>>>> dataset;
    private List<List<List<Double>>> currentClassDrawings;
    private List<List<Double>> currentTestDrawing;
    private DrawPanel drawPanel;
    private final int GESTURE_SIZE=10;
    private NeuralNetwork neuralNetwork;

    private DrawPanel trainDrawPanel;
    private DrawPanel testDrawPanel;

    private TestPanel predictionListener;

    public PointCollector(DrawPanel trainDrawPanel,DrawPanel testDrawPanel) {
        this.trainDrawPanel=trainDrawPanel;
        trainDrawPanel.setDrawListener(this);
        this.testDrawPanel=testDrawPanel;
        testDrawPanel.setDrawListener(this);

        dataset=new LinkedList<>();
        currentClassDrawings=new LinkedList<>();
        currentTestDrawing=new LinkedList<>();
    }

    public void update(DrawPanel drawPanel,List<Point> drawing,int minX,int minY,int maxX,int maxY){
        List<List<Double>> scaledDrawing=new LinkedList<>();
        double length=0;
        for(int i=0;i<drawing.size();i++){
            Point p=drawing.get(i);
            List<Double> newPoint=new LinkedList<>();
            double maxmax;
            if ((maxX-minX)>maxY-minY){
                maxmax=maxX-minX;
            }
            else{
                maxmax=maxY-minY;
            }
            newPoint.add((p.x-minX)/maxmax);
            newPoint.add((p.y-minY)/maxmax);
            scaledDrawing.add(newPoint);

            if (i>0){
                length+=PointCollector.distanceBetweenPoints(scaledDrawing.get(i-1),scaledDrawing.get(i));
            }
        }

        //odaberi samo 10 tocaka
        List<List<Double>> discreteScaledDrawing=new LinkedList<>();
        double step=length/(GESTURE_SIZE-1);
        double currentLength=0;
        int k=0;
        for (int i=0;i<drawing.size();i++){
            if (i>0){
                currentLength+=PointCollector.distanceBetweenPoints(scaledDrawing.get(i-1),scaledDrawing.get(i));
            }

            while(k*step<=currentLength) {
                if (currentLength == k * step) {
                    discreteScaledDrawing.add(scaledDrawing.get(i));
                    ++k;
                } else if (currentLength > k * step) {
                    List<Double> point1 = scaledDrawing.get(i - 1);
                    List<Double> point2 = scaledDrawing.get(i);
                    double t = (k * step - (currentLength - PointCollector.distanceBetweenPoints(point1, point2))) / PointCollector.distanceBetweenPoints(point1, point2);
                    List<Double> newPoint = new LinkedList<>();
                    newPoint.add((point2.get(0) - point1.get(0)) * t + point1.get(0));
                    newPoint.add((point2.get(1) - point1.get(1)) * t + point1.get(1));

                    discreteScaledDrawing.add(newPoint);
                    ++k;
                }
            }
        }
        if (discreteScaledDrawing.size()<10){
            discreteScaledDrawing.add(scaledDrawing.get(scaledDrawing.size()-1));
        }

        if (drawPanel.equals(trainDrawPanel)) {
            currentClassDrawings.add(discreteScaledDrawing);
            System.out.println("Broj primjera u klasi: "+currentClassDrawings.size());
        }
        else{
            currentTestDrawing=discreteScaledDrawing;
            if (neuralNetwork!=null) {
                List<Double> prediction = predict();
                notifyPredictionListener(prediction);
            }
        }
    }

    public void nextClass(){
        dataset.add(currentClassDrawings);
        System.out.println("Broj klasa: "+dataset.size());
        currentClassDrawings=new LinkedList<>();

        SwingUtilities.invokeLater(()->{
            trainDrawPanel.repaint();
        });

    }

    public void deleteLastDrawing(){

        SwingUtilities.invokeLater(()->{
            trainDrawPanel.repaint();
        });
        currentClassDrawings.remove(currentClassDrawings.size()-1);
    }

    public boolean hasDrawings(){
        if (currentClassDrawings.size()>0){
            return true;
        }
        return false;
    }

    public void createDataset(String datasetPath){
        System.out.println("Spremanje dataseta u datoteku...");
        Dataset.writeDrawingsToFile(dataset,datasetPath);
        System.out.println("Dataset spremljen u datoteku");
    }

    public static double distanceBetweenPoints(List<Double> point1,List<Double> point2){
        double dx=point2.get(0)-point1.get(0);
        double dy=point2.get(1)-point1.get(1);

        return Math.sqrt((dx*dx)+(dy*dy));
    }

    public NeuralNetwork createNetwork(List<Integer> hiddenLayerSizes,List<List<List<Double>>> dataset){

        hiddenLayerSizes.add(0,dataset.get(0).get(0).size());
        hiddenLayerSizes.add(dataset.get(0).get(1).size());
        return new NeuralNetwork(hiddenLayerSizes);
    }

    public void stochasticGradientDescent(List<Integer> hiddenLayerSizes,String datasetPath,double learningRate){
        List<List<List<Double>>> dataset=Dataset.readFromFile(datasetPath);
        this.neuralNetwork=createNetwork(hiddenLayerSizes,dataset);

        this.neuralNetwork.stochasticGradientDescent(dataset,learningRate);
    }
    public void batchGradientDescent(List<Integer> hiddenLayerSizes,String datasetPath,double learningRate){
        List<List<List<Double>>> dataset=Dataset.readFromFile(datasetPath);
        this.neuralNetwork=createNetwork(hiddenLayerSizes,dataset);

        this.neuralNetwork.batchGradientDescent(dataset,learningRate);
    }
    public void miniBatchGradientDescent(List<Integer> hiddenLayerSizes,String datasetPath,double learningRate){
        List<List<List<Double>>> dataset=Dataset.readFromFile(datasetPath);
        this.neuralNetwork=createNetwork(hiddenLayerSizes,dataset);

        this.neuralNetwork.miniBatchGradientDescent(dataset,learningRate);
    }

    public List<Double> predict(){
        List<Double> xList=new LinkedList<>();
        for(List<Double> point:currentTestDrawing){
            xList.add(point.get(0));
            xList.add(point.get(1));
        }
        return neuralNetwork.forwardPass(xList);
    }

    public void setPredictionListener(TestPanel testPanel){
        this.predictionListener=testPanel;
    }
    public void notifyPredictionListener(List<Double> prediction){
        SwingUtilities.invokeLater(()->{
            this.predictionListener.update(Functions.mySoftmax(prediction));
        });

    }

}
