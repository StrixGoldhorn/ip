package megatron.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import megatron.command.AddCommand;
import megatron.command.Command;
import megatron.command.DatetimeHelpCommand;
import megatron.command.DeleteCommand;
import megatron.command.ExitCommand;
import megatron.command.FindCommand;
import megatron.command.ListCommand;
import megatron.command.MarkCommand;
import megatron.command.Parser;
import megatron.command.UnmarkCommand;
import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;

/**
 * Controls the main Megatron window.
 */
public class MainWindowController {
    private static final String STORAGE_FILE_PATH = "data/megatron.csv";
    private static final String USER_MESSAGE_PREFIX = "";

    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField commandInput;

    @FXML
    private Button sendButton;

    private final TaskStorage storage;
    private final TaskList tasks;
    private final Parser parser;

    /**
     * Creates a controller backed by Megatron's default task storage.
     */
    public MainWindowController() {
        storage = new TaskStorage(STORAGE_FILE_PATH);
        tasks = storage.load();
        parser = new Parser();
    }

    /**
     * Displays the welcome message after FXML injects the controls.
     */
    @FXML
    private void initialize() {
        appendMessage(captureOutput(ui -> ui.showWelcome()), false, DialogBox.DialogType.WELCOME);
    }

    /**
     * Processes the command in the input field and displays the response.
     */
    @FXML
    private void handleUserInput() {
        String input = commandInput.getText();
        if (input.isBlank()) {
            return;
        }

        appendMessage(USER_MESSAGE_PREFIX + input, true, DialogBox.DialogType.DEFAULT);

        boolean isExit = false;
        try {
            Command command = parser.parse(input);
            appendMessage(captureCommandOutput(command), false, getDialogType(command));
            isExit = command.isExit();
        } catch (MegatronException exception) {
            appendMessage(captureOutput(ui -> ui.showError(exception)), false, DialogBox.DialogType.ERROR);
        }

        commandInput.clear();
        if (isExit) {
            commandInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /**
     * Appends a message to the conversation and scrolls to the latest message.
     *
     * @param message The message to append.
     */
    private void appendMessage(String message, boolean isUserMessage, DialogBox.DialogType dialogType) {
        DialogBox dialogBox = new DialogBox(message, isUserMessage, dialogType);
        messageContainer.getChildren().add(dialogBox);
        Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }

    /**
     * Returns the visual style for a parsed command response.
     *
     * @param command The parsed command.
     * @return The dialog type for the command response.
     */
    private static DialogBox.DialogType getDialogType(Command command) {
        return switch (command) {
            case AddCommand ignored -> DialogBox.DialogType.ADD;
            case MarkCommand ignored -> DialogBox.DialogType.MARK;
            case UnmarkCommand ignored -> DialogBox.DialogType.UNMARK;
            case FindCommand ignored -> DialogBox.DialogType.FIND;
            case ListCommand ignored -> DialogBox.DialogType.LIST;
            case DeleteCommand ignored -> DialogBox.DialogType.DELETE;
            case DatetimeHelpCommand ignored -> DialogBox.DialogType.HELP;
            case ExitCommand ignored -> DialogBox.DialogType.BYE;
            case null, default -> DialogBox.DialogType.DEFAULT;
        };
    }

    /**
     * Captures output produced through a temporary console UI.
     *
     * @param outputAction The action that writes to the UI.
     * @return The captured output without its trailing line break.
     */
    private static String captureOutput(UiOutputAction outputAction) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner("")) {
            Ui ui = new Ui(scanner, printStream);
            outputAction.execute(ui);
        }
        return output.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Executes a command and captures its response.
     *
     * @param command The command to execute.
     * @return The command response without its trailing line break.
     * @throws MegatronException If the command input or task selection is invalid.
     */
    private String captureCommandOutput(Command command) throws MegatronException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner("")) {
            Ui ui = new Ui(scanner, printStream);
            command.execute(tasks, ui, storage);
        }
        return output.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Represents an action that writes a response through the console UI.
     */
    @FunctionalInterface
    private interface UiOutputAction {
        void execute(Ui ui);
    }
}
