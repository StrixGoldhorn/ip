/** Represents the completion status of a task. */
public enum TaskStatus {
    NOT_DONE("[ ]"),
    DONE("[X]");

    private final String marker;

    TaskStatus(String marker) {
        this.marker = marker;
    }

    /** Returns the status marker shown in the user interface. */
    public String getMarker() {
        return marker;
    }
}
