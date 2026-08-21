/** Marks one task as not done. */
public final class UnmarkCommand extends Command {
    private final int taskNumber;

    /** Creates a command for a one-based task number. */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Unmarks, saves, and displays the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException {
        Task task = tasks.setNotDone(taskNumber);
        storage.save(tasks);
        ui.showTaskMarked(task, false);
    }
}
