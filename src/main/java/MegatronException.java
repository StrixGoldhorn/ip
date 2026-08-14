import java.util.List;

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
    /** Creates an error that lists the commands currently supported by Megatron. */
    UnknownCommandException(List<String> availableCommands) {
        super("I do not recognise that command. Try " + formatCommands(availableCommands) + ".");
    }

    /** Formats command names as a readable list with "or" before the final command. */
    private static String formatCommands(List<String> availableCommands) {
        if (availableCommands.size() == 1) {
            return availableCommands.get(0);
        }
        String firstCommands = String.join(", ", availableCommands.subList(0, availableCommands.size() - 1));
        return firstCommands + ", or " + availableCommands.get(availableCommands.size() - 1);
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
