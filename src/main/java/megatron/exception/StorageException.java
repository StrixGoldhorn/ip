package megatron.exception;

/**
 * Reports that Megatron could not access the task storage.
 */
public final class StorageException extends MegatronException {
    private static final String SAVE_ERROR_MESSAGE =
            "Could not save tasks. Check that the data file is writable.";
    private static final String LOAD_ERROR_MESSAGE =
            "Could not load tasks. Check that the data file is readable.";

    /**
     * Creates a storage error with the given message.
     *
     * @param message The user-facing error message.
     * @param cause The storage error that caused the failure.
     */
    private StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an error for a failed save operation.
     *
     * @param cause The storage error that caused the failure.
     */
    public StorageException(Throwable cause) {
        super(SAVE_ERROR_MESSAGE, cause);
    }

    /**
     * Creates an error for a failed load operation.
     *
     * @param cause The storage error that caused the failure.
     * @return The load error.
     */
    public static StorageException forLoad(Throwable cause) {
        return new StorageException(LOAD_ERROR_MESSAGE, cause);
    }
}
