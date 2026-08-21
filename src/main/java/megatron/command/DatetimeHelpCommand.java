package megatron.command;

import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.ui.Ui;

/** Displays information about supported date and time formats. */
public final class DatetimeHelpCommand extends Command {
    /** Displays the existing date/time help text. */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) {
        ui.showDatetimeInformation();
    }
}
