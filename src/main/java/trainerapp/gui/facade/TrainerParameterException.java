package trainerapp.gui.facade;

/**
 * An exception signalizing that creation of a {@code NeuralNetworkTrainer}
 * has failed because some input training parameters are invalid.
 * An instance of this class contains an {@code enum} parameter, which
 * determines the invalid parameter.
 * @author Konstantin Zhdanov
 */
public class TrainerParameterException extends Exception {

    private static final long serialVersionUID = 2531878857294009332L;
    
    /**
     * Type of parameter that was the source of raising an instance of this exception
     */
    public enum Parameter {
        EPOCH,
        PERFORMANCE
    }
    
    private final Parameter source;
    
    public TrainerParameterException(Parameter source) {
        this("", source);
    }
    
    public TrainerParameterException(String msg, Parameter source) {
        super(msg);
        this.source = source;
    }
    
    public Parameter getSource() {
        return source;
    }
}
