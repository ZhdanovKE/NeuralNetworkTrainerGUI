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
import trainerapp.gui.facade.ListViewEditingFacade;

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
    private Button removeNNButton;
    
    @FXML
    private Button testNNButton;
    
    @FXML
    private Button viewNNButton;
    
    @FXML
    private Button viewSamplesButton;
    
    @FXML
    private Button removeSamplesButton;
    
    @FXML
    private TextArea statusMessagesArea;
    
    @FXML
    private ListView<NeuralNetwork> networksListView;
    private ListViewEditingFacade<NeuralNetwork> networksListViewFacade;
    
    private NamedObjectRepository<NeuralNetwork> nnRepository;
    
    @FXML
    private ListView<SamplesRepository<Double>> samplesListView;
    private ListViewEditingFacade<SamplesRepository<Double>> samplesListViewFacade;
    
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
    private void handleViewSamplesButtonAction(ActionEvent event) {
        if (samplesRepoRepository.isEmpty()) {
            reportMessage("First load samples");
            return;
        }
        SamplesRepository<Double> selectedRepo = samplesListViewFacade.
                getSelectedItem();
        if (selectedRepo == null) {
            reportMessage("First select samples");
            return;
        }
        try {
            Window thisWindow = ((Node)event.getSource()).getScene().getWindow();

            Windows.showViewSamplesWindow(thisWindow, samplesRepoRepository, 
                    selectedRepo);
        }
        catch (IllegalArgumentException e) {
            reportMessage("Exception: " + e.toString());
        }
    }
    
    @FXML
    private void handleRemoveSamplesButtonAction(ActionEvent event) {
        SamplesRepository<Double> selectedRepo = samplesListViewFacade.
                getSelectedItem();
        if (selectedRepo == null) {
            reportMessage("First select samples");
            return;
        }
        samplesRepoRepository.remove(samplesRepoRepository.
                getNameForObject(selectedRepo));
    }
    
    @FXML
    private void handleTrainNNButtonAction(ActionEvent event) {
        if (nnRepository.isEmpty()) {
            reportMessage("First create or load a neural network");
            return;
        }
        NeuralNetwork selectedNN = networksListViewFacade.getSelectedItem();
        if (selectedNN == null) {
            reportMessage("First select a neural network");
            return;
        }
        SamplesRepository<Double> selectedRepo = samplesListViewFacade.
                getSelectedItem();
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
                NeuralNetwork loadedNetwork;
                if (extractExtension(selectedFile.getName()).toLowerCase().
                        trim().equals("txt")) {
                    loadedNetwork = loader.loadFromTextFile(selectedFile.getAbsolutePath());
                }
                else {
                    loadedNetwork = loader.load(selectedFile.getAbsolutePath());
                }
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
    
    private String extractExtension(String namePlusExtension) {
        int lastDotIdx = namePlusExtension.lastIndexOf(".");
        if (lastDotIdx == -1) {
            return "";
        }
        return namePlusExtension.substring(lastDotIdx + 1);
    }
    
    @FXML
    private void handleSaveNNButtonAction(ActionEvent event) {
        if (nnRepository.isEmpty()) {
            reportMessage("First create or load a neural network");
            return;
        }
        NeuralNetwork selectedNN = networksListViewFacade.getSelectedItem();
        if (selectedNN == null) {
            reportMessage("First select a neural network");
            return;
        }
        Window thisWindow = ((Node)event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Neural Network File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        String networkName = nnRepository.getNameForObject(selectedNN);
        fileChooser.setInitialFileName(networkName.trim().
                        replaceAll("\\s+", "_") + ".dat");
        File selectedFile = fileChooser.showSaveDialog(thisWindow);
        if (selectedFile != null) {
            reportMessage("Selected file: " + selectedFile.getAbsolutePath());
            NeuralNetworkLoader loader = new NeuralNetworkLoader();
            try {
                if (extractExtension(selectedFile.getAbsolutePath()).
                        toLowerCase().trim().equals("txt")) {
                    reportMessage("Saving network as text");
                    loader.saveWithNameAsText(selectedNN, networkName, 
                            selectedFile.getAbsolutePath());
                }
                else {
                    reportMessage("Saving network as binary data");
                    loader.saveWithName(selectedNN, networkName, 
                            selectedFile.getAbsolutePath());
                }
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
    
    @FXML
    private void handleRemoveNNButtonAction(ActionEvent event) {
        NeuralNetwork selectedNN = networksListViewFacade.getSelectedItem();
        if (selectedNN == null) {
            reportMessage("First select a neural network");
            return;
        }
        nnRepository.remove(nnRepository.getNameForObject(selectedNN));
    }
    
    private void addNetworkToList(NeuralNetwork nn, String name) {
        if (nnRepository.containsName(name)) {
            name = name + UNIQUE_SUFFIX;
            if (nn instanceof NamedNeuralNetwork) {
                ((NamedNeuralNetwork) nn).setName(name);
            }
        }
        nnRepository.add(name, nn);
        focusNetworkIfNecessary();
    }
    
    public void focusNetworkIfNecessary() {
        if (nnRepository.size() == 1) {
            networksListViewFacade.select(nnRepository.
                    getObjectsObservableList().get(0));
        }
    }
    
    public void focusSamplesRepoIfNecessary() {
        if (samplesRepoRepository.size() == 1) {
            samplesListViewFacade.select(samplesRepoRepository.
                    getObjectsObservableList().get(0));
        }
    }
    
    @FXML
    private void handleTestNNButtonAction(ActionEvent event) {
        if (nnRepository.isEmpty()) {
            reportMessage("First create or load a neural network");
            return;
        }
        NeuralNetwork selectedNN = networksListViewFacade.getSelectedItem();
        if (selectedNN == null) {
            reportMessage("First select a neural network");
            return;
        }
        try {
            Window thisWindow = ((Node)event.getSource()).getScene().getWindow();
            
            Windows.showTestNetworkWindow(thisWindow, nnRepository, selectedNN);
        } 
        catch (IllegalArgumentException e) {
            reportMessage("Exception: " + e.toString());
        }
    }
    
    @FXML 
    private void handleViewNNButtonAction(ActionEvent event) {
        NeuralNetwork selectedNN = networksListViewFacade.getSelectedItem();
        if (selectedNN == null) {
            reportMessage("First select a neural network");
            return;
        }
        try {
            Window thisWindow = ((Node)event.getSource()).getScene().getWindow();
            
            Windows.showViewNetworkWindow(thisWindow, nnRepository, 
                    selectedNN);
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
        nnRepository.setOnNameChangeListener((object, oldName, newName) -> {
            if (object instanceof NamedNeuralNetwork) {
                ((NamedNeuralNetwork) object).setName(newName);
            }
        });
        networksListViewFacade = new ListViewEditingFacade<>(networksListView, 
                nnRepository);
        
        samplesRepoRepository = new NamedObjectRepository<>();        
        samplesListViewFacade = new ListViewEditingFacade<>(samplesListView,
                samplesRepoRepository);
        
        trainNNButton.disableProperty().bind(Bindings.isNull(networksListView.
                getSelectionModel().selectedItemProperty()));
        testNNButton.disableProperty().bind(Bindings.isNull(networksListView.
                getSelectionModel().selectedItemProperty()));
        saveNNButton.disableProperty().bind(Bindings.isNull(networksListView.
                getSelectionModel().selectedItemProperty()));
        viewNNButton.disableProperty().bind(Bindings.isNull(networksListView.
                getSelectionModel().selectedItemProperty()));
        removeNNButton.disableProperty().bind(Bindings.isNull(networksListView.
                getSelectionModel().selectedItemProperty()));
        viewSamplesButton.disableProperty().bind(Bindings.isNull(samplesListView.
                getSelectionModel().selectedItemProperty()));
        removeSamplesButton.disableProperty().bind(Bindings.isNull(samplesListView.
                getSelectionModel().selectedItemProperty()));
    }
}
