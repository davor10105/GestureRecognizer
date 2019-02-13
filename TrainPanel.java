import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class TrainPanel extends JPanel {
    private static String[] TRAIN_OPTIONS={"STOCHASTIC","MINIBATCH","BATCH"};
    private static String DEFAULT_PATH="./dataset.txt";
    private Thread runningTrain;
    JButton trainButton;

    public TrainPanel(DrawPanel trainDrawPanel,PointCollector pointCollector){
        this.setLayout(new BorderLayout());
        this.add(trainDrawPanel,BorderLayout.CENTER);

        JPanel buttonPanel=new JPanel();
        Button clearScreen=new Button("CLEAR SCREEN");
        clearScreen.addActionListener((e)->{
            trainDrawPanel.repaint();
        });
        Button finishClass=new Button("FINISH CLASS");
        finishClass.addActionListener((e)->{
            pointCollector.nextClass();
        });
        Button undoDrawing=new Button("UNDO LAST DRAWING");
        undoDrawing.addActionListener((e)->{
            if (pointCollector.hasDrawings()){
                pointCollector.deleteLastDrawing();
            }
        });
        JTextField createDatasetField=new JTextField();
        createDatasetField.setText(DEFAULT_PATH);
        createDatasetField.setColumns(20);
        Button createDataset=new Button("CREATE DATASET");
        createDataset.addActionListener((e)->{
            Thread t=new Thread(()->{
                pointCollector.createDataset(createDatasetField.getText());
            });

            t.start();

        });
        buttonPanel.add(finishClass);
        buttonPanel.add(clearScreen);
        buttonPanel.add(undoDrawing);
        buttonPanel.add(createDatasetField);
        buttonPanel.add(createDataset);

        JPanel wholeButtonPanel=new JPanel();
        wholeButtonPanel.setLayout(new BorderLayout());
        wholeButtonPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel bottomButtonPanel=new JPanel();
        JPanel datasetPathPanel=new JPanel();

        JLabel datasetPathLabel=new JLabel("Dataset path");
        JTextField datasetPath=new JTextField();
        datasetPath.setColumns(20);

        datasetPathPanel.add(datasetPathLabel);
        datasetPathPanel.add(datasetPath);

        JPanel hiddenLayerSizesPanel=new JPanel();

        JLabel hiddenLayerSizesLabel=new JLabel("Hidden layers:");
        JTextField hiddenLayerSizes=new JTextField();
        hiddenLayerSizes.setText("20");
        hiddenLayerSizes.setColumns(10);

        hiddenLayerSizesPanel.add(hiddenLayerSizesLabel);
        hiddenLayerSizesPanel.add(hiddenLayerSizes);

        JPanel learningRatePanel=new JPanel();

        JLabel learningRateLabel=new JLabel("Learning rate:");
        JTextField learningRate=new JTextField();
        learningRate.setText("10");
        learningRate.setColumns(8);

        learningRatePanel.add(learningRateLabel);
        learningRatePanel.add(learningRate);

        JPanel comboBoxPanel=new JPanel();
        JLabel comboBoxLabel=new JLabel("Batch size:");
        JComboBox<String> comboBox=new JComboBox<>(TRAIN_OPTIONS);
        comboBox.setSelectedIndex(0);

        comboBoxPanel.add(comboBoxLabel);
        comboBoxPanel.add(comboBox);

        trainButton=new JButton("TRAIN");
        trainButton.addActionListener((e)->{
            String chosenOption=TRAIN_OPTIONS[comboBox.getSelectedIndex()];
            double learningRateDouble=Double.parseDouble(learningRate.getText());
            List<Integer> hiddenLayers=new LinkedList<>();
            for(String s:hiddenLayerSizes.getText().split(",")){
                hiddenLayers.add(Integer.parseInt(s));
            }
            if (chosenOption.equals("STOCHASTIC")){
                runningTrain=new Thread(()->{
                    SwingUtilities.invokeLater(()->{
                        trainButton.setEnabled(false);
                    });
                    pointCollector.stochasticGradientDescent(hiddenLayers,datasetPath.getText(),learningRateDouble);
                    SwingUtilities.invokeLater(()->{
                        trainButton.setEnabled(true);
                    });

                });
                runningTrain.start();

            }
            else if (chosenOption.equals("MINIBATCH")){
                runningTrain=new Thread(()->{
                    SwingUtilities.invokeLater(()->{
                        trainButton.setEnabled(false);
                    });
                    pointCollector.miniBatchGradientDescent(hiddenLayers,datasetPath.getText(),learningRateDouble);
                    SwingUtilities.invokeLater(()->{
                        trainButton.setEnabled(true);
                    });
                });
                runningTrain.start();

            }
            else{
                runningTrain=new Thread(()->{
                    SwingUtilities.invokeLater(()->{
                        trainButton.setEnabled(false);
                    });
                    pointCollector.batchGradientDescent(hiddenLayers,datasetPath.getText(),learningRateDouble);
                    SwingUtilities.invokeLater(()->{
                        trainButton.setEnabled(true);
                    });
                });
                runningTrain.start();
            }
        });

        datasetPath.setText(DEFAULT_PATH);

        //bottomButtonPanel.setLayout(new GridLayout(1,0));

        bottomButtonPanel.add(datasetPathPanel);
        bottomButtonPanel.add(hiddenLayerSizesPanel);
        bottomButtonPanel.add(learningRatePanel);
        bottomButtonPanel.add(comboBoxPanel);
        bottomButtonPanel.add(trainButton);

        wholeButtonPanel.add(bottomButtonPanel,BorderLayout.SOUTH);

        this.add(wholeButtonPanel,BorderLayout.SOUTH);
        //this.add(buttonPanel,BorderLayout.SOUTH);
    }
}
