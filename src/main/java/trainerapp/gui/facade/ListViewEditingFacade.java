/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainerapp.gui.facade;

import java.util.AbstractList;
import java.util.function.BiConsumer;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javax.swing.text.TableView;
import trainerapp.gui.repository.NamedObjectRepository;

/**
 * A wrapper around a {@code ListView} containing a {@code NamedObjectRepository} and  
 * adding the rename abilities to
 * the loaded items from the {@code NamedObjectRepository}.
 * @param <T> Type of items stored in the underlying {@code TableView}.
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
        
        public EditingCell(StringConverter<E> converter, 
                NameChangeHandler<E> nameChangeHandler) {
            textField = null;
            this.converter = converter;
            this.nameChangeHandler = nameChangeHandler;
        }
        
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
