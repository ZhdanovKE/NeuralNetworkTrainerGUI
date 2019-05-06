package trainerapp.gui.controller;

import trainerapp.gui.model.NamedNeuralNetwork;
import trainerapp.gui.facade.NetworkTrainerGuiFacade;
import trainerapp.gui.facade.TrainerParameterException;
import trainerapp.gui.util.Windows;
import trainerapp.gui.facade.TextFieldErrorMessageFacade;
import trainerapp.gui.repository.NamedObjectRepository;
import trainerapp.gui.repository.SamplesRepository;
import neuralnetwork.NeuralNetwork;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import trainerapp.gui.facade.ComboBoxRepositoryFacade;
import trainerapp.gui.repository.FilteredNamedObjectRepository;

/**
 * Train Neural Network Window Controller class
 *
 * @author Konstantin Zhdanov
 */
public class TrainNNWindowController implements Initializable {

    @FXML
    private Label nSamplesVarsLabel;
    
    @FXML
    private ProgressBar trainingProgressBar;

    @FXML
    private Label trainedPerformanceLabel;

    @FXML
    private LineChart<Integer, Double> performanceLineChart;
    
    @FXML
    private Button startStopTrainButton;
    
    @FXML
    private Button saveCloseButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private ComboBox<NeuralNetwork> nnComboBox;
    private ComboBoxRepositoryFacade<NeuralNetwork> nnComboBoxFacade;
    
    @FXML
    private ComboBox<SamplesRepository<Double>> samplesComboBox;
    private ComboBoxRepositoryFacade<SamplesRepository<Double>> samplesComboBoxFacade;
    
    @FXML
    private Button addSampleButton;

    @FXML
    private TextField nEpochsField;
    private TextFieldErrorMessageFacade nEpochsFieldErrorFacade;
    
    @FXML
    private TextField performanceGoalField;
    private TextFieldErrorMessageFacade performanceGoalFieldErrorFacade;
    
    @FXML
    private TextField newNameField;
    private TextFieldErrorMessageFacade newNameFieldErrorFacade;
    
    private final NetworkTrainerGuiFacade trainerFacade;
    
    private List<Double> performanceEpochList;
    
    private final int maxNumPoints = 30;
    
    private NamedObjectRepository<NeuralNetwork> nnRepository;
    private final SimpleBooleanProperty networkChosen;
    
    private NamedObjectRepository<SamplesRepository<Double>> samplesRepoRepository;
    private final SimpleBooleanProperty samplesRepoChosen;
    
    private final SimpleBooleanProperty trainingCanStart;
    
    // if the chosen network has been trained
    private final SimpleBooleanProperty networkHasBeenTrained;
    
    private final SimpleBooleanProperty networkHasBeenSaved;
    
    public TrainNNWindowController() {
        
        performanceEpochList = new ArrayList<>();
        
        networkChosen = new SimpleBooleanProperty(false);
        
        samplesRepoChosen = new SimpleBooleanProperty(false);
        
        trainingCanStart = new SimpleBooleanProperty(false);
        
        networkHasBeenSaved = new SimpleBooleanProperty(false);
        
        networkHasBeenTrained = new SimpleBooleanProperty(false);
        
        trainerFacade = new NetworkTrainerGuiFacade();
        trainerFacade.setOnTrainingComplete((t) -> {
            trainingProgressBar.setProgress(1.0);
            setFinalPerformance(t.getPerformance());
            setUpPerformanceChart();
            setNetworkHasBeenSaved(false);
            setNetworkHasBeenTrained(true);
        });
        trainerFacade.setOnTrainingCanceled((t) -> {
            trainingProgressBar.setProgress(0);
            setFinalPerformance(t.getPerformance());
            setUpPerformanceChart();
            setNetworkHasBeenSaved(false);
            setNetworkHasBeenTrained(true);
        });
        trainerFacade.setOnTrainingEpochComplete((t) -> {
            double progress = ((double)t.getEpoch()) / trainerFacade.lastMaxEpoch();
            trainingProgressBar.setProgress(progress);
            performanceEpochList.add(t.getPerformance());
        });
        trainerFacade.setMaxNumberEpochCompleteEvents(maxNumPoints);
    }
    
    public void setNetworkRepository(NamedObjectRepository<NeuralNetwork> nnRepository) {
        this.nnRepository = nnRepository;
        nnComboBoxFacade.setRepository(this.nnRepository);
    }

    public void selectNetwork(NeuralNetwork selectedNN) {
        nnComboBoxFacade.select(selectedNN);
    }
    
    public void setSamplesRepository(NamedObjectRepository<SamplesRepository<Double>> samplesRepo) {
        if (samplesRepo == null) {
            throw new NullPointerException("Samples repository cannot be null");
        }
        this.samplesRepoRepository = samplesRepo;
        updateValidSamplesReposList();
    }

    public void selectSamplesRepo(SamplesRepository<Double> selectedSamplesRepo) {
        // if selected before Network, discard change
        if (    nnComboBoxFacade == null ||
                nnComboBoxFacade.getSelectedItem() == null  ) {
            return;
        }
        // if network has been selected, and this repo isn't compatible, discard
        if (selectedSamplesRepo.sampleSize() != getRequiredSampleSize(
                nnComboBoxFacade.getSelectedItem())) {
            return;
        }
        samplesComboBoxFacade.select(selectedSamplesRepo);
    }
    
    @FXML
    void handleAddSampleButtonAction(ActionEvent event) {
        Window thisWindow = ((Node)event.getSource()).getScene().getWindow();
        Windows.showLoadSamplesWindow(thisWindow, samplesRepoRepository);
        updateValidSamplesReposList();
    }
    
    @FXML
    void handleStartStopTrainingButtonAction(ActionEvent event) {
        if (!getTrainingCanStart()) {
            return;
        }
        if (trainerFacade.getTrainingActive()) {
            trainerFacade.stopTraining();
        }
        else {
            clearTrainingInfo();
            NeuralNetwork network = nnComboBoxFacade.getSelectedItem();
            int nEpoch;
            try {
                nEpoch = Integer.parseInt(nEpochsField.getText());
                nEpochsFieldErrorFacade.hideError();
            }
            catch (NumberFormatException e) {
                nEpochsFieldErrorFacade.showError(
                    "Please enter an integer number greater than 0.");
                return;
            }
            double performanceGoal;
            try {
                performanceGoal = Double.parseDouble(
                        performanceGoalField.getText());
                performanceGoalFieldErrorFacade.hideError();
            }
            catch (NumberFormatException e) {
                performanceGoalFieldErrorFacade.showError(
                    "Please enter a decimal number greater than 0.");
                return;
            }
            try {
                trainerFacade.startTraining(nEpoch, performanceGoal, 
                        network, samplesComboBoxFacade.getSelectedItem());
            }
            catch(TrainerParameterException e) {
                switch(e.getSource()) {
                    case EPOCH:
                        nEpochsFieldErrorFacade.showError(
                                "Please enter an integer number greater than 0.");
                        break;
                    case PERFORMANCE:
                        performanceGoalFieldErrorFacade.showError(
                                "Please enter a decimal number greater than 0.");
                        break;
                }
            }
        }
    }
    
    @FXML
    void handleSaveAndCloseButtonAction(ActionEvent event) {
        if (trySaveNetworkFromTrainer()) {
            closeWindow(event);
        }
    }
    
    @FXML
    void handleSaveButtonAction(ActionEvent event) {
        trySaveNetworkFromTrainer();
    }
    
    private boolean trySaveNetworkFromTrainer() {
        String newName = newNameField.getText().trim();
        if (newName.length() < 1) {
            newNameFieldErrorFacade.showError("Need to provide a non-empty name for the trained network.");
            newNameField.requestFocus();
            return false;
        }
        NeuralNetwork trainedNN = trainerFacade.getTrainedNetwork();
        NamedNeuralNetwork namedTrainedNN = new NamedNeuralNetwork(trainedNN, newName);
        if (nnRepository.containsName(newName)) {
            newNameFieldErrorFacade.showError("Network with the same name already exists.\nPlease choose another name.");
            newNameField.requestFocus();
            return false;
        }
        nnRepository.add(newName, namedTrainedNN);
        
        setNetworkHasBeenSaved(true);
        
        newNameFieldErrorFacade.hideError();
        return true;
    }
    
    @FXML
    void handleCancelButtonAction(ActionEvent event) {
        closeWindow(event);
    }
    
    private void closeWindow(ActionEvent event) {
        if (trainerFacade.getTrainingActive()) {
            trainerFacade.stopTraining();
        }
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    private boolean getTrainingCanStart() {
        return trainingCanStart.getValue();
    }
    
    private void setNetworkHasBeenSaved(boolean value) {
        networkHasBeenSaved.setValue(value);
    }
    
    private void setNetworkHasBeenTrained(boolean value) {
        networkHasBeenTrained.set(value);
    }
    
    private void setFinalPerformance(double performance) {
        trainedPerformanceLabel.setText(String.valueOf(performance));
    }
    
    private void clearFinalPerformance() {
        trainedPerformanceLabel.setText("");
    }
    
    private void setUpPerformanceChart() {
        if (performanceEpochList == null) {
            return;
        }
        ObservableList<XYChart.Data<Integer, Double>> chartDataList = 
                FXCollections.observableArrayList();
        int i = 1;
        for (Double value : performanceEpochList) {
            XYChart.Data<Integer, Double> elem = 
                    new XYChart.Data<>(i*trainerFacade.getPeriodBetweenEpochCompleteEvents(), 
                            value);
            chartDataList.add(elem);
            i++;
        }
        
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();
        series.setData(chartDataList);
        performanceLineChart.getData().clear();
        performanceLineChart.getData().<Integer, Double>add(series);
        series.getNode().setStyle("-fx-border-style: solid; -fx-stroke: blue;");
    }
    
    private void clearTrainingInfo() {
        trainingProgressBar.setProgress(0.0);
        performanceLineChart.getData().clear();
        performanceEpochList.clear();
        clearFinalPerformance();
    }
    
    private String getDefaultTrainedNameFor(NeuralNetwork nn) {
        String trainedName;
        if (nn instanceof NamedNeuralNetwork) {
            trainedName = ((NamedNeuralNetwork) nn).getName() + "_trained";
        }
        else {
            trainedName = nn == null ? "" : (nn.toString() + "_trained");
        }
        return trainedName;
    }
    
    private void setChosenNetwork(NeuralNetwork chosenNN) {
        setNetworkHasBeenSaved(false);
        setNetworkHasBeenTrained(false);
        
        clearTrainingInfo();
        if (trainerFacade.getTrainingActive()) {
            trainerFacade.stopTraining();
        }

        nSamplesVarsLabel.setText(chosenNN == null ? "" : 
                String.format(" (%d vars)",
                    getRequiredSampleSize(chosenNN)));
        newNameField.setText(getDefaultTrainedNameFor(chosenNN));
       
        if (samplesRepoRepository != null) {
            updateValidSamplesReposList();
        }
    }
    
    private void setChosenSamplesRepo(SamplesRepository<Double> samplesRepo) {
        clearTrainingInfo();
        if (trainerFacade.getTrainingActive()) {
            trainerFacade.stopTraining();
        }
    }
    
    private int getRequiredSampleSize(NeuralNetwork nn) {
        return nn == null ? 0 : nn.getNumberInputs() + nn.getNumberOutputs();
    } 
    
    private void updateValidSamplesReposList() {
        final int requiredSampleSize = getRequiredSampleSize(
                nnComboBoxFacade.getSelectedItem());
        
        FilteredNamedObjectRepository<SamplesRepository<Double>> 
                samplesRepoRepositoryFiltered = new FilteredNamedObjectRepository<>(
                    samplesRepoRepository, t -> 
                            t.sampleSize() == requiredSampleSize);

        samplesComboBoxFacade.setRepository(samplesRepoRepositoryFiltered);       
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        nnComboBox.disableProperty().bind(trainerFacade.trainingActiveProperty());
        nnComboBoxFacade  = new ComboBoxRepositoryFacade<>(nnComboBox,
                (t, s) -> t.toString());
        nnComboBoxFacade.setOnItemSelected(this::setChosenNetwork);
        
        networkChosen.bind(nnComboBox.valueProperty().isNotNull());
        
        samplesComboBox.disableProperty().bind(trainerFacade.trainingActiveProperty());
        samplesComboBoxFacade = new ComboBoxRepositoryFacade<>(samplesComboBox, 
            (t,s) -> String.format("%s (%d vars)", s, t.sampleSize()));
        samplesComboBoxFacade.setOnItemSelected(this::setChosenSamplesRepo);
        
        samplesRepoChosen.bind(samplesComboBox.valueProperty().isNotNull());

        trainingCanStart.bind(samplesRepoChosen.and(networkChosen));
        
        trainerFacade.trainingActiveProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                startStopTrainButton.setText("Stop Training");
            }
            else {
                startStopTrainButton.setText("Start Training");
            }
        });
        
        saveButton.disableProperty().bind(
                trainerFacade.trainingActiveProperty().or(
                        networkHasBeenSaved.or(trainingCanStart.not()).
                                or(networkHasBeenTrained.not())));
        saveCloseButton.disableProperty().bind(saveButton.disableProperty());
        startStopTrainButton.disableProperty().bind(trainingCanStart.not());
        addSampleButton.disableProperty().bind(trainerFacade.trainingActiveProperty());
        
        newNameField.setOnAction((event) -> {
            if (!saveButton.isDisable()) {
                trySaveNetworkFromTrainer();
            }
        });
        newNameFieldErrorFacade = new TextFieldErrorMessageFacade(newNameField);
        performanceGoalFieldErrorFacade = new TextFieldErrorMessageFacade(performanceGoalField);
        nEpochsFieldErrorFacade = new TextFieldErrorMessageFacade(nEpochsField);
       
        performanceLineChart.getStylesheets().add(this.getClass().
                getResource("/styles/LineChartStyle.css").toExternalForm());
    }    
    
}
