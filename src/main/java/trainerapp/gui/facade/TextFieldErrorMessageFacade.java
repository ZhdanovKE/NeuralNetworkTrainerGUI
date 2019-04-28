package trainerapp.gui.facade;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Wrap a {@code TextField} and provide methods for 
 * showing and hiding an error message below it.
 * @author Konstantin Zhdanov
 */
public class TextFieldErrorMessageFacade {
    
    private final TextField textField;
    
    private Tooltip oneTimeErrorTooltip;
    
    private Tooltip hoverErrorTooltip;
    
    private final Timer timer;
    
    private static final int TOOLTIP_CLOSE_DELAY = 2500;
    
    public TextFieldErrorMessageFacade(TextField toValidate) {
        if (toValidate == null) {
            throw new NullPointerException("TextField cannot be null");
        }
        textField = toValidate;
        timer = new Timer(true);
    }
    
    public void showError(String errorMsg) {
        clear();
        setTextFieldErrorStyle(textField);
        addErrorMessageTooltipToTextField(textField, errorMsg);
    }
    
    public void hideError() {
        clear();
        clearTextFieldStyle(textField);
        textField.setTooltip(null);
    }
    
    private void clear() {
        if (oneTimeErrorTooltip != null) {
            oneTimeErrorTooltip.hide();
            oneTimeErrorTooltip = null;
        }
        if (hoverErrorTooltip != null) {
            hoverErrorTooltip.hide();
            hoverErrorTooltip = null;
        }
    }
    
    private void setTextFieldErrorStyle(TextField tf) {
        tf.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
    }
    
    private void clearTextFieldStyle(TextField tf) {
        tf.setStyle("");
    }
    
    private void addErrorMessageTooltipToTextField(TextField tf, String message) {
        oneTimeErrorTooltip = createErrorTooltip(message);
        setTooltipCloseTimeout(oneTimeErrorTooltip, TOOLTIP_CLOSE_DELAY);
        showErrorTooltipUnderNode(oneTimeErrorTooltip, tf);
        hoverErrorTooltip = createErrorTooltip(message);
        tf.setTooltip(hoverErrorTooltip);
    }
    
    private Tooltip createErrorTooltip(String message) {
        Tooltip errorTooltip = new Tooltip();
        errorTooltip.setText(message);
        errorTooltip.setStyle("-fx-background-color: white;-fx-text-fill: red; -fx-font-weight: bold;");
        errorTooltip.setAutoHide(true);
        return errorTooltip;
    }
    
    private void setTooltipCloseTimeout(Tooltip tooltip, long timeout) {
        tooltip.setOnShown((event) -> {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        tooltip.hide();
                    });
                }
            }, timeout);
            timer.purge();
        });
    }
    
    private void showErrorTooltipUnderNode(Tooltip tooltip, Node node) {
        Point2D pointsInScreen = node.localToScreen(node.getBoundsInLocal().getMinX(), node.getBoundsInLocal().getMaxY());
        tooltip.show(node, pointsInScreen.getX(), pointsInScreen.getY());
    }
}
