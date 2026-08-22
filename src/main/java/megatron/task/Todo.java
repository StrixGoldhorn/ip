package megatron.task;

/** A task with no date or time attached to it. */
public class Todo extends Task {
    /** Creates a todo task with the given description. */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
