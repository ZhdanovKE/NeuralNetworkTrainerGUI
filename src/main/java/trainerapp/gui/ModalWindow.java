package trainerapp.gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * A class for creating modal windows based on FXML files.
 * 
 * @author Konstantin Zhdanov
 */
public class ModalWindow {
    
    Stage stage;
    Object controller;
    
    public ModalWindow(String fxmlPath, String title, Window parent) {
        stage = new Stage();
        FXMLLoader loader = new FXMLLoader(this.getClass().
                getResource(fxmlPath));
        Parent root;
        try {
            root = loader.load();
        }
        catch(IOException e) {
            throw new IllegalArgumentException("Cannot create window", e);
        }
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        
        setStageOnCenterOfWindow(stage, parent);
        
        controller = loader.getController();
    }
    
    private void setStageOnCenterOfWindow(Stage stage, Window parent) {
        final double centerXPosition = parent.getX() + parent.getWidth() / 2.0;
        final double centerYPosition = parent.getY() + parent.getHeight() / 2.0;

        stage.setOnShowing((evt) -> {
            stage.hide();
        });
        stage.setOnShown((evt) -> {
            stage.setX(centerXPosition - stage.getWidth() / 2.0);
            stage.setY(centerYPosition - stage.getHeight() / 2.0);
            stage.show();
        });
    }
    
    public Object getController() {
        return controller;
    }
    
    public Object getUserData() {
        return stage.getScene().getUserData();
    }
    
    // Blocking operation
    public void show() {
        stage.showAndWait();
    }
}
