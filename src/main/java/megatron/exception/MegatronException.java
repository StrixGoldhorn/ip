package megatron.exception;

/** Base class for input errors reported by the Megatron chatbot. */
public class MegatronException extends Exception {
    /** Creates an exception with a message that can be shown to the user. */
    public MegatronException(String message) {
        super(message);
    }
}
