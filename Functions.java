import java.util.LinkedList;
import java.util.List;

public class Functions {
    public static List<Double> softmax(List<Double> ys){
        List<Double> returnValues=new LinkedList<>();
        for(Double yk:ys){
            double suma=0;
            for(Double yj:ys){
                suma+=Math.exp(yj);
            }
            returnValues.add(Math.exp(yk)/suma);
        }
        return returnValues;
    }

    public static List<Double> mySoftmax(List<Double> ys){
        List<Double> returnValues=new LinkedList<>();
        double suma=0;
        for(Double yj:ys){
            suma+=yj;
        }
        for(Double yk:ys){
            returnValues.add(yk/suma);
        }
        return returnValues;
    }
}
