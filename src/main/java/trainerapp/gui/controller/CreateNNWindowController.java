package trainerapp.gui.controller;

import trainerapp.gui.model.NamedNeuralNetwork;
import trainerapp.gui.facade.NumberTableViewFacade;
import trainerapp.gui.facade.TextFieldErrorMessageFacade;
import trainerapp.gui.facade.VisualControlErrorMessageFacade;
import trainerapp.gui.repository.NamedObjectRepository;
import neuralnetwork.NeuralNetwork;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * Neural Network Creation Window Controller class
 *
 * @author Konstantin Zhdanov
 */
public class CreateNNWindowController implements Initializable {

    @FXML
    private TableView<ObservableList<Integer>> hiddenLayerSizesTable;
    private NumberTableViewFacade<Integer> hiddenLayerSizesTableFacade;
    private VisualControlErrorMessageFacade hiddenLayerSizesTableErrorFacade;
    
    @FXML
    private TextField nInputsField;
    private TextFieldErrorMessageFacade nInputsFieldErrorFacade;
    
    @FXML
    private TextField nOutputsField;
    private TextFieldErrorMessageFacade nOutputsFieldErrorFacade;
    
    @FXML
    private TextField nameField;
    private TextFieldErrorMessageFacade nameFieldErrorFacade;
    
    @FXML
    private CheckBox randomWeightsCheckbox;
    
    ObservableList<Integer> hiddenLayerSizes;
    
    public NamedObjectRepository<NeuralNetwork> nnRepository;
            
    public void setNetworkRepository(NamedObjectRepository<NeuralNetwork> nnRepository) {
        if (nnRepository == null) {
            throw new NullPointerException("Network repository cannot be null");
        }
        this.nnRepository = nnRepository;
    }
    
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        closeWindow(event);
    }
    
    @FXML
    private void handleCreateButtonAction(ActionEvent event) {
        int nInputs;
        try {
            nInputs = Integer.parseInt(nInputsField.getText());
            if (nInputs <= 0) {
                throw new NumberFormatException("Cannot be zero or negative");
            }
            nInputsFieldErrorFacade.hideError();
        }
        catch (NumberFormatException e) {
            nInputsFieldErrorFacade.showError("Value provided for the number of inputs is invalid.\n Must be an integer greater than 0.");
            return;
        }
        int nOutputs;
        try {
            nOutputs = Integer.parseInt(nOutputsField.getText());
            if (nOutputs <= 0) {
                throw new NumberFormatException("Cannot be zero or negative");
            }
            nOutputsFieldErrorFacade.hideError();
        }
        catch (NumberFormatException e) {
            nOutputsFieldErrorFacade.showError("Value provided for the number of outputs is invalid.\n Must be an integer greater than 0.");
            return;
        }
        int[] hiddenLayerSizesArray = new int[this.hiddenLayerSizes.size()];
        for (int i = 0; i < this.hiddenLayerSizes.size(); i++) {
            int value = this.hiddenLayerSizes.get(i);
            if (value <= 0) {
                hiddenLayerSizesTableErrorFacade.showError("Hidden layer sizes cannot be zero or negative.\nChoose another values.");
                return;
            }
            hiddenLayerSizesArray[i] = value;
        }
        hiddenLayerSizesTableErrorFacade.hideError();
        
        boolean randomWeights = randomWeightsCheckbox.isSelected();
        String name = nameField.getText().trim();
        if (name.length() < 1) {
            nameFieldErrorFacade.showError("Name cannot be empty.\nChoose another name.");
            return;
        }
        if (nnRepository.containsName(name)) {
            nameFieldErrorFacade.showError("A neural network with the same name already exists.\nChoose another name.");
            return;
        }
        nameFieldErrorFacade.hideError();
      
        NeuralNetwork newNN = createNamedNeuralNetwork(nInputs, hiddenLayerSizesArray,
                nOutputs, name);
                
        if (newNN != null) {
            if (randomWeights) {
                randomizeWeights(newNN);
            }
            nnRepository.add(name, newNN);
        }
        else {
        }
        closeWindow(event);
    }
    
    private NeuralNetwork createNamedNeuralNetwork(int nInputs, int[] hiddenLayerSizes,
            int nOutputs, String name) {
        NeuralNetwork nn = null;
        try {
            nn = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, 
                    nOutputs, name);
        }
        catch(Exception e) {

        }
        return nn;
    }
    
    private void randomizeWeights(NeuralNetwork arg) {
        Random rnd = new Random();
        for (int neuronNum = 0; neuronNum < arg.getHiddenLayerSize(0); neuronNum++) {
            for (int prevLayerNeuron = 0; prevLayerNeuron < arg.getNumberInputs(); prevLayerNeuron++) {
                arg.setWeight(0, prevLayerNeuron, neuronNum, rnd.nextDouble());
            }
            arg.setBias(0, neuronNum, rnd.nextDouble());
        }
        
        for (int layerNum = 1; layerNum < arg.getNumberHiddenLayers(); layerNum++) {
            for (int neuronNum = 0; neuronNum < arg.getHiddenLayerSize(layerNum); neuronNum++) {
                for (int prevLayerNeuron = 0; prevLayerNeuron < arg.getHiddenLayerSize(layerNum - 1); prevLayerNeuron++) {
                    arg.setWeight(layerNum, prevLayerNeuron, neuronNum, rnd.nextDouble());
                }
                arg.setBias(layerNum, neuronNum, rnd.nextDouble());
            }
        }
        
        for (int neuronNum = 0; neuronNum < arg.getNumberOutputs(); neuronNum++) {
            for (int prevLayerNeuron = 0; prevLayerNeuron < arg.getHiddenLayerSize(arg.getNumberHiddenLayers() - 1); prevLayerNeuron++) {
                arg.setWeight(arg.getNumberHiddenLayers(), prevLayerNeuron, neuronNum, rnd.nextDouble());
            }
            arg.setBias(arg.getNumberHiddenLayers(), neuronNum, rnd.nextDouble());
        }
    }
    
    @FXML
    private void handleAddLayerButtonAction(ActionEvent event) {
        hiddenLayerSizes.add(1);
        hiddenLayerSizesTableFacade.addColumn("Layer " + hiddenLayerSizes.size());
    }
    
    @FXML
    private void handleRemoveLayerButtonAction(ActionEvent event) {
        if (hiddenLayerSizes.size() == 1) {
            return;
        }
        hiddenLayerSizesTableFacade.removeLastColumn();
        hiddenLayerSizes.remove(hiddenLayerSizes.size() - 1);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hiddenLayerSizes = FXCollections.observableArrayList();

        hiddenLayerSizesTableFacade = new NumberTableViewFacade<>(hiddenLayerSizesTable, 
            new IntegerStringConverter());
        
        hiddenLayerSizesTableFacade.addColumn("Layer 1");
        hiddenLayerSizes.add(1);
        
        hiddenLayerSizesTableFacade.getItems().add(hiddenLayerSizes);

        nInputsFieldErrorFacade = new TextFieldErrorMessageFacade(nInputsField);
        nOutputsFieldErrorFacade = new TextFieldErrorMessageFacade(nOutputsField);
        nameFieldErrorFacade = new TextFieldErrorMessageFacade(nameField);
        
        hiddenLayerSizesTableErrorFacade = new VisualControlErrorMessageFacade(
                hiddenLayerSizesTable);
        
        String name = generateName();
        nameField.setText(name);
    }    
    
    private String generateName() {
        final int nameLength = 5;
        Random rnd = new Random();
        char[] nameChars = new char[nameLength];
        for (int i = 0; i < nameLength; i++) {
            nameChars[i] = (char)('a' + rnd.nextInt(26));
        }
        return new String(nameChars);
    }
}
