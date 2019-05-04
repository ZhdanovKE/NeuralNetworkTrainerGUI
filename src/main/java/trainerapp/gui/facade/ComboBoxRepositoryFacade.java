package trainerapp.gui.facade;

import java.util.function.Consumer;
import java.util.function.Function;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import trainerapp.gui.repository.NamedObjectRepository;

/**
 * A Facade for setting up a {@code ComboBox} to hold objects of type {@link T} 
 * from {@code NamedObjectRepository} and inform the outer code if the selected 
 * item changes.
 * @param <T> Type of elements that the underlying {@code ComboBox} and
 * {@code NamedObjectRepository} hold.
 * @author Konstantin Zhdanov
 */
public class ComboBoxRepositoryFacade<T> {
    private final ComboBox<T> comboBox;
    private NamedObjectRepository<T> repo;
    
    private T selectedItem;
    
    private Consumer<T> onItemSelectedHandler = (t) -> {};
    private final Function<T,String> stringConverter;

    /**
     * Create the facade for the passed {@link comboBox} and use the 
     * {@link stringConverter} to show the items of type {@code T} in
     * the {@link comboBox}.
     * @param comboBox A {@code ComboBox} that will be controlled.
     * @param stringConverter A {@code Function<T,String>} to be used to convert
     * items of type {@code T} to String to be showed in the {@link comboBox}.
     * @throws NullPointerException if either {@link comboBox} or {@link stringConverter}
     * is null.
     */
    public ComboBoxRepositoryFacade(ComboBox<T> comboBox, 
            Function<T,String> stringConverter) {
        if (comboBox == null || stringConverter == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        this.comboBox = comboBox;
        this.stringConverter = stringConverter;
        this.comboBox.setConverter(new StringConverter<T>() {
            @Override public String toString(T object) {
                return ComboBoxRepositoryFacade.this.
                        stringConverter.apply(object);
            }
            @Override public T fromString(String string) {
                throw new UnsupportedOperationException("Not supported.");
            }
        });
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    onItemSelectedHandler.accept(newValue);
        });
    }
    
    /**
     * Set the {@code NamedObjectRepository<T>} repository holding the items
     * that the underlying {@code ComboBox} will hold.
     * @param repository An instance of {@code NamedObjectRepository<T>} holding
     * items of type {@code T}.
     * @throws NullPointerException if {@link repository} is null.
     */
    public void setRepository(NamedObjectRepository<T> repository) {
        if (repository == null) {
            throw new NullPointerException("Repository cannot be null");
        }
        repo = repository;
        onRepositoryUpdate();
    }
    
    private void onRepositoryUpdate() {
        selectedItem = null;
        comboBox.setItems(repo.getObjectsObservableList());
    }
    
    /**
     * Select the passed {@link item} in the underlying {@code ComboBox}.
     * @param item The item to be selected in the {@code ComboBox}. 
     */
    public void select(T item) {
       comboBox.getSelectionModel().select(item);
    }
    
    /**
     * Get the underlying {@code ComboBox}'s selected item.
     * @return The selected item.
     */
    public T getSelectedItem() {
        return selectedItem;
    }
    
    protected void onItemSelected(T newItem) {
         selectedItem = newItem;
    }
    
    /**
     * Set the {@code ComboBox}'s selected item changed handler to be
     * called every time a new item is selected.
     * @param handler A {@code Consumer<T>} to be called when a new value
     * is selected. The new value is passed to the {@link handler}.
     * @throws NullPointerException if {@link handler} is null.
     */
    public void setOnItemSelected(Consumer<T> handler) {
        if (handler == null) {
            throw new NullPointerException("Handler cannot be null");
        }
        onItemSelectedHandler = handler;
    }
}
