package megatron;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Represents the main window of the Megatron JavaFX application.
 */
public class Main extends Application {
    private static final String WINDOW_TITLE = "Megatron";
    private static final double WINDOW_WIDTH = 700;
    private static final double WINDOW_HEIGHT = 500;

    /**
     * Displays the main window loaded from its FXML layout.
     *
     * @param stage The primary JavaFX stage.
     * @throws IOException If the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }
}
