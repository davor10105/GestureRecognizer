import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

public class Test {
    public static void main(String[] args) {
        NeuralNetwork neuralNetwork=new NeuralNetwork(new LinkedList<>(Arrays.asList(20,20,3)));

        neuralNetwork.stochasticGradientDescent(Dataset.readFromFile("./testni3_dataset.txt"), 0.1);
        System.out.println("KITA");
        System.out.println(neuralNetwork.forwardPass(Arrays.asList(0.47368421052631576,0.0,0.5087719298245614,0.2372993079696351,0.5087719298245614,0.4860070640072155,0.44011792238646524,0.7147339521388498,0.2550253381808401,0.8677816793630195,0.020944539178697798,0.861349462571805,0.0858794508747045,0.6842105263157895,0.31808597386044324,0.7391386054393906,0.5093471771501252,0.8949285912137844,0.7192982456140351,1.0)));
        System.out.println(neuralNetwork.forwardPass(Arrays.asList(0.21212121212121213,1.0,0.14248574493145208,0.7578832701118059,0.07024006705661216,0.5027154867982123,0.015151515151515152,0.24864658830377967,0.02593056097193112,0.0021862346655495916,0.13636363636363635,0.17556421351546506,0.30526368635593953,0.2727272727272727,0.5358187381967866,0.35124270243172667,0.5554274901910193,0.5912932870026993,0.3939393939393939,0.7424242424242424)));

    }
}