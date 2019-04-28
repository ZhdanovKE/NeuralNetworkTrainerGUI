package trainerapp.gui.controller;

import trainerapp.gui.model.NamedNeuralNetwork;
import trainerapp.gui.util.NeuralNetworkLoader;
import trainerapp.gui.util.Windows;
import trainerapp.gui.repository.NamedObjectRepository;
import trainerapp.gui.repository.SamplesRepository;
import neuralnetwork.NeuralNetwork;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Main Window Controller class
 * @author Konstantin Zhdanov
 */
public class MainWindowController implements Initializable {

    @FXML
    private Button trainNNButton;
    
    @FXML
    private Button saveNNButton;
    
    @FXML
    private Button testNNButton;
    
    @FXML
    private TextArea statusMessagesArea;
    
    @FXML
    private ListView<String> networksListView;
    
    private NamedObjectRepository<NeuralNetwork> nnRepository;
    
    @FXML
    private ListView<String> samplesListView;
    
    private NamedObjectRepository<SamplesRepository<Double>> samplesRepoRepository;
    
    private static final String UNIQUE_SUFFIX = "*";
    
    @FXML
    private void handleLoadSamplesButtonAction(ActionEvent event) {
        try {
            Window thisWindow = ((Node)event.getSource()).getScene().getWindow();

            Windows.showLoadSamplesWindow(thisWindow, samplesRepoRepository);
            
            focusSamplesRepoIfNecessary();
        }
        catch (IllegalArgumentException e) {
            reportMessage("Exception: " + e.toString());
        }
    }
    
    @FXML
    private void handleTrainNNButtonAction(ActionEvent event) {
        if (nnRepository.isEmpty()) {
            reportMessage("First create or load a neural network");
            return;
        }
        String selectedNNName = networksListView.getSelectionModel().
                getSelectedItem();
        if (selectedNNName == null) {
            reportMessage("First select a neural network");
            return;
        }
        NeuralNetwork selectedNN = nnRepository.get(selectedNNName);
        String selectedRepoName = samplesListView.getSelectionModel().getSelectedItem();
        SamplesRepository<Double> selectedRepo = null;
        if (selectedRepoName != null) {
            selectedRepo = samplesRepoRepository.get(selectedRepoName);
        }
        try {
            Window thisWindow = ((Node)event.getSource()).getScene().getWindow();

            Windows.showTrainNetworkWindow(thisWindow, nnRepository, selectedNN, 
                    samplesRepoRepository, selectedRepo);
        }
        catch (IllegalArgumentException e) {
            reportMessage("Exception: " + e.toString());
        }
    }
    
    @FXML
    private void handleLoadNNButtonAction(ActionEvent event) {
        Window thisWindow = ((Node)event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Neural Network File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(thisWindow);
        if (selectedFile != null) {
            reportMessage("Selected file: " + selectedFile.getAbsolutePath());
            NeuralNetworkLoader loader = new NeuralNetworkLoader();
            try {
                NeuralNetwork loadedNetwork = loader.load(selectedFile.getAbsolutePath());
                if (loadedNetwork instanceof NamedNeuralNetwork) {
                    addNetworkToList(loadedNetwork, ((NamedNeuralNetwork) loadedNetwork).getName());
                }
                else {
                    addNetworkToList(loadedNetwork, extractFileName(selectedFile.getName()));
                }
            }
            catch(IllegalArgumentException e) {
                reportMessage("Error while loading file: " + e.getMessage());
            }
        }
        else {
            reportMessage("File is null");
        }
    }
    
    private String extractFileName(String namePlusExtension) {
        int lastDotIdx = namePlusExtension.lastIndexOf(".");
        if (lastDotIdx == -1) {
            return namePlusExtension;
        }
        return namePlusExtension.substring(0, lastDotIdx);
    }
    
    @FXML
    private void handleSaveNNButtonAction(ActionEvent event) {
        if (nnRepository.isEmpty()) {
            reportMessage("First create or load a neural network");
            return;
        }
        String selectedNNName = networksListView.getSelectionModel().
                getSelectedItem();
        if (selectedNNName == null) {
            reportMessage("First select a neural network");
            return;
        }
        NeuralNetwork selectedNN = nnRepository.get(selectedNNName);
        
        Window thisWindow = ((Node)event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Neural Network File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        if (selectedNN instanceof NamedNeuralNetwork) {
            fileChooser.setInitialFileName(((NamedNeuralNetwork) selectedNN).getName() + ".dat");
        }
        else {
            fileChooser.setInitialFileName(selectedNNName + ".dat");
        }
        File selectedFile = fileChooser.showSaveDialog(thisWindow);
        if (selectedFile != null) {
            reportMessage("Selected file: " + selectedFile.getAbsolutePath());
            NeuralNetworkLoader loader = new NeuralNetworkLoader();
            try {
                loader.save(selectedNN, selectedFile.getAbsolutePath());
            }
            catch(IllegalArgumentException e) {
                reportMessage("Error while saving a file: " + e.getMessage());
            }
        }
        else {
            reportMessage("File is null");
        }
    }
    
    @FXML
    private void handleCreateNNButtonAction(ActionEvent event) {
        try {
            Window thisWindow = ((Node)event.getSource()).getScene().getWindow();

            Windows.showCreateNetworkWindow(thisWindow, nnRepository);
            
            focusNetworkIfNecessary();

        }
        catch (IllegalArgumentException e) {
            reportMessage("Exception: " + e.toString());
        }
    }
    
    private void addNetworkToList(NeuralNetwork nn, String name) {
        if (nnRepository.containsName(name)) {
            name = name + UNIQUE_SUFFIX;
        }
        nnRepository.add(name, nn);
        focusNetworkIfNecessary();
    }
    
    public void focusNetworkIfNecessary() {
        if (nnRepository.size() == 1) {
            networksListView.getSelectionModel().select(0);
        }
    }
    
    public void focusSamplesRepoIfNecessary() {
        if (samplesRepoRepository.size() == 1) {
            samplesListView.getSelectionModel().select(0);
        }
    }
    
    @FXML
    private void handleTestNNButtonAction(ActionEvent event) {
        if (nnRepository.isEmpty()) {
            reportMessage("First create or load a neural network");
            return;
        }
        String selectedNNName = networksListView.getSelectionModel().
                getSelectedItem();
        if (selectedNNName == null) {
            reportMessage("First select a neural network");
            return;
        }
        NeuralNetwork selectedNN = nnRepository.get(selectedNNName);
        try {
            Window thisWindow = ((Node)event.getSource()).getScene().getWindow();
            
            Windows.showTestNetworkWindow(thisWindow, nnRepository, selectedNN);
        } 
        catch (IllegalArgumentException e) {
            reportMessage("Exception: " + e.toString());
        }
    }
    
    private void reportMessage(String msg) {
        statusMessagesArea.appendText(msg);
        statusMessagesArea.appendText("\n");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nnRepository = new NamedObjectRepository<>();
        samplesRepoRepository = new NamedObjectRepository<>();
        networksListView.setItems(nnRepository.getNamesObservableList());
        samplesListView.setItems(samplesRepoRepository.getNamesObservableList());
        
        trainNNButton.disableProperty().bind(Bindings.isEmpty(networksListView.getItems()));
        testNNButton.disableProperty().bind(Bindings.isEmpty(networksListView.getItems()));
        saveNNButton.disableProperty().bind(Bindings.isEmpty(networksListView.getItems()));
    }
}
