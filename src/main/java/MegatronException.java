/** Base class for input errors reported by the Megatron chatbot. */
public class MegatronException extends Exception {
    /** Creates an exception with a message that can be shown to the user. */
    public MegatronException(String message) {
        super(message);
    }
}

/** Reports that a task description was missing. */
class EmptyDescriptionException extends MegatronException {
    EmptyDescriptionException() { super("A todo description cannot be empty."); }
}

/** Reports that the user entered a blank command. */
class EmptyCommandException extends MegatronException {
    EmptyCommandException() { super("Please enter a command."); }
}

/** Reports that the user entered a command that Megatron does not support. */
class UnknownCommandException extends MegatronException {
    UnknownCommandException() {
        super("I do not recognise that command. Try todo, deadline, event, list, mark, or unmark.");
    }
}

/** Reports that a deadline or event does not follow its required format. */
class InvalidTaskFormatException extends MegatronException {
    InvalidTaskFormatException(String usage) { super("Use: " + usage); }
}

/** Reports that a mark or unmark command does not contain a valid number. */
class InvalidTaskNumberException extends MegatronException {
    InvalidTaskNumberException() { super("Please provide a valid task number."); }
}

/** Reports that a requested task number is outside the current list. */
class TaskNotFoundException extends MegatronException {
    TaskNotFoundException() { super("That task number does not exist."); }
}

/** Reports that no more tasks can be stored. */
class TaskListFullException extends MegatronException {
    TaskListFullException() { super("The task list is full. Remove a task before adding another."); }
}
