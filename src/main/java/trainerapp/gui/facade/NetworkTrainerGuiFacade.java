package trainerapp.gui.facade;

import trainerapp.gui.repository.SamplesRepository;
import neuralnetwork.NeuralNetwork;
import neuralnetwork.train.Listener;
import neuralnetwork.train.NeuralNetworkTrainer;
import neuralnetwork.train.TrainerEvent;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

/**
 * Facade for starting and stopping training from the JavaFX thread.
 * @author Konstantin Zhdanov
 */
public class NetworkTrainerGuiFacade {
    
    private final Listener trainingListener = new Listener() {
        @Override public void onTrainingComplete(final TrainerEvent event) {
            NetworkTrainerGuiFacade.this.onTrainingComplete(event);
        }

        @Override public void onTrainingCanceled(final TrainerEvent event) {
            NetworkTrainerGuiFacade.this.onTrainingCanceled(event);
        }

        @Override public void onTrainingEpochComplete(final TrainerEvent event) {
            NetworkTrainerGuiFacade.this.onTrainingEpochComplete(event);
        }
    };
    
    private final Consumer<TrainerEvent> defaultEventHandler = (event) -> {};
    
    private Consumer<TrainerEvent> trainingCompleteEventHandler = defaultEventHandler;
    
    private Consumer<TrainerEvent> trainingCanceledEventHandler = defaultEventHandler;
    
    private Consumer<TrainerEvent> trainingEpochCompleteEventHandler = defaultEventHandler;
    
    private final BooleanProperty trainingActiveProperty;
    
    private NeuralNetworkTrainer trainer;
    
    private int lastNEpoch;
    
    private double lastPerformanceGoal;
    
    private int maxEpochUpdateNumber;
    
    private volatile int periodBetweenUpdates;
            
    public NetworkTrainerGuiFacade() {
        trainingActiveProperty = new SimpleBooleanProperty(false);
        lastNEpoch = 0;
        lastPerformanceGoal = Double.POSITIVE_INFINITY;
        maxEpochUpdateNumber = -1;
        periodBetweenUpdates = 1;
    }
    
    
    /*
     *********************************************
     ************* Training methods **************
     *********************************************/
    
    public void startTraining(int nEpoch, double performanceGoal, 
            NeuralNetwork nn, SamplesRepository<Double> samplesRepo) 
                throws TrainerParameterException {
        if (trainer != null) {
            trainer.removeListener(trainingListener);
        }
        
        trainer = buildTrainer(nEpoch, performanceGoal);

        lastNEpoch = nEpoch;
        lastPerformanceGoal = performanceGoal;
        if (    maxEpochUpdateNumber < 0 ||
                maxEpochUpdateNumber >= lastNEpoch   ) {
            periodBetweenUpdates = 1;
        }
        else {
            periodBetweenUpdates = lastNEpoch / maxEpochUpdateNumber;
        }
        
        final int numSamples = samplesRepo.size();
        final int inputSize = nn.getNumberInputs();
        double[][] inputs = new double[numSamples][inputSize];
        double[][] targets = new double[numSamples][nn.getNumberOutputs()];

        for (int i = 0; i < numSamples; i++) {
            ObservableList<Double> sample = samplesRepo.getSample(i);
            for (int j = 0; j < inputs[i].length; j++) {
                inputs[i][j] = sample.get(j);
            }
            for (int j = 0; j < targets[i].length; j++) {
                targets[i][j] = sample.get(inputSize + j);
            }
        }
        startTraining(nn, inputs, targets);
    }
    
    private void startTraining(NeuralNetwork network, double[][] inputs, 
            double[][] targets) {
        trainer.registerListener(trainingListener);
        trainer.startTrain(network, inputs, targets);
        setTrainingActive(true);
    }
    
    public void stopTraining() {
        if (trainer == null) {
            return;
        }
        trainer.stopTraining();
        setTrainingActive(false);
    }
    
    /**
     * Retrieve the training result, blocking if necessary.
     * @return A trained neural network.
     */
    public NeuralNetwork getTrainedNetwork() {
        if (trainer == null) {
            return null;
        }
        return trainer.getTrainedNetwork();
    }
    
    private NeuralNetworkTrainer buildTrainer(int nEpoch, 
            double performanceGoal) throws TrainerParameterException {

        NeuralNetworkTrainer.Builder builder = new NeuralNetworkTrainer.Builder();
        try { 
            builder.withMaxEpoch(nEpoch);
        }
        catch(IllegalArgumentException e) {
            throw new TrainerParameterException(TrainerParameterException.
                    Parameter.EPOCH);
        }
        try {
            builder.withPerformanceGoal(performanceGoal);
        }
        catch(IllegalArgumentException e) {
            throw new TrainerParameterException(TrainerParameterException.
                    Parameter.PERFORMANCE);
        }
        NeuralNetworkTrainer newTrainer = builder.build();
        
        return newTrainer;
    }
    
    /*
     *********************************************
     *********** Notification parameters *********
     *********************************************/
    
    public void setMaxNumberEpochCompleteEvents(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Maximum number must be positive");
        }
        this.maxEpochUpdateNumber = max;
    }
    
    public void resetMaxNumberEpochCompleteEvents() {
        
        this.maxEpochUpdateNumber = -1;
    }
    
    public int getPeriodBetweenEpochCompleteEvents() {
        return periodBetweenUpdates;
    }
    
    /*
     *********************************************
     ********* Last training parameters **********
     *********************************************/
    
    public int lastMaxEpoch() {
        return lastNEpoch;
    }
    
    public double lastPerformanceGoal() {
        return lastPerformanceGoal;
    }
    
    /*
     *********************************************
     ************* Boolean properties ************
     *********************************************/
    
    public BooleanProperty trainingActiveProperty() {
        return trainingActiveProperty;
    }
    
    public void setTrainingActive(boolean value) {
        trainingActiveProperty.set(value);
    }
    
    public boolean getTrainingActive() {
        return trainingActiveProperty.get();
    }
    
    /*
     *********************************************
     **************** Event handlers *************
     *********************************************/
   
    public void setOnTrainingComplete(Consumer<TrainerEvent> eventHandler) {
        this.trainingCompleteEventHandler = eventHandler;
    }
    
    private void onTrainingComplete(final TrainerEvent event) {
        Platform.runLater(() -> {
            setTrainingActive(false);
            trainingCompleteEventHandler.accept(event);
        });
    }
    
    public void setOnTrainingCanceled(Consumer<TrainerEvent> eventHandler) {
        this.trainingCanceledEventHandler = eventHandler;
    }
    
    private void onTrainingCanceled(final TrainerEvent event) {
        Platform.runLater(() -> {
            setTrainingActive(false);
            trainingCanceledEventHandler.accept(event);
        });
    }
    
    public void setOnTrainingEpochComplete(Consumer<TrainerEvent> eventHandler) {
        this.trainingEpochCompleteEventHandler = eventHandler;
    }
    
    private void onTrainingEpochComplete(final TrainerEvent event) {
        if (event.getEpoch() % periodBetweenUpdates != 0) {
            return;
        }
        Platform.runLater(() -> {
            trainingEpochCompleteEventHandler.accept(event);
        });
    }
    
    
}
