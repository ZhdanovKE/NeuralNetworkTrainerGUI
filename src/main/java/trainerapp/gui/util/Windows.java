package trainerapp.gui.util;

import trainerapp.gui.controller.CreateNNWindowController;
import trainerapp.gui.controller.LoadSamplesWindowController;
import trainerapp.gui.controller.TestNNWindowController;
import trainerapp.gui.controller.TrainNNWindowController;
import trainerapp.gui.repository.NamedObjectRepository;
import trainerapp.gui.repository.SamplesRepository;
import neuralnetwork.NeuralNetwork;
import javafx.stage.Window;
import trainerapp.gui.ModalWindow;
import trainerapp.gui.controller.ViewNNWindowController;

/**
 * Utility class for creating and showing different modal windows related
 * to this application.
 * 
 * @author Konstantin Zhdanov
 */
public class Windows {
    
    public static void showCreateNetworkWindow(Window parent,
            NamedObjectRepository<NeuralNetwork> nnRepository) {
        ModalWindow window = new ModalWindow("/fxml/CreateNNWindow.fxml", 
                    "Create Neural Network", parent);
        CreateNNWindowController controller = 
                (CreateNNWindowController)window.getController();
        controller.setNetworkRepository(nnRepository);

        window.show();
    }
    
    public static void showLoadSamplesWindow(Window parent, 
            NamedObjectRepository<SamplesRepository<Double>> samplesRepoRepository) {
        
        ModalWindow window = new ModalWindow("/fxml/LoadSamplesWindow.fxml", 
                    "Load Samples", parent);
        LoadSamplesWindowController controller =  
                (LoadSamplesWindowController)window.getController();
        controller.setSamplesRepository(samplesRepoRepository);

        window.show();
    }
    
    public static void showTrainNetworkWindow(Window parent, 
            NamedObjectRepository<NeuralNetwork> nnRepository, 
            NeuralNetwork selectedNN, 
            NamedObjectRepository<SamplesRepository<Double>> samplesRepoRepository,
            SamplesRepository<Double> selectedSamplesRepo) {
        
        ModalWindow window = new ModalWindow("/fxml/TrainNNWindow.fxml", 
                "Train Neural Network", parent);

        TrainNNWindowController controller = 
                (TrainNNWindowController)window.getController();

        controller.setNetworkRepository(nnRepository);
        controller.selectNetwork(selectedNN);
        
        controller.setSamplesRepository(samplesRepoRepository);
        if (selectedSamplesRepo != null) {
            controller.selectSamplesRepo(selectedSamplesRepo);
        }
        
        window.show();
    }
    
    public static void showTestNetworkWindow(Window parent, 
            NamedObjectRepository<NeuralNetwork> nnRepository, 
            NeuralNetwork selectedNN) {
        
        ModalWindow window = new ModalWindow("/fxml/TestNNWindow.fxml", 
                "Test Neural Network", parent);

        TestNNWindowController controller = 
                (TestNNWindowController)window.getController();
        controller.setNetworkRepository(nnRepository);
        controller.selectNetwork(selectedNN);
        window.show();   
    }
    
    public static void showViewNetworkWindow(Window parent, 
            NamedObjectRepository<NeuralNetwork> nnRepository,
            NeuralNetwork selectedNN) {
        ModalWindow window = new ModalWindow("/fxml/ViewNNWindow.fxml", 
                "View Neural Network", parent);

        ViewNNWindowController controller = 
                (ViewNNWindowController)window.getController();
        controller.setNetworkRepository(nnRepository);
        controller.setNetwork(selectedNN);
        window.show();   
    }
}
