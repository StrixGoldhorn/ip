import java.util.Objects;

/** Adds one task to the task list. */
public final class AddCommand extends Command {
    private static final int MAX_TASKS = 100;

    private final String input;
    private final Parser parser;

    /** Creates an add command from the original user input. */
    public AddCommand(String input, Parser parser) {
        this.input = Objects.requireNonNull(input);
        this.parser = Objects.requireNonNull(parser);
    }

    /** Validates, adds, saves, and displays the new task. */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException {
        if (tasks.size() == MAX_TASKS) {
            throw new TaskListFullException();
        }
        Task task = parser.createTask(input);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
