package trainerapp.gui.util;

import neuralnetwork.NeuralNetwork;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

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
