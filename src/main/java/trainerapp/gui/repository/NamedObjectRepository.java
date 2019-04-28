package trainerapp.gui.repository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Repository for storing objects and their names, accessing objects by
 * their names and getting the name of an object.
 * @param <T> Type of objects to store in the repository.
 * @author Konstantin Zhdanov
 */
public class NamedObjectRepository<T> {
    private final Map<String, T> nameObjectMap;
    private final ObservableList<String> names;
    private final ObservableList<T> objects;
    
    public NamedObjectRepository() {
        nameObjectMap = new LinkedHashMap<>();
        names = FXCollections.observableArrayList();
        objects = FXCollections.observableArrayList();
    }
    
    // O(n) time due to removal if it already contains the name
    // Replace if already contains the name
    public void add(String name, T object) {
        if (name == null || object == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        T prev = nameObjectMap.put(name, object);
        if (prev != null) {
            objects.remove(prev);
        }
        else {
            names.add(name);
        }
        objects.add(object);
    }
    
    // O(n) time
    // boolean result
    public boolean remove(String name) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        T obj = nameObjectMap.get(name);
        if (obj == null) {
            return false;
        }
        if (nameObjectMap.remove(name) == null) {
            return false;
        }
        names.remove(name);
        objects.remove(obj);
        return true;
    }
    
    // O(1) time
    // null if not exist
    public T get(String name) {
        return nameObjectMap.get(name);
    }
    
    // O(n) operation
    // null if not exist
    public String getNameForObject(T object) {
        for (String name : getNames()) {
            if (object.equals(get(name))) {
                return name;
            }
        }
        return null;
    }
    
    // O(1) time
    public boolean containsName(String name) {
        return nameObjectMap.containsKey(name);
    }
    
    // O(1) time
    public int size() {
        return nameObjectMap.size();
    }
    
    // O(1) time
    public boolean isEmpty() {
        return nameObjectMap.size() == 0;
    }
    
    // O(1) time
    public Set<String> getNames() {
        return Collections.unmodifiableSet(nameObjectMap.keySet());
    }
    
    // O(1) time
    // unmodifiable view
    public ObservableList<String> getNamesObservableList() {
        return FXCollections.unmodifiableObservableList(names);
    }
    
    // O(1) time
    // unmodifiable view
    public ObservableList<T> getObjectsObservableList() {
        return FXCollections.unmodifiableObservableList(objects);
    }
}
