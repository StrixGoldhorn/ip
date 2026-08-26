package megatron.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controls the main Megatron window.
 */
public class MainWindowController {
    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField commandInput;

    @FXML
    private Button sendButton;
}
