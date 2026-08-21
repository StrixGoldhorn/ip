/** Represents an executable user command. */
public abstract class Command {
    /** Executes this command using the application objects. */
    public abstract void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException;

    /** Returns whether executing this command should end the application. */
    public boolean isExit() {
        return false;
    }
}
