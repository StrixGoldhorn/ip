package megatron.task;

import java.time.LocalDateTime;

/** A task with a specified start and end date or time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Creates an event by parsing supported user start and end date/time values.
     *
     * @param description The event description.
     * @param from The supported event start input.
     * @param to The supported event end input.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = DatetimeValidator.parseToLocalDateTime(from);
        LocalDateTime parsedTo;
        try {
            parsedTo = DatetimeValidator.parseToLocalDateTime(to);
        } catch (IllegalArgumentException exception) {
            // A time-only end value is interpreted on the start date.
            parsedTo = DatetimeValidator.parseToLocalDateTime(this.from.toLocalDate() + " " + to);
        }
        this.to = parsedTo;
    }

    /** Recreates an event from its ISO local date/time storage values.
     *
     * @param description The event description.
     * @param from The stored event start value.
     * @param to The stored event end value.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtra() { return from + "|" + to; }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DatetimeValidator.formatForUser(from)
                + " to: " + DatetimeValidator.formatForUser(to) + ")";
    }
}
