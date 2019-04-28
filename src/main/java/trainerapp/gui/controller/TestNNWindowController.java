package trainerapp.gui.controller;

import trainerapp.gui.facade.NumberTableViewFacade;
import trainerapp.gui.repository.NamedObjectRepository;
import neuralnetwork.NeuralNetwork;
import neuralnetwork.train.NeuralNetworkEvaluator;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

/**
 * Test Neural Network Window Controller class
 *
 * @author Konstantin Zhdanov
 */
public class TestNNWindowController implements Initializable {

    @FXML
    private TableView<ObservableList<Double>> outputTableView;
    private NumberTableViewFacade<Double> outputTableViewFacade;

    @FXML
    private ComboBox<NeuralNetwork> nnCombobox;

    @FXML
    private Button evaluateButton;
    
    @FXML
    private TableView<ObservableList<Double>> inputTableView;
    private NumberTableViewFacade<Double> inputTableViewFacade;
    
    private final ObservableList<NeuralNetwork> nnList;
    
    private NamedObjectRepository<NeuralNetwork> nnRepository;
    
    private final WritableValue<NeuralNetwork> chosenNetwork;
    
    private NeuralNetworkEvaluator nnEvaluator;
    
    public TestNNWindowController() {
        chosenNetwork = new SimpleObjectProperty<>(null);
        nnList = FXCollections.observableArrayList();
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void handleCloseButtonAction(ActionEvent event) {
        closeWindow(event);
    }
    
    @FXML
    void handleEvaluateButtonAction(ActionEvent event) {
        if (chosenNetwork.getValue() == null) {
            return;
        }
        for (int rowIdx = 0; rowIdx < inputTableViewFacade.getItems().size(); rowIdx++) {
            List<Double> inputList = inputTableViewFacade.getItems().get(rowIdx);
            double[] input = inputList.stream().mapToDouble(Double::doubleValue).
                    toArray();
            double[] output = nnEvaluator.getOutput(input);
            setOutputForRow(output, rowIdx);
        }
    }

    @FXML
    void handleAddInputButtonAction(ActionEvent event) {
        if (chosenNetwork.getValue() == null) {
            return;
        }
        addRow();
        addEmptyOutputRow();
    }

    @FXML
    void handleRemoveInputButtonAction(ActionEvent event) {
        if (chosenNetwork.getValue() == null) {
            return;
        }
        removeLastRow();
        removeLastOutputRow();
    }
    
    public void setNetworkRepository(NamedObjectRepository<NeuralNetwork> nnRepository) {
        this.nnRepository = nnRepository;
        updateNetworkList();
    }
    
    public void selectNetwork(NeuralNetwork selectedNN) {
        nnCombobox.getSelectionModel().select(selectedNN);
    }
    
    private void updateNetworkList() {
        nnList.clear();
        nnRepository.getNames().forEach((name) -> {
            nnList.add(nnRepository.get(name));
        });
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nnCombobox.setConverter(converter);
        nnCombobox.setItems(nnList);
        nnCombobox.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    setChosenNetwork(newValue);
        });
        
        inputTableViewFacade = new NumberTableViewFacade<>(inputTableView,
            new DoubleStringConverter());
        outputTableViewFacade = new NumberTableViewFacade<>(outputTableView, 
            new DoubleStringConverter());
    }    
    
    private void setChosenNetwork(NeuralNetwork nn) {
        evaluateButton.setDisable(true);
        
        chosenNetwork.setValue(nn);
        nnEvaluator = new NeuralNetworkEvaluator(nn);
        
        inputTableViewFacade.clear();
        createInputColumns(nn.getNumberInputs());
        addRow();
        
        outputTableViewFacade.clear();
        createOutputColumns(nn.getNumberOutputs());
        addEmptyOutputRow();
        
        evaluateButton.setDisable(false);
    }
    
    private void createInputColumns(int nColumns) {
        for (int columnIdx = 0; columnIdx < nColumns; columnIdx++) {
            inputTableViewFacade.addColumn("Var " + (columnIdx + 1));
        }
    }
    
    private void createOutputColumns(int nColumns) {
        for (int columnIdx = 0; columnIdx < nColumns; columnIdx++) {
            outputTableViewFacade.addColumn("Output " + (columnIdx + 1));
        }
    }
   
    private void addRow() {
        final int size = inputTableViewFacade.getColumnsCount();
        ObservableList<Double> newRow = FXCollections.observableArrayList();
        for (int i = 0; i < size; i++) {
            newRow.add(0.0);
        }
        inputTableViewFacade.getItems().add(newRow);
    }
    
    private void removeLastRow() {
        if (inputTableView.getItems().size() == 1) {
            return;
        }
        inputTableView.getItems().remove(inputTableView.getItems().size() - 1);
    }
    
    private void addEmptyOutputRow() {
        final int size = outputTableViewFacade.getColumnsCount();
        ObservableList<Double> newRow = FXCollections.observableArrayList();
        for (int i = 0; i < size; i++) {
            newRow.add(null);
        }
        outputTableViewFacade.getItems().add(newRow);
    }
    
    private void removeLastOutputRow() {
        if (outputTableViewFacade.getItems().size() == 1) {
            return;
        }
        outputTableViewFacade.getItems().remove(
                outputTableViewFacade.getItems().size() - 1);
    }
    
    private void setOutputForRow(double[] output, int rowIdx) {
        ObservableList<Double> newRow = FXCollections.observableArrayList();
        for (int i = 0; i < output.length; i++) {
            newRow.add(output[i]);
        }
        outputTableViewFacade.getItems().set(rowIdx, newRow);
    }
    
    private final NeuralNetworkConverter converter = new NeuralNetworkConverter();
    
    private static class NeuralNetworkConverter extends StringConverter<NeuralNetwork> {

        @Override
        public String toString(NeuralNetwork object) {
            return object.toString();
        }

        @Override
        public NeuralNetwork fromString(String string) {
            throw new UnsupportedOperationException("Not supported.");
        }
        
    }
    
}
