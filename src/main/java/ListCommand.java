/** Displays all tasks in the task list. */
public final class ListCommand extends Command {
    /** Displays the current tasks without modifying them. */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) {
        ui.showTasks(tasks);
    }
}
