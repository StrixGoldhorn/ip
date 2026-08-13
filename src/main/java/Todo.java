/** A task with no date or time attached to it. */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeMarker() {
        return "[T]";
    }
}
