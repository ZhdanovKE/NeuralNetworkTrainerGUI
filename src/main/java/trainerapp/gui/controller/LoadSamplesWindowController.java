package trainerapp.gui.controller;

import trainerapp.gui.facade.NumberTableViewFacade;
import trainerapp.gui.facade.TextFieldErrorMessageFacade;
import trainerapp.gui.repository.NamedObjectRepository;
import trainerapp.gui.repository.SamplesRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

/**
 * Load Samples Window Controller class
 *
 * @author Konstantin Zhdanov
 */
public class LoadSamplesWindowController implements Initializable {
    
    private static final String DELIMETER = ",";

    @FXML
    private Button removeSampleButton;

    @FXML
    private TableView<ObservableList<Double>> samplesTableView;
    private NumberTableViewFacade<Double> samplesTableViewFacade;

    @FXML
    private Button loadButton;

    @FXML
    private Button addSampleButton;

    @FXML
    private TextField filenameField;
    private TextFieldErrorMessageFacade filenameFieldErrorFacade;
    
    @FXML
    private TextField nameField;
    private TextFieldErrorMessageFacade nameFieldErrorFacade;
    
    private SimpleBooleanProperty samplesLoadedProperty;
    
    private NamedObjectRepository<SamplesRepository<Double>> samplesRepoRepository;

    private SamplesRepository<Double> samples;
    
    private List<String> samplesHeader;
            
    private void clearLoadedSamples() {
        samplesTableViewFacade.clear();
    }
    
    private void clearErrorMessages() {
        filenameFieldErrorFacade.hideError();
        nameFieldErrorFacade.hideError();
    }
    
    public void setSamplesRepository(NamedObjectRepository<SamplesRepository<Double>> repo) {
        if (repo == null) {
            throw new NullPointerException("Repository cannot be null");
        }
        this.samplesRepoRepository = repo;
    }
    
    // Choose a CSV file and load it
    @FXML
    void handleChooseFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
        fileChooser.setTitle("Choose a CSV file");
        File chosenFile = fileChooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        if (chosenFile == null) {
            return;
        }
        nameField.setText(extractFileName(chosenFile.getName()));
        filenameField.setText(chosenFile.getAbsolutePath());
        clearErrorMessages();
        clearLoadedSamples();
        loadSamplesFromFile(chosenFile);
        setSamplesLoaded(true);
    }
    
    @FXML
    void handleFilenameFieldAction(ActionEvent event) {
        TextField source = (TextField)event.getSource();
        String filename = source.getText();
        File file = new File(filename);
        clearErrorMessages();
        clearLoadedSamples();
        if (!file.exists()) {
            setSamplesLoaded(false);
            filenameFieldErrorFacade.showError("The file doesn't exist.\n Choose another file.");
            nameField.setText("");
        }
        else {
            loadSamplesFromFile(file);
            nameField.setText(extractFileName(file.getName()));
            setSamplesLoaded(true);
        }
    }
    
    private String extractFileName(String namePlusExtension) {
        int lastDotIdx = namePlusExtension.lastIndexOf(".");
        if (lastDotIdx == -1) {
            return namePlusExtension;
        }
        return namePlusExtension.substring(0, lastDotIdx);
    }
    
    // Parse a CSV file line. 
    // Return null if cannot be parsed
    private ObservableList<Double> parseLine(String line) {
        String[] lineValues = line.split(DELIMETER);
        ObservableList<Double> sample;
        try {
            sample = FXCollections.observableArrayList();
            for (String value : lineValues) {
                double doubleValue = Double.parseDouble(value.trim());
                sample.add(doubleValue);
            }
        }
        catch (NumberFormatException e) {
            // error parsing the line
            sample = null;
        }
        return sample;
    }
    
    // extract header titles from a CSV line
    private String[] extractHeaderTitles(String line) {
        String[] lineValues = line.split(DELIMETER);
        String[] headerValues = new String[lineValues.length];
        for (int headerColNum = 0; headerColNum < headerValues.length; headerColNum++) {
            headerValues[headerColNum] = lineValues[headerColNum].trim();
        }
        return headerValues;
    }
    
    // create default header titles
    private String[] createDefaultHeaderTitles(int headerSize) {
        String[] headerValues = new String[headerSize];
        for (int valueNum = 0; valueNum < headerValues.length; valueNum++) {
            headerValues[valueNum] = "Var " + (valueNum + 1);
        }
        return headerValues;
    }
    
    // create columns and set the titles
    private void createHeaderColumns(String[] headerValues) {
        for (String value : headerValues) {
            samplesTableViewFacade.addColumn(value);
        }
    }
    
    // load samples one by one from the BufferedReader
    // invalid samples are discarded
    private void loadSamples(BufferedReader bufReader) throws IOException {
        String line;
        ObservableList<Double> sample;
        final int expectedSize = samplesTableViewFacade.getColumnsCount();
        while ((line = bufReader.readLine()) != null) {
            sample = parseLine(line);
            if (sample == null) {
                // skipping...
            }
            else if (sample.size() != expectedSize) {
                // wrong size => skipping...
            }
            else {
                samples.add(sample);
            }
        }
    }
    
    // load a CSV file
    private void loadSamplesFromFile(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            try (BufferedReader bufReader = new BufferedReader(fileReader)) {
                String line = bufReader.readLine();
                if (line == null) {
                    // file is empty
                    return;
                }
                String[] headerTitles;
                ObservableList<Double> sample = parseLine(line);
                if (sample == null) {
                    // the first line is the header
                    headerTitles = extractHeaderTitles(line);
                }
                else {
                    // there's no header
                    headerTitles = createDefaultHeaderTitles(sample.size());
                    samples.add(sample);
                }
                samplesHeader = Arrays.asList(headerTitles);
                samples.setHeader(samplesHeader);
                createHeaderColumns(headerTitles);
                loadSamples(bufReader);
            }
        }
        catch (IOException e) {
           
        }
    }

    @FXML
    void handleLoadButtonAction(ActionEvent event) {
        String name = nameField.getText().trim();
        if (samples.size() == 0) {
            filenameFieldErrorFacade.showError("Samples are empty.\n Please upload a non-empty file.");
            return;
        }
        if (name.length() < 1) {
            nameFieldErrorFacade.showError("Name cannot be empty.\nChoose another name.");
            return;
        }
        else if (samplesRepoRepository.containsName(name)) {
            nameFieldErrorFacade.showError("Some samples have been already loaded with the same name.\nPlease choose another name.");
            return;
        }
        samplesRepoRepository.add(name, samples);
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void handleCancelButtonAction(ActionEvent event) {
        closeWindow(event);
    }

    @FXML
    void handleAddSampleButtonAction(ActionEvent event) {
        ObservableList<Double> newSample = FXCollections.observableArrayList();
        final int size = samplesTableViewFacade.getColumnsCount();
        for (int i = 0; i < size; i++) {
            newSample.add(0.0);
        }
        samples.add(newSample);
    }

    @FXML
    void handleRemoveSampleButtonAction(ActionEvent event) {
        if (samples.size() == 1) {
            return;
        }
        samples.remove(samples.size() - 1);
    }
    
    private boolean getSamplesLoaded() {
        return samplesLoadedProperty.getValue();
    }
    
    private void setSamplesLoaded(boolean value) {
        samplesLoadedProperty.setValue(value);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        samples = new SamplesRepository<Double>();
        samplesTableViewFacade = new NumberTableViewFacade<>(samplesTableView, 
            new DoubleStringConverter());
        samplesTableViewFacade.setItems(samples.getAll());
        filenameFieldErrorFacade = new TextFieldErrorMessageFacade(filenameField);
        nameFieldErrorFacade = new TextFieldErrorMessageFacade(nameField);
        samplesLoadedProperty = new SimpleBooleanProperty(false);
        loadButton.disableProperty().bind(samplesLoadedProperty.not());
        addSampleButton.disableProperty().bind(samplesLoadedProperty.not());
        removeSampleButton.disableProperty().bind(samplesLoadedProperty.not());
        
    }    
    
}
