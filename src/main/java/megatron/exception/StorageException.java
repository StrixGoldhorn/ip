package megatron.exception;

/**
 * Reports that Megatron could not save the task list.
 */
public final class StorageException extends MegatronException {
    /**
     * Creates an error for a failed storage operation.
     *
     * @param cause The storage error that caused the failure.
     */
    public StorageException(Throwable cause) {
        super("Could not save tasks. Check that the data file is writable.", cause);
    }
}
