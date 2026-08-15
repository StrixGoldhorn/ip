/** Identifies the kind of task stored in Megatron. */
public enum TaskType {
    TODO("[T]"),
    DEADLINE("[D]"),
    EVENT("[E]"),
    /** Marker used by the base Task class when it is instantiated directly. */
    TASK("[-]");

    private final String marker;

    TaskType(String marker) {
        this.marker = marker;
    }

    /** Returns the marker shown before a task in the user interface. */
    public String getMarker() {
        return marker;
    }
}
