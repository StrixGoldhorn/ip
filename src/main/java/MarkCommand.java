/** Marks one task as done. */
public final class MarkCommand extends Command {
    private final int taskNumber;

    /** Creates a command for a one-based task number. */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Marks, saves, and displays the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException {
        Task task = tasks.setDone(taskNumber);
        storage.save(tasks);
        ui.showTaskMarked(task, true);
    }
}
