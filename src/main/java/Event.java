/** A task with a specified start and end date or time. */
public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getExtra() { return from + "|" + to; }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
