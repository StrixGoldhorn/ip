import java.time.LocalDateTime;

/** A task that must be completed by a specified date or time. */
public class Deadline extends Task {
    private final LocalDateTime by;

    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = DatetimeValidator.parseToLocalDateTime(by);
    }

    /** Recreates a deadline from its ISO local date/time storage value. */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    @Override
    public String getExtra() { return by.toString(); }

    @Override
    public String toString() {
        return super.toString() + " (by: " + DatetimeValidator.formatForUser(by) + ")";
    }
}
