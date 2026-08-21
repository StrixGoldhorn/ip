import java.util.ArrayList;
import java.util.List;

/**
 * Starts the Megatron chatbot application.
 */
public class Megatron {
    private static final int MAX_TASKS = 100;
    private static final List<String> AVAILABLE_COMMANDS = new ArrayList<>(List.of(
            "todo", "deadline", "event", "list", "mark", "unmark", "delete", "datetime-help"));

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        TaskStorage storage = new TaskStorage(args.length > 0 ? args[0] : "data/megatron.csv");
        List<Task> tasks = storage.load();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            try {
                if (command.equals("list")) {
                    ui.showTasks(tasks);
                } else if (command.equals("datetime-help")) {
                    ui.showDatetimeInformation();
                } else if (command.startsWith("mark ")) {
                    markTask(tasks, command, true, storage, ui);
                } else if (command.startsWith("unmark ")) {
                    markTask(tasks, command, false, storage, ui);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    deleteTask(tasks, command, storage, ui);
                } else if (!command.isBlank()) {
                    if (tasks.size() == MAX_TASKS) {
                        throw new TaskListFullException();
                    }
                    Task newTask = createTask(command);
                    tasks.add(newTask);
                    storage.save(tasks);
                    ui.showTaskAdded(newTask, tasks.size());
                } else {
                    throw new EmptyCommandException();
                }
            } catch (MegatronException exception) {
                ui.showError(exception);
            }
            ui.showDivider();
        }
        ui.close();
    }

    /** Converts a user command into the matching task subtype. */
    private static Task createTask(String command) throws MegatronException {
        if (command.equals("todo")) {
            throw new EmptyDescriptionException();
        }
        if (command.startsWith("todo ")) {
            String description = command.substring(5).trim();
            if (description.isEmpty()) {
                throw new EmptyDescriptionException();
            }
            return new Todo(description);
        }
        if (command.startsWith("deadline ")) {
            String[] parts = command.substring(9).split(" /by ", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                throw new InvalidTaskFormatException("deadline <description> /by <date>.");
            }
            try {
                return new Deadline(parts[0].trim(), parts[1].trim());
            } catch (IllegalArgumentException exception) {
                throw new InvalidTaskFormatException("deadline <description> /by <valid date/time>. "
                        + "Use datetime-help to view supported date/time formats.");
            }
        }
        if (command.startsWith("event ")) {
            String[] parts = command.substring(6).split(" /from ", 2);
            if (parts.length != 2) {
                throw new InvalidTaskFormatException("event <description> /from <start> /to <end>.");
            }
            String[] times = parts[1].split(" /to ", 2);
            if (times.length != 2 || parts[0].trim().isEmpty() || times[0].trim().isEmpty()
                    || times[1].trim().isEmpty()) {
                throw new InvalidTaskFormatException("event <description> /from <start> /to <end>.");
            }
            try {
                return new Event(parts[0].trim(), times[0].trim(), times[1].trim());
            } catch (IllegalArgumentException exception) {
                throw new InvalidTaskFormatException("event <description> /from <valid start> /to <valid end>. "
                        + "Use datetime-help to view supported date/time formats.");
            }
        }
                    throw new UnknownCommandException(AVAILABLE_COMMANDS);
    }

    /**
     * Changes the completion status of a task selected by its list number.
     *
     * @param tasks the collection containing the stored tasks
     * @param command the mark or unmark command
     * @param shouldMarkDone whether the task should be marked as done
     */
    private static void markTask(List<Task> tasks, String command, boolean shouldMarkDone, TaskStorage storage,
            Ui ui) throws MegatronException {
        try {
            int taskNumber = Integer.parseInt(command.substring(command.indexOf(' ') + 1).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TaskNotFoundException();
            }

            Task task = tasks.get(taskNumber - 1);
            if (shouldMarkDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            storage.save(tasks);
            ui.showTaskMarked(task, shouldMarkDone);
        } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
            throw new InvalidTaskNumberException();
        }
    }

    /**
     * Removes a task selected by its list number.
     *
     * @param tasks the collection containing the stored tasks
     * @param command the delete command
     * @throws MegatronException if the command does not contain a valid task number
     */
    private static void deleteTask(List<Task> tasks, String command, TaskStorage storage, Ui ui)
            throws MegatronException {
        try {
            int taskNumber = Integer.parseInt(command.substring(command.indexOf(' ') + 1).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TaskNotFoundException();
            }

            Task removedTask = tasks.remove(taskNumber - 1);
            storage.save(tasks);
            ui.showTaskDeleted(removedTask, tasks.size());
        } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
            throw new InvalidTaskNumberException();
        }
    }

}
