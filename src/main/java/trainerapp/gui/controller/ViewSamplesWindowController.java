package trainerapp.gui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import trainerapp.gui.facade.ComboBoxRepositoryFacade;
import trainerapp.gui.repository.NamedObjectRepository;
import trainerapp.gui.repository.SamplesRepository;

/**
 * View Samples Window Controller class
 *
 * @author Konstantin Zhdanov
 */
public class ViewSamplesWindowController implements Initializable {

    @FXML
    private TableView<ObservableList<Double>> samplesTableView;

    @FXML
    private ComboBox<SamplesRepository<Double>> samplesComboBox;
    private ComboBoxRepositoryFacade<SamplesRepository<Double>> samplesComboBoxFacade;
    
    private NamedObjectRepository<SamplesRepository<Double>> samplesRepoRepository;
    
    public void setSamplesRepository(NamedObjectRepository<SamplesRepository<Double>> repo) {
        if (repo == null) {
            throw new NullPointerException("Repository cannot be null");
        }
        this.samplesRepoRepository = repo;
        samplesComboBoxFacade.setRepository(this.samplesRepoRepository);
    }
    
    public void selectSamples(SamplesRepository<Double> selectedSamplesRepo) {
        samplesComboBoxFacade.select(selectedSamplesRepo);
    }
    
    private void setChosenRepo(SamplesRepository<Double> selectedSamplesRepo) {
        updateSamplesTable();
    }
    
    private void updateSamplesTable() {
        samplesTableView.getItems().clear();
        samplesTableView.getColumns().clear();
        
        if (samplesComboBoxFacade.getSelectedItem() == null) {
            return;
        }
        
        // Create colums
        createSamplesTableColumns();
        
        // Load values
        samplesTableView.getItems().addAll(samplesComboBoxFacade.
                getSelectedItem().getAll());
    }
    
    private void createSamplesTableColumns() {
        int nColumns = samplesComboBoxFacade.getSelectedItem().sampleSize();
        List<String> header = samplesComboBoxFacade.getSelectedItem().getHeader();
        for (int columnNum = 0; columnNum < nColumns; columnNum++) {
            final int varIdx = columnNum;
            TableColumn<ObservableList<Double>, Double> column = 
                    new TableColumn<>(header.get(varIdx));
            column.setCellValueFactory((param) -> {
                return new ReadOnlyObjectWrapper<>(param.getValue().get(varIdx));
            });
            samplesTableView.getColumns().add(column);
        }
    }
    
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void handleCloseButtonHandler(ActionEvent event) {
        closeWindow(event);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        samplesComboBoxFacade = new ComboBoxRepositoryFacade<>(samplesComboBox,
                (t, s) -> String.format("%s (%d vars)", 
                            s, t.sampleSize()));
        samplesComboBoxFacade.setOnItemSelected(this::setChosenRepo);
    }    
    
}
