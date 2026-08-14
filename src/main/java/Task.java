/**
 * Represents one task in Megatron's task list.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new task that is not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task is done.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task in the format used by the user interface.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }

    /** Returns the task type marker used in the list view. */
    protected String getTypeMarker() {
        return "[-]";
    }

    /** Returns the type marker, status, and description for this task. */
    public String displayText() {
        return getTypeMarker() + toString();
    }
}
