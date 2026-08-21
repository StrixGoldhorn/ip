/**
 * Starts the Megatron chatbot application.
 */
public class Megatron {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        TaskStorage storage = new TaskStorage(args.length > 0 ? args[0] : "data/megatron.csv");
        TaskList tasks = storage.load();
        Parser parser = new Parser();
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
                    markTask(tasks, command, true, storage, ui);
                } else if (command.getType() == Parser.CommandType.UNMARK) {
                    markTask(tasks, command, false, storage, ui);
                } else if (command.getType() == Parser.CommandType.DELETE) {
                    deleteTask(tasks, command, storage, ui);
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

    /**
     * Changes the completion status of a task selected by its list number.
     *
     * @param tasks the collection containing the stored tasks
     * @param command the mark or unmark command
     * @param shouldMarkDone whether the task should be marked as done
     */
    private static void markTask(TaskList tasks, Parser.Command command, boolean shouldMarkDone, TaskStorage storage,
            Ui ui) throws MegatronException {
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
     * @param tasks the collection containing the stored tasks
     * @param command the delete command
     * @throws MegatronException if the command does not contain a valid task number
     */
    private static void deleteTask(TaskList tasks, Parser.Command command, TaskStorage storage, Ui ui)
            throws MegatronException {
        if (command.getTaskNumber() == null) {
            throw new InvalidTaskNumberException();
        }
        Task removedTask = tasks.removeTask(command.getTaskNumber());
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }

}
