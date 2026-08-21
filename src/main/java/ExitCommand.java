/** Executes the user's request to exit Megatron. */
public final class ExitCommand extends Command {
    /** Displays the existing goodbye message. */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) {
        ui.showGoodbye();
    }

    /** An exit command ends the application loop. */
    @Override
    public boolean isExit() {
        return true;
    }
}
