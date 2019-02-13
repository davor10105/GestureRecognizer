import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Neuron {
    private List<Neuron> neuronsBefore;
    public List<Neuron> neuronsAfter;
    public List<Double> weights;
    public double bias;
    public double output;

    private static long RANDOM_SEED=69;

    public Neuron(List<Neuron> neuronsBefore,List<Neuron> neuronsAfter){
        this.neuronsBefore=neuronsBefore;
        this.neuronsAfter=neuronsAfter;

        if(neuronsBefore==null) {
            weights = new LinkedList<>();
            weights.add(1.0);
            bias=0.0;
        }
        else{
            weights=new LinkedList<>();

            Random random=new Random();
            random.setSeed(RANDOM_SEED);
            for(int i=0;i<neuronsBefore.size();i++){
                weights.add((random.nextDouble()-0.5)*(2.4/(neuronsBefore.size()+1)));
            }
            bias=(random.nextDouble()-0.5)*(2.4/(neuronsBefore.size()+1));
        }

    }

    public double output(Neuron neuronTo) {
        if (this.neuronsBefore==null){
            return output;
        }
        output=0;
        for(int i=0;i<neuronsBefore.size();i++){
            output+=weights.get(i)*neuronsBefore.get(i).output(this);
        }
        output=ActivationFunction.sigmoid(output+bias);
        return output;

    }

    public void setNeuronsAfter(List<Neuron> neuronsAfter){
        this.neuronsAfter=neuronsAfter;


    }
}
