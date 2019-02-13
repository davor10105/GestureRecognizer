import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;

public class NeuralNetwork {
    private static double EPSILON=0.001;
    public List<List<Neuron>> layers;

    public NeuralNetwork(List<Integer> layerSizes){
        layers= new LinkedList<>();
        List<Neuron> innerList=new LinkedList<Neuron>();
        List<Neuron> beforeList=null;
        for(int i=0;i<layerSizes.size();i++){
            for(int j=0;j<layerSizes.get(i);j++){
                innerList.add(new Neuron(beforeList,new LinkedList<>()));
            }
            layers.add(innerList);
            //ako nije prvi layer dodati afterlist
            if (i>0){
                for(Neuron n:layers.get(i-1)){
                    n.setNeuronsAfter(innerList);
                }
            }
            beforeList=new LinkedList<>(innerList);
            innerList=new LinkedList<>();
        }
    }

    public List<Double> forwardPass(List<Double> inputs){
        if (inputs.size()!=layers.get(0).size()){
            System.out.println(inputs.size()+" "+layers.get(0).size());
            System.out.println("ne valja "+inputs);
            throw new InvalidParameterException("Ulazni primjer nije iste dimenzije kao ulazni sloj mreze");
        }

        for(int i=0;i<layers.get(0).size();i++){
            layers.get(0).get(i).output=inputs.get(i);
        }

        List<Double> returnValues=new LinkedList<>();
        for(int i=0;i<layers.get(layers.size()-1).size();i++){
            returnValues.add(layers.get(layers.size()-1).get(i).output(null));
        }

        return returnValues;
    }

    public void train(List<List<List<Double>>> dataset, int batchSize,double learningRate,double epsilon){
        System.out.println(dataset.size());
        System.out.println(batchSize);
        int currentBatch=0;
        double lastError=Double.MAX_VALUE;
        int epoch=0;
        while(true) {
            ++epoch;

            //postavi sve delte na 0
            List<List<List<Double>>> deltaWeights=new LinkedList<>();
            List<List<Double>> deltaBiases=new LinkedList<>();
            for (int i=1;i<layers.size();i++){

                List<List<Double>> thisLayerDeltaWeights=new LinkedList<>();
                List<Double> thisLayerDeltaBiases=new LinkedList<>();
                for(int j=0;j<layers.get(i).size();j++){

                    thisLayerDeltaBiases.add(0.0);
                    List<Double> thisNeuronDeltaWeights=new LinkedList<>();
                    for(int k=0;k<layers.get(i).get(j).weights.size();k++){
                        thisNeuronDeltaWeights.add(0.0);
                    }
                    thisLayerDeltaWeights.add(thisNeuronDeltaWeights);
                }
                deltaWeights.add(thisLayerDeltaWeights);
                deltaBiases.add(thisLayerDeltaBiases);
            }
            for (int i = 0; i < dataset.size(); i++) {
                //System.out.println("primjer "+i);
                ++currentBatch;

                List<Double> xi = dataset.get(i).get(0);
                List<Double> yi = dataset.get(i).get(1);

                List<Double> hi = this.forwardPass(xi);

                List<List<Double>> currentDeltaList = new LinkedList<>();
                //za izlazni layer
                List<Double> delta = new LinkedList<>();
                for (int k = 0; k < yi.size(); k++) {
                    delta.add((yi.get(k) - hi.get(k)) * hi.get(k) * (1 - hi.get(k)));
                }
                currentDeltaList.add(delta);
                //za skriveni layer
                for (int j = layers.size() - 2; j >= 1; j--) {
                    delta = new LinkedList<>();
                    for (int k = 0; k < layers.get(j).size(); k++) {
                        List<Double> currentWeights = new LinkedList<>();
                        for(int l=0;l<layers.get(j+1).size();l++){
                            currentWeights.add(layers.get(j+1).get(l).weights.get(k));
                        }
                        //delta.add(0.0);
                        double newDelta=0.0;
                        for (int l = 0; l < currentWeights.size(); l++) {
                            //delta.set(k, delta.get(k) + currentWeights.get(l) * currentDeltaList.get(currentDeltaList.size() - 1).get(l));
                            newDelta+=currentWeights.get(l) * currentDeltaList.get(currentDeltaList.size() - 1).get(l);
                        }
                        Neuron currentNeuron=layers.get(j).get(k);
                        delta.add(newDelta * currentNeuron.output * (1 - currentNeuron.output));
                    }
                    currentDeltaList.add(delta);
                }
                for (int j = 1; j < layers.size(); j++) {
                    List<List<Double>> thisLayerDeltaWeights=deltaWeights.get(j-1);
                    List<Double> thisLayerDeltaBiases=deltaBiases.get(j-1);
                    for (int k = 0; k < layers.get(j).size(); k++) {
                        List<Double> thisNeuronDeltaWeigths=thisLayerDeltaWeights.get(k);
                        Double thisNeuronDeltaBias=thisLayerDeltaBiases.get(k);
                        Neuron currentNeuron = layers.get(j).get(k);
                        for (int l = 0; l < currentNeuron.weights.size(); l++) {
                            //currentNeuron.weights.set(l, currentNeuron.weights.get(l) + 1./batchSize*currentDeltaList.get(layers.size() - 1 - j).get(k) * layers.get(j-1).get(l).output * learningRate);
                            thisNeuronDeltaWeigths.set(l,thisNeuronDeltaWeigths.get(l)+currentDeltaList.get(layers.size() - 1 - j).get(k) * layers.get(j-1).get(l).output);
                        }
                        thisNeuronDeltaBias+=currentDeltaList.get(layers.size()-1-j).get(k);
                        //currentNeuron.bias+=1./batchSize*learningRate*currentDeltaList.get(layers.size()-1-j).get(k);

                        thisLayerDeltaWeights.set(k,thisNeuronDeltaWeigths);
                        thisLayerDeltaBiases.add(k,thisNeuronDeltaBias);
                    }
                    deltaWeights.set(j-1,thisLayerDeltaWeights);
                    deltaBiases.set(j-1,thisLayerDeltaBiases);
                }
                //UPDATE TEZINE I BIASEVE AKO JE TRENUTNI PRIMJER BATCHSIZE
                if (currentBatch%batchSize==0 || i==dataset.size()-1){

                    for (int j = 1; j < layers.size(); j++) {
                        List<List<Double>> thisLayerDeltaWeights=deltaWeights.get(j-1);
                        List<Double> thisLayerDeltaBiases=deltaBiases.get(j-1);
                        for (int k = 0; k < layers.get(j).size(); k++) {
                            Neuron currentNeuron = layers.get(j).get(k);
                            List<Double> thisNeuronDeltaWeights=thisLayerDeltaWeights.get(k);
                            for (int l = 0; l < currentNeuron.weights.size(); l++) {
                                currentNeuron.weights.set(l, currentNeuron.weights.get(l) + 1./currentBatch*deltaWeights.get(j-1).get(k).get(l)* learningRate);
                                //postavi deltaweights na 0.0 za sljedece iteracije
                                thisNeuronDeltaWeights.set(l,0.0);
                            }
                            currentNeuron.bias+=1./currentBatch*learningRate*deltaBiases.get(j-1).get(k);
                            thisLayerDeltaWeights.set(k,thisNeuronDeltaWeights);
                            thisLayerDeltaBiases.set(k,0.0);
                        }
                        deltaWeights.set(j-1,thisLayerDeltaWeights);
                        deltaBiases.set(j-1,thisLayerDeltaBiases);
                    }
                    currentBatch=0;
                }
            }
            //izracunaj pogresku i print
            double currentError=0;
            for (int i = 0; i < dataset.size(); i++) {
                List<Double> xi = dataset.get(i).get(0);
                List<Double> yi = dataset.get(i).get(1);

                List<Double> hi = this.forwardPass(xi);

                for (int j = 0; j < yi.size(); j++) {
                    currentError += (yi.get(j) - hi.get(j)) * (yi.get(j) - hi.get(j));
                }
            }
            currentError*=1./(2*dataset.size());
            System.out.println(currentError);

            if(currentError<epsilon){
                System.out.println("Konvergencija u "+epoch+" epoha");
                break;
            }




        }
    }

    public void stochasticGradientDescent(List<List<List<Double>>> dataset,double learningRate){
        this.train(dataset,1,learningRate,EPSILON);
    }
    public void batchGradientDescent(List<List<List<Double>>> dataset,double learningRate){
        this.train(dataset,dataset.size(),learningRate,EPSILON);
    }
    public void miniBatchGradientDescent(List<List<List<Double>>> dataset,double learningRate){
        //PRETPOSTAVKA JE DA JE BROJ PRIMJERA ZA SVAKU KLASU ISTI
        //svaki mini batch ima po dva primjera za svaku klasu
        List<List<List<Double>>> newDataset=new LinkedList<>();
        int numberOfClasses=dataset.get(0).get(1).size();
        int examplesPerClass=dataset.size()/numberOfClasses;
        for(int i=0;i<examplesPerClass/2;i++){
            for(int j=0;j<numberOfClasses;j++) {
                newDataset.add(dataset.get(j*examplesPerClass+i*2));
                newDataset.add(dataset.get(j*examplesPerClass+i*2+1));
            }
        }
        this.train(newDataset,dataset.size()/(numberOfClasses*2),learningRate,EPSILON);
    }
}
