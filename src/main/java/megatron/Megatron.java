package megatron;

import megatron.command.Command;
import megatron.command.Parser;
import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Starts the Megatron chatbot application.
 */
public class Megatron {
    private final Ui ui;
    private final TaskStorage storage;
    private final TaskList tasks;
    private final Parser parser;

    /**
     * Creates a Megatron application that stores tasks at the given path.
     *
     * @param filePath The path to the task storage file.
     */
    public Megatron(String filePath) {
        ui = new Ui();
        storage = new TaskStorage(filePath);
        tasks = storage.load();
        parser = new Parser();
    }

    /**
     * Creates a Megatron application using the default data path.
     */
    public Megatron() {
        this("data/megatron.csv");
    }

    /**
     * Runs the application until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showDivider();

            try {
                Command command = parser.parse(input);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (MegatronException exception) {
                ui.showError(exception);
            } finally {
                if (!isExit) {
                    ui.showDivider();
                }
            }
        }
        ui.close();
    }

    /**
     * Starts Megatron using a path supplied as the first command-line argument.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        new Megatron(args.length > 0 ? args[0] : "data/megatron.csv").run();
    }

}
