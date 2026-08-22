package megatron.exception;

/** Reports that a deadline or event does not follow its required format. */
public final class InvalidTaskFormatException extends MegatronException {
    /** Creates an error that shows the required command format.
     *
     * @param usage The required command format.
     */
    public InvalidTaskFormatException(String usage) {
        super("Use: " + usage);
    }
}
