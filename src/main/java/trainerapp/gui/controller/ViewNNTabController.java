package trainerapp.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.util.converter.DoubleStringConverter;
import neuralnetwork.NeuralNetwork;
import trainerapp.gui.facade.NumberTableViewFacade;

/**
 * View Neural Network Tab Controller
 * @author Konstantin Zhdanov
 */
public class ViewNNTabController implements Initializable {
    
    @FXML
    private TableView<ObservableList<Double>> weightsTableView;
    private NumberTableViewFacade<Double> weightsTableViewFacade;
    
    @FXML
    private TableView<ObservableList<Double>> biasesTableView;
    private NumberTableViewFacade<Double> biasesTableViewFacade;

    @FXML
    private Label curLayerNeuronLabel;
    
    @FXML
    private Label curLayerBiasesLabel;

    @FXML
    private Label prevLayerNeuronLabel;
    
    private NeuralNetwork network;
    
    private ObservableList<Double> layerBiases;
    
    private ObservableList<ObservableList<Double>> layerWeights;
    
    private int curLayerNum;
    
    private int curLayerSize;
    
    private int prevLayerSize;
    
    public ObservableList<ObservableList<Double>> getWeights() {
        return layerWeights;
    }
    
    public ObservableList<Double> getBiases() {
        return layerBiases;
    }
    
    public void setUp(NeuralNetwork nn, int curLayerNum) {
        if (nn == null) {
            throw new NullPointerException("Network cannot be null");
        }
        if (curLayerNum < 0) {
            throw new IllegalArgumentException("Index of layer cannot be negative");
        }
        network = nn;
        this.curLayerNum = curLayerNum;
        initLayerInfoData();

        // Set weights and biases
        layerBiases = FXCollections.observableList(
            new ArrayList<>(curLayerSize));
        for (int toNeuron = 0; toNeuron < curLayerSize; toNeuron++) {
            weightsTableViewFacade.addColumn("Neuron " + (toNeuron + 1));
            biasesTableViewFacade.addColumn("Neuron " + (toNeuron + 1));
            layerBiases.add(nn.getBias(curLayerNum, toNeuron));
        }
        biasesTableViewFacade.getItems().add(layerBiases);
        
        layerWeights = FXCollections.observableList(
                new ArrayList<>(prevLayerSize));
        for (int fromNeuron = 0; fromNeuron < prevLayerSize; fromNeuron++) {
            ObservableList<Double> weightsFromNeuron = 
                    FXCollections.observableList(new ArrayList<>(
                            curLayerSize));
            for (int toNeuron = 0; toNeuron < curLayerSize; toNeuron++) {
                 weightsFromNeuron.add(nn.getWeight(curLayerNum, fromNeuron, toNeuron));
            }
            layerWeights.add(weightsFromNeuron);
        }
        weightsTableViewFacade.setItems(layerWeights);
    }
    
    private void initLayerInfoData() {
        String curLayerStr;
        String prevLayerStr;
        if (curLayerNum == 0) {
            // first hidden layer
            curLayerSize = network.getHiddenLayerSize(curLayerNum);
            prevLayerSize = network.getNumberInputs();
            curLayerStr = "Layer 1";
            prevLayerStr = "Input layer";
        }
        else if (curLayerNum == network.getNumberHiddenLayers()) {
            // output layer
            curLayerSize = network.getNumberOutputs();
            prevLayerSize = network.getHiddenLayerSize(curLayerNum - 1);
            curLayerStr = "Output layer";
            prevLayerStr = "Layer " + (curLayerNum);
        }
        else {
            curLayerSize = network.getHiddenLayerSize(curLayerNum);
            prevLayerSize = network.getHiddenLayerSize(curLayerNum - 1);
            curLayerStr = "Layer " + (curLayerNum + 1);
            prevLayerStr = "Layer " + (curLayerNum);
        }

        // Set labels
        curLayerNeuronLabel.setText(curLayerStr + "'s neurons");
        curLayerBiasesLabel.setText(curLayerStr + "'s biases");
        prevLayerNeuronLabel.setText(prevLayerStr + "'s neurons");
    }
    
    public void saveChanges() {
        if (network == null) {
            throw new IllegalStateException("This tab hasn't been set up");
        }
        for (int toNeuron = 0; toNeuron < curLayerSize; toNeuron++) {
            network.setBias(curLayerNum, toNeuron, layerBiases.get(toNeuron));
        }

        for (int fromNeuron = 0; fromNeuron < prevLayerSize; fromNeuron++) {
            for (int toNeuron = 0; toNeuron < curLayerSize; toNeuron++) {
                 network.setWeight(curLayerNum, fromNeuron, toNeuron, 
                         layerWeights.get(fromNeuron).get(toNeuron));
            }
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        weightsTableViewFacade = new NumberTableViewFacade<>(weightsTableView, 
            new DoubleStringConverter());
        biasesTableViewFacade = new NumberTableViewFacade<>(biasesTableView, 
            new DoubleStringConverter());
    }
}
