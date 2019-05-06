package trainerapp.gui.util;

import neuralnetwork.NeuralNetwork;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Collectors;
import trainerapp.gui.model.NamedNeuralNetwork;

/**
 * Helper class for serializing and de-serializing {@code NeuralNetwork} objects
 * to/from files.
 * @author Konstantin Zhdanov
 */
public class NeuralNetworkLoader {
    
    public void save(NeuralNetwork nn, String fileName) {
        if (nn == null || fileName == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        File file = new File(fileName);
        try (OutputStream out = new FileOutputStream(file)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
                oos.writeObject(nn);
            }
        }
        catch(IOException e) {
            throw new IllegalArgumentException("Cannot write into file", e);
        }
    }
    
    public void saveAsText(NeuralNetwork nn, String fileName) {
        if (nn == null || fileName == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        File file = new File(fileName);
        try (Writer out = new FileWriter(file)) {
            if (nn instanceof NamedNeuralNetwork) {
                // write name
                out.write(((NamedNeuralNetwork) nn).getName());
                out.write("\n");
            }
            // write signature
            String hiddenSizes = Arrays.stream(nn.getHiddenLayerSizes()).
                    mapToObj(v -> String.valueOf(v)).collect(
                            Collectors.joining(", "));
            String signature = String.format("%d, %s, %d", nn.getNumberInputs(),
                    hiddenSizes, nn.getNumberOutputs());
            out.write(signature);
            out.write("\n");

            int curLayerIdx = 0;
            int prevLayerSize = nn.getNumberInputs();
            int curLayerSize = nn.getHiddenLayerSize(curLayerIdx);

            // input <--> layer 1
            String layerStr = layerToString(nn, curLayerIdx, 
                    curLayerSize, prevLayerSize);
            out.write(layerStr);
            out.write("\n");

            // hidden layers
            for (curLayerIdx = 1; curLayerIdx < nn.getNumberHiddenLayers(); curLayerIdx++) {
                prevLayerSize = nn.getHiddenLayerSize(curLayerIdx - 1);
                curLayerSize = nn.getHiddenLayerSize(curLayerIdx);
                layerStr = layerToString(nn, curLayerIdx, 
                    curLayerSize, prevLayerSize);
                out.write(layerStr);
                out.write("\n");
            }
            
            // last layer <--> output
            prevLayerSize = nn.getHiddenLayerSize(nn.getNumberHiddenLayers() - 1);
            curLayerSize = nn.getNumberOutputs();
            curLayerIdx = nn.getNumberHiddenLayers();
            layerStr = layerToString(nn, curLayerIdx, 
                curLayerSize, prevLayerSize);
            out.write(layerStr);
            out.flush();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Cannot write into file", e);
        }
    }
    
    private String layerToString(NeuralNetwork nn, int layerNum, int layerSize, 
            int prevLayerSize) {
        StringBuilder sb = new StringBuilder();
        for (int prevNeuron = 0; prevNeuron < prevLayerSize - 1; prevNeuron++) {
            for (int curNeuron = 0; curNeuron < layerSize - 1; curNeuron++) {
                sb.append(nn.getWeight(layerNum, prevNeuron, curNeuron));
                sb.append(" ");
            }
            sb.append(nn.getWeight(layerNum, prevNeuron, layerSize - 1));
            sb.append("\n");
        }
        
        // last neuron of layerNum - 1
        for (int curNeuron = 0; curNeuron < layerSize - 1; curNeuron++) {
            sb.append(nn.getWeight(layerNum, prevLayerSize - 1, curNeuron));
            sb.append(" ");
        }
        sb.append(nn.getWeight(layerNum, prevLayerSize - 1, layerSize - 1));
        sb.append("\n");
        
        // biases
        for (int curNeuron = 0; curNeuron < layerSize - 1; curNeuron++) {
            sb.append(nn.getBias(layerNum, curNeuron));
            sb.append(" ");
        }
        sb.append(nn.getBias(layerNum, layerSize - 1));
        
        return sb.toString();
    }
    
    public NeuralNetwork load(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        NeuralNetwork nn;
        File file = new File(fileName);
        try (InputStream in = new FileInputStream(file)) {
            try (ObjectInputStream oos = new ObjectInputStream(in)) {
                nn = (NeuralNetwork)oos.readObject();
            }
        }
        catch(IOException e) {
            throw new IllegalArgumentException("Cannot read from file", e);
        }
        catch(ClassNotFoundException | ClassCastException e) {
            throw new IllegalArgumentException("Wrong file format", e);
        }
        return nn;
    }
}
