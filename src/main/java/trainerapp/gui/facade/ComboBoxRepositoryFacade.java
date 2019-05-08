package trainerapp.gui.facade;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    
    private BooleanProperty itemIsSelected;
    
    private Consumer<T> onItemSelectedHandler = (t) -> {};
    // Default converter
    private Function<T,String> stringConverter = (t) -> {
        return repo.getNameForObject(t);
    };

    private BiFunction<T,String,String> overridingConverter;
    
    /**
     * Create the facade for the passed {@link comboBox} and use the 
     * default conversion of objects 
     * of type {@link T} to {@code String} to show the items in
     * the {@link comboBox}. The default conversion is to use the 
     * {@code NamedObjectRepository} to get the name of an object.
     * @param comboBox A {@code ComboBox} that will be controlled.
     * @throws NullPointerException if either {@link comboBox} is null.
     */
    public ComboBoxRepositoryFacade(ComboBox<T> comboBox) {
        this(comboBox, (t, s) -> s);
    }

    /**
     * Create the facade for the passed {@link comboBox} and use the 
     * {@link overridingConverter} to change the default conversion of objects 
     * of type {@link T} to {@code String} to show the items in
     * the {@link comboBox}. The default conversion is to use the 
     * {@code NamedObjectRepository} to get the name of an object.
     * @param comboBox A {@code ComboBox} that will be controlled.
     * @param overridingConverter A {@code BiFunction<T,String,String>} to be 
     * used to change the default conversion (second parameter) of element 
     * {@code T} (first parameter) into {@code String} (third parameter).
     * items of type {@code T} to String to be showed in the {@link comboBox}.
     * @throws NullPointerException if either {@link comboBox} or {@link overridingConverter}
     * is null.
     */
    public ComboBoxRepositoryFacade(ComboBox<T> comboBox, 
            BiFunction<T,String,String> overridingConverter) {
        if (comboBox == null || overridingConverter == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        this.comboBox = comboBox;
        this.overridingConverter = overridingConverter;
        this.comboBox.setConverter(new StringConverter<T>() {
            @Override public String toString(T object) {
                return ComboBoxRepositoryFacade.this.
                        overridingConverter.apply(object, 
                                        stringConverter.apply(object));
            }
            @Override public T fromString(String string) {
                throw new UnsupportedOperationException("Not supported.");
            }
        });
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    ComboBoxRepositoryFacade.this.onItemSelected(newValue);
        });
        this.itemIsSelected = new SimpleBooleanProperty(false);
        this.itemIsSelected.bind(this.comboBox.valueProperty().isNotNull());
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
     * @throws IllegalStateException if a non-null {@code NamedObjectRepository} 
     * wasn't set with the {@code setRepository} method.
     * @throws IllegalArgumentException if the {@link item} doesn't exist in
     * the {@code NamedObjectRepository}.
     * @throws NullPointerException if the {@link item} is null.
     */
    public void select(T item) {
       if (repo == null) {
           throw new IllegalStateException("Repository is not set");
       }
       if (!repo.containsObject(item)) {
           throw new IllegalArgumentException("The passed item doesn't exist in the repository");
       }
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
         onItemSelectedHandler.accept(newItem);
    }
    
    /**
     * Get a {@code BooleanProperty} that reflects if an item
     * has been selected in the {@code ComboBox}.
     * @return {@code BooleanProperty} holding a boolean value if an item
     * has been selected in the {@code ComboBox}.
     */
    public BooleanProperty itemIsSelectedProperty() {
        return itemIsSelected;
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
