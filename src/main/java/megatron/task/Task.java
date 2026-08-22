package megatron.task;

/**
 * Represents one task in Megatron's task list.
 */
public class Task {
    private final String description;
    private final TaskType type;
    private TaskStatus status;

    /**
     * Creates a new task that is not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        this(description, TaskType.TASK);
    }

    /** Creates a task with the given type and an initial not-done status. */
    protected Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.status = TaskStatus.NOT_DONE;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns whether this task is done.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /** Returns the task description for storage. */
    public String getDescription() {
        return description;
    }

    /** Returns the CSV type code for storage. */
    public String getTypeCode() {
        return type == TaskType.DEADLINE ? "D" : type == TaskType.EVENT ? "E" : "T";
    }

    /** Returns the additional CSV field for this task. */
    public String getExtra() {
        return "";
    }

    /**
     * Returns the task in the format used by the user interface.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return status.getMarker() + " " + description;
    }

    /** Returns the task type marker used in the list view. */
    protected String getTypeMarker() {
        return type.getMarker();
    }

    /** Returns the type marker, status, and description for this task. */
    public String displayText() {
        return getTypeMarker() + toString();
    }
}
