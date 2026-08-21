package megatron.command;

import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.ui.Ui;

/** Deletes one task from the task list. */
public final class DeleteCommand extends Command {
    private final int taskNumber;

    /** Creates a command for a one-based task number. */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Deletes, saves, and displays the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException {
        Task removedTask = tasks.removeTask(taskNumber);
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
