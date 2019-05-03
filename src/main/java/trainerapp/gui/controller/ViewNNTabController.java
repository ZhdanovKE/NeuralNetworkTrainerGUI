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
    
    public void setUp(NeuralNetwork nn, int curLayerNum) {
        int curLayerSize;
        int prevLayerSize;
        String curLayerStr;
        String prevLayerStr;
        if (curLayerNum == 0) {
            // first hidden layer
            curLayerSize = nn.getHiddenLayerSize(curLayerNum);
            prevLayerSize = nn.getNumberInputs();
            curLayerStr = "Layer 1";
            prevLayerStr = "Input layer";
        }
        else if (curLayerNum == nn.getNumberHiddenLayers()) {
            // output layer
            curLayerSize = nn.getNumberOutputs();
            prevLayerSize = nn.getHiddenLayerSize(curLayerNum - 1);
            curLayerStr = "Output layer";
            prevLayerStr = "Layer " + (curLayerNum);
        }
        else {
            curLayerSize = nn.getHiddenLayerSize(curLayerNum);
            prevLayerSize = nn.getHiddenLayerSize(curLayerNum - 1);
            curLayerStr = "Layer " + (curLayerNum + 1);
            prevLayerStr = "Layer " + (curLayerNum);
        }

        // Set labels
        curLayerNeuronLabel.setText(curLayerStr + "'s neurons");
        curLayerBiasesLabel.setText(curLayerStr + "'s biases");
        prevLayerNeuronLabel.setText(prevLayerStr + "'s neurons");
             
        // Set weights and biases
        ObservableList<Double> biases = FXCollections.observableList(
            new ArrayList<>(curLayerSize));
        for (int toNeuron = 0; toNeuron < curLayerSize; toNeuron++) {
            weightsTableViewFacade.addColumn("Neuron " + (toNeuron + 1));
            biasesTableViewFacade.addColumn("Neuron " + (toNeuron + 1));
            biases.add(nn.getBias(curLayerNum, toNeuron));
        }
        biasesTableViewFacade.getItems().add(biases);
        for (int fromNeuron = 0; fromNeuron < prevLayerSize; fromNeuron++) {
            ObservableList<Double> weightsFromNeuron = 
                    FXCollections.observableList(new ArrayList<>(
                            curLayerSize));
            for (int toNeuron = 0; toNeuron < curLayerSize; toNeuron++) {
                 weightsFromNeuron.add(nn.getWeight(curLayerNum, fromNeuron, toNeuron));
            }
            weightsTableViewFacade.getItems().add(weightsFromNeuron);
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
