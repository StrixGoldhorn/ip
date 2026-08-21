/**
 * Starts the Megatron chatbot application.
 */
public class Megatron {
    private static final int MAX_TASKS = 100;
    private final Ui ui;
    private final TaskStorage storage;
    private final TaskList tasks;
    private final Parser parser;

    /** Creates a Megatron application that stores tasks at the given path. */
    public Megatron(String filePath) {
        ui = new Ui();
        storage = new TaskStorage(filePath);
        tasks = storage.load();
        parser = new Parser();
    }

    /** Creates a Megatron application using the default data path. */
    public Megatron() {
        this("data/megatron.csv");
    }

    /** Runs the application until the user exits or input ends. */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showDivider();

            try {
                Parser.Command command = parser.parse(input);
                if (command.getType() == Parser.CommandType.BYE) {
                    ui.showGoodbye();
                    break;
                } else if (command.getType() == Parser.CommandType.LIST) {
                    ui.showTasks(tasks);
                } else if (command.getType() == Parser.CommandType.DATETIME_HELP) {
                    ui.showDatetimeInformation();
                } else if (command.getType() == Parser.CommandType.MARK) {
                    markTask(command, true);
                } else if (command.getType() == Parser.CommandType.UNMARK) {
                    markTask(command, false);
                } else if (command.getType() == Parser.CommandType.DELETE) {
                    deleteTask(command);
                } else {
                    if (tasks.size() == MAX_TASKS) {
                        throw new TaskListFullException();
                    }
                    Task newTask = parser.createTask(command);
                    tasks.add(newTask);
                    storage.save(tasks);
                    ui.showTaskAdded(newTask, tasks.size());
                }
            } catch (MegatronException exception) {
                ui.showError(exception);
            }
            ui.showDivider();
        }
        ui.close();
    }

    /** Starts Megatron using a path supplied as the first command-line argument. */
    public static void main(String[] args) {
        new Megatron(args.length > 0 ? args[0] : "data/megatron.csv").run();
    }

    /**
     * Changes the completion status of a task selected by its list number.
     *
     * @param command the mark or unmark command
     * @param shouldMarkDone whether the task should be marked as done
     */
    private void markTask(Parser.Command command, boolean shouldMarkDone) throws MegatronException {
        int taskNumber = command.getTaskNumber();
        Task task;
        if (shouldMarkDone) {
            task = tasks.setDone(taskNumber);
        } else {
            task = tasks.setNotDone(taskNumber);
        }
        storage.save(tasks);
        ui.showTaskMarked(task, shouldMarkDone);
    }

    /**
     * Removes a task selected by its list number.
     *
     * @param command the delete command
     * @throws MegatronException if the command does not contain a valid task number
     */
    private void deleteTask(Parser.Command command) throws MegatronException {
        if (command.getTaskNumber() == null) {
            throw new InvalidTaskNumberException();
        }
        Task removedTask = tasks.removeTask(command.getTaskNumber());
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }

}
