package trainerapp.gui.model;

import neuralnetwork.NeuralNetwork;
import java.io.Serializable;

/**
 * A neural network that contains a name associated with it.
 * @author Konstantin Zhdanov
 */
public class NamedNeuralNetwork extends NeuralNetwork implements Serializable {

    private static final long serialVersionUID = -6259252896064493552L;
    
    /** Name of this Neural Network **/
    private String name;
    
    public NamedNeuralNetwork(int numInputs, int[] hiddenLayerSizes, int numOutputs, String name) {
        super(numInputs, hiddenLayerSizes, numOutputs);
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        this.name = name;
    }

    public NamedNeuralNetwork(NeuralNetwork nn, String name) {
        super(nn);
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        this.name = name;
    }
    
    public NamedNeuralNetwork(NamedNeuralNetwork nn) {
        this(nn, nn.getName());
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        this.name = name;
    }
    
    /**
     * Convert to {@code String} containing the name and the structure of the network
     * @return {@code String} containing the name and the structure of the network
     */
    @Override
    public String toString() {
        String nnStructure = super.toString();
        return String.format("%s %s", name, nnStructure);
    }
}
