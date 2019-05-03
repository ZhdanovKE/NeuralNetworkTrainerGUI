package trainerapp.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import neuralnetwork.NeuralNetwork;
import trainerapp.gui.repository.NamedObjectRepository;

/**
 * View Network Window Controller class
 *
 * @author Konstantin Zhdanov
 */
public class ViewNNWindowController implements Initializable {
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private ComboBox<NeuralNetwork> selectedNNComboBox;

    @FXML
    private Button closeButton;
    
    private NamedObjectRepository<NeuralNetwork> nnRepository;
    
    private NeuralNetwork nn;
    
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
    
    public void setNetworkRepository(NamedObjectRepository<NeuralNetwork> repo) {
        this.nnRepository = repo;
        selectedNNComboBox.setItems(nnRepository.getObjectsObservableList());
    }
    
    public void setNetwork(NeuralNetwork nn) {
        if (nn == null) {
            throw new NullPointerException("Network cannot be null");
        }        
        selectedNNComboBox.getSelectionModel().select(nn);
    }
    
    private void setChosenNetwork(NeuralNetwork network) {
        this.nn = network;
        updateTabs();
    }
    
    private void updateTabs() {
        tabPane.getTabs().clear();
        
        // Hidden layers
        for (int tabIdx = 0; tabIdx < nn.getNumberHiddenLayers(); tabIdx++) {
            Node tabContent = createTabContentForNN(nn, tabIdx);
            addTabWithContent(tabContent, "Layer " + (tabIdx + 1));
        }
        
        // Output layer
        Node tabContent = createTabContentForNN(nn, nn.getNumberHiddenLayers());
        addTabWithContent(tabContent, "Output");
    }
            
    private void addTabWithContent(Node content, String title) {
        Tab tab = new Tab(title);
        tab.setContent(content);
        tabPane.getTabs().add(tab);
    }
        
    
    private Node createTabContentForNN(NeuralNetwork nn, int idx) {
        FXMLLoader tabLoader = new FXMLLoader(this.getClass().
                getResource("/fxml/ViewNNTab.fxml"));
        Node tab = null;
        try {
            tab = tabLoader.load();
            ViewNNTabController tabController = (ViewNNTabController)tabLoader.
                    getController();
            tabController.setUp(nn, idx);
        }
        catch(IOException e) {
            System.out.println("Exception: " + e.toString());
        }
        return tab;
    }
    
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleCloseButtonAction(ActionEvent event) {
        closeWindow(event);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedNNComboBox.setConverter(converter);
        selectedNNComboBox.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    setChosenNetwork(newValue);
        });
    }
}