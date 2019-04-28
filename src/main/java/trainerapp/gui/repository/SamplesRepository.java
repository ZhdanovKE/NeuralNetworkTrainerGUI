package trainerapp.gui.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Repository for storing neural network's samples, containing numbers.
 * @param <T> A numerical type that the samples hold.
 * @author Konstantin Zhdanov
 */
public class SamplesRepository<T extends Number> {
    private final ObservableList<ObservableList<T>> items;
    
    public SamplesRepository() {
        items = FXCollections.observableArrayList();
    }
    
    public void add(ObservableList<T> sample) {
        if (sample == null) {
            throw new NullPointerException("Sample cannot be null");
        }
        items.add(sample);
    }
    
    public boolean remove(int idx) {
        return items.remove(idx) != null;
    }
    
    public ObservableList<T> getSample(int idx) {
        return items.get(idx);
    }
    
    public ObservableList<ObservableList<T>> getAll() {
        return items;
    }
    
    public int size() {
        return items.size();
    }
    
    public int sampleSize() {
        if (items.isEmpty()) {
            return 0;
        }
        return items.get(0).size();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
