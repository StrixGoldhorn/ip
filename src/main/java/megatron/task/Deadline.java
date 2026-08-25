package megatron.task;

import java.time.LocalDateTime;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline by parsing a supported user date/time value.
     *
     * @param description The deadline description.
     * @param by The supported deadline date/time input.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = DatetimeValidator.parseToLocalDateTime(by);
    }

    /**
     * Recreates a deadline from its ISO local date/time storage value.
     *
     * @param description The deadline description.
     * @param by The stored local date/time value.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtra() {
        return by.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DatetimeValidator.formatForUser(by) + ")";
    }
}
