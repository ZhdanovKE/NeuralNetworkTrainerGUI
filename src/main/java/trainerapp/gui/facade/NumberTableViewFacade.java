package trainerapp.gui.facade;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

/**
 * Wrap a {@code TableView} that can hold {@code ObservableList} of a subclass 
 * of {@code Number}, make its cells editable and provide convenience utility 
 * methods for operating on this {@code TableView}.
 * 
 * @param <T> Numeric type that the underlying {@code TableView} hold.
 * 
 * @author Konstantin Zhdanov
 */
public class NumberTableViewFacade<T extends Number> {
    private final TableView<ObservableList<T>> tableView;

    private final StringConverter<T> converter;
    
    public NumberTableViewFacade(TableView<ObservableList<T>> tableView, 
            StringConverter<T> converter) {
        if (tableView == null || converter == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        this.tableView = tableView;
        this.converter = converter;
    }
    
    /**
     * Create a non-sortable column of editable cells with {@code title} and add
     * it as the right-most column of the underlying {@code TableView}.
     * @param title {@code String} value to be used as the title of the
     * newly created column.
     */
    public void addColumn(String title) {
        TableColumn<ObservableList<T>, T> newColumn = 
                new TableColumn<>(title);
        newColumn.setSortable(false);
        
        final int idx = tableView.getColumns().size();
        newColumn.setCellFactory((param) -> {
            TableCell<ObservableList<T>, T> cell = 
                    new EditingTableCell<>(converter, idx);
            return cell;
        });
        newColumn.setCellValueFactory((param) -> {
            if (idx >= param.getValue().size()) {
                return new ReadOnlyObjectWrapper<>(null);
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().get(idx));
        });
        newColumn.setEditable(true);
        
        tableView.getColumns().add(newColumn);
    }
    
    /**
     * Remove the right-most column of the underlying {@code TableView} unless
     * it's the only column in the {@code TableView}.
     */
    public void removeLastColumn() {
        if (tableView.getColumns().isEmpty()) {
            return;
        }
        tableView.getColumns().remove(tableView.getColumns().size() - 1);
    }
    
    /**
     * Remove all items and columns of the underlying {@code TableView}.
     */
    public void clear() {
        tableView.getItems().clear();
        tableView.getColumns().clear();
    }
    
    /**
     * Number of columns in the underlying {@code TableView}.
     * @return {@code int} value representing the number of columns of
     * the underlying {@code TableView}.
     */
    public int getColumnsCount() {
        return tableView.getColumns().size();
    }
    
    /**
     * Set the items collection of the underlying {@code TableView} to the
     * provided value {@link value}.
     * @param items {@code ObservableList} of {@code ObservableList} of numeric values.
     */
    public void setItems(ObservableList<ObservableList<T>> items) {
        tableView.setItems(items);
    }
    
    /**
     * Get the items collection of the underlying {@code TableView}.
     * @return {@code ObservableList} of {@code ObservableList} of numeric values
     * stored in the underlying {@code TableView}.
     */
    public ObservableList<ObservableList<T>> getItems() {
        return tableView.getItems();
    }
    
    /**
     * Get the numeric value of the cell of the underlying {@code TableView} 
     * at {@link row} row and {@link col} column.
     * @param row Index of the row of the cell to get the value from.
     * @param col Index of the column of the cell to get the value from.
     * @return The numeric value the cell at {@code row} row and {@code col} column
     * holds.
     * @throws IndexOutOfBoundsException if {@link row} {@literal < 0} or
     * {@link col} {@literal < 0} or {@link row}, {@link col} exceed the bounds. 
     */
    public T getValue(int row, int col) {
        if (row < 0 || row >= tableView.getItems().size() ||
                col < 0 || col >= tableView.getColumns().size()) {
            throw new IndexOutOfBoundsException("One of the indices is out of bounds");
        }
        return tableView.getItems().get(row).get(col);
    }
    
    /**
     * Set the numeric value of the cell of the underlying {@code TableView} 
     * at {@link row} row and {@link col} column to the {@link value}.
     * @param row Index of the row of the cell to set a new value {@link value}.
     * @param col Index of the column of the cell to set a new value {@link value}.
     * @param value The value to set.
     * @throws IndexOutOfBoundsException if {@link row} {@literal < 0} or
     * {@link col} {@literal < 0} or {@link row}, {@link col} exceed the bounds. 
     */
    public void setValue(int row, int col, T value) {
        if (row < 0 || row >= tableView.getItems().size() ||
                col < 0 || col >= tableView.getColumns().size()) {
            throw new IndexOutOfBoundsException("One of the indices is out of bounds");
        }
        tableView.getItems().get(row).set(col, value);
    }
    
    private static class EditingTableCell<E> extends TableCell<ObservableList<E>, E>{

        private final TextField textField;

        private final StringConverter<E> converter;
        
        private final int columnIdx;

        public EditingTableCell(StringConverter<E> converter, int columnIdx) {
            
            this.converter = converter;
            this.columnIdx = columnIdx;

            textField = new TextField();

            textField.setOnAction((event) -> {
                E converted;
                try {
                    converted = converter.fromString(textField.getText());
                }
                catch(Exception e) {
                    converted = null;
                }
                if (converted == null) {
                    cancelEdit();
                }
                else {
                    commitEdit(converted);
                }
                event.consume();
            });

            textField.setOnKeyReleased((event) -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                    event.consume();
                }
            });

            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                // If focus is lost, then commit
                if (!newValue) {
                    E converted;
                    try {
                        converted = converter.fromString(textField.getText());
                    }
                    catch(Exception e) {
                        converted = null;
                    }
                    if (converted == null) {
                        cancelEdit();
                    }
                    else {
                        commitEdit(converted);
                    }
                }
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (this.isEditing()) {
                textField.setText(converter.toString(this.getItem()));
                this.setText(null);
                this.setGraphic(textField);
                textField.selectAll();
                textField.requestFocus();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            this.setText(converter.toString(this.getItem()));
            setGraphic(null);
        }

        @Override
        public void commitEdit(E item) {
            if (isEditing()) {
                this.getTableView().getItems().get(this.getTableRow().getIndex()).
                        set(columnIdx, item);
                
            }
            super.commitEdit(item);
        }
        
        @Override
        protected void updateItem(E item, boolean empty) {
            super.updateItem(item, empty);
            if (this.isEmpty()) {
                this.setText(null);
                this.setGraphic(null);
            } else {
                if (this.isEditing()) {
                    textField.setText(converter.toString(this.getItem()));

                    this.setText(null);

                    this.setGraphic(textField);
                } else {
                    this.setText(converter.toString(this.getItem()));
                    this.setGraphic(null);
                }
            }
        }
    }
}
