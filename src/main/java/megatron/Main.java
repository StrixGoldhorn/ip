package megatron;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Represents the main window of the Megatron JavaFX application.
 */
public class Main extends Application {
    private static final String WINDOW_TITLE = "Megatron";
    private static final String PLACEHOLDER_MESSAGE = "Megatron GUI will be added here.";
    private static final double WINDOW_WIDTH = 600;
    private static final double WINDOW_HEIGHT = 400;

    /**
     * Displays the temporary main window.
     *
     * @param stage The primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        Label placeholder = new Label(PLACEHOLDER_MESSAGE);
        Scene scene = new Scene(placeholder, WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }
}
