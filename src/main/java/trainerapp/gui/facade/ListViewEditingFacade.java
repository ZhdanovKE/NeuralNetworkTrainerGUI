package trainerapp.gui.facade;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import trainerapp.gui.repository.NamedObjectRepository;

/**
 * A wrapper around a {@code ListView} containing a {@code NamedObjectRepository} and  
 * adding the rename ability to
 * the loaded items from the {@code NamedObjectRepository}.
 * @param <T> Type of items stored in the underlying {@code ListView}.
 * @author Konstantin Zhdanov
 */
public class ListViewEditingFacade<T> {
    private final ListView<T> listView;
    
    private final NamedObjectRepository<T> repo;
    
    private StringConverter<T> converter = new StringConverter<T>() {
        @Override
        public String toString(T object) {
            return repo.getNameForObject(object);
        }

        @Override
        public T fromString(String string) {
            return repo.get(string);
        }
    };
    
    /**
     * Create a Facade for the {@link listView} which holds elements of the {@link repo}
     * and allows a user to rename the objects of the {@link repo}.
     * @param listView a {@code ListView} instance to be wrapped and enhanced
     * with the editing ability.
     * @param repo a {@NamedObjectRepository} instance which elements are to be
     * the {@link listView}'s items.
     */
    public ListViewEditingFacade(ListView<T> listView, NamedObjectRepository<T> repo) {
        if (listView == null || repo == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        this.listView = listView;
        this.repo = repo;
        
        this.listView.setCellFactory((param) -> {
            return new EditingCell<>(converter, this::updateNamedObject); 
        });
        
        this.listView.setItems(repo.getObjectsObservableList());
    }
    
    private void updateNamedObject(String oldName, String newName) {
        repo.rename(oldName, newName);
    }

    private static class EditingCell<E> extends ListCell<E> {

        public interface NameChangeHandler<E> {
            void handle(String oldName, String newName);
        }
        
        private final NameChangeHandler<E> nameChangeHandler;
        
        private final StringConverter<E> converter;
        
        private TextField textField;
        
        /**
         * Create a cell that allows changing it's text value via showing
         * a {@code TextField} and will inform the outer code that the 
         * change has been made.
         * @param converter an instance of {@code StringConverter<E>} used for
         * converting the items of type {@link E} into String and vice versa. 
         * @param nameChangeHandler an instance of {@code NameChangeHandler<E>}
         * to be called when the successful change of the cell item's to be
         * made on the underlying model.
         */
        public EditingCell(StringConverter<E> converter, 
                NameChangeHandler<E> nameChangeHandler) {
            textField = null;
            this.converter = converter;
            this.nameChangeHandler = nameChangeHandler;
        }
        
        /**
         * Call the provided handler to let outer code handle the change and
         * update the model of this cell.
         * @param oldName The old name that has been changed.
         * @param newName The new name to be set on the model.
         */
        protected void handleNameChange(String oldName, String newName) {
            if (nameChangeHandler != null) {
                nameChangeHandler.handle(oldName, newName);
            }
        }
        
        private void setUpTextFieldListeners(final TextField tf) {
            tf.setOnAction((event) -> {
                String newName = tf.getText();
                if (newName != null && newName.length() > 0) {
                    String oldName = converter.toString(this.getItem());
                    if (!newName.equals(oldName)) {
                        // notify that the name changed
                        handleNameChange(oldName, newName);
                    }
                }
                cancelEdit(); // item stays the same
                event.consume();
            });

            tf.setOnKeyReleased((event) -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                    event.consume();
                }
            });

            tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
                // If focus is lost, then commit
                if (!newValue) {
                    String newName = tf.getText();
                    if (newName != null && newName.length() > 0) {
                        String oldName = converter.toString(this.getItem());
                        if (!newName.equals(oldName)) {
                            // notify that the name changed
                            handleNameChange(oldName, newName);
                        }
                    }
                    cancelEdit(); // item stays the same
                }
            });
        }
        
        @Override
        public void cancelEdit() {
            super.cancelEdit(); 
            this.setText(converter.toString(this.getItem()));
            this.setGraphic(null);
        }

        @Override
        public void startEdit() {
            super.startEdit(); 
            if (this.isEditing()) {
                if (textField == null) {
                    textField = new TextField();
                    setUpTextFieldListeners(textField);
                }
                textField.setText(converter.toString(this.getItem()));
                this.setText(null);
                this.setGraphic(textField);
                textField.selectAll();
                textField.requestFocus();
            }
        }

        @Override
        protected void updateItem(E item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                if (this.isEditing()) {
                    textField.setText(converter.toString(this.getItem()));
                    this.setText(null);
                    this.setGraphic(textField);
                } else {
                    this.setText(converter.toString(this.getItem()));
                    this.setGraphic(null);
                }
            }
            else {
                this.setText(null);
                this.setGraphic(null);
            }
        }
        
    }
}
