package trainerapp.gui;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class
 * @author Konstantin Zhdanov
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
        Scene scene = new Scene(root, root.prefWidth(-1), root.prefHeight(-1));
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("Neural Network Trainer GUI");
        stage.setScene(scene);
        stage.setOnShowing((event) -> {
            stage.hide();
        });
        stage.setOnShown((event) -> {
            double widthDelta = stage.getWidth() - stage.getScene().getWidth();
            double heightDelta = stage.getHeight() - stage.getScene().getHeight();
            if (widthDelta < 0) {
                widthDelta = 0;
            }
            if (heightDelta < 0) {
                heightDelta = 0;
            }
            stage.setMinWidth(stage.getScene().getRoot().minWidth(-1) + widthDelta);
            stage.setMinHeight(stage.getScene().getRoot().minHeight(-1) + heightDelta);
            stage.show();
        });
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
