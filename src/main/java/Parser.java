import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Interprets user commands and creates tasks from add commands. */
public final class Parser {
    private static final List<String> AVAILABLE_COMMANDS = new ArrayList<>(List.of(
            "todo", "deadline", "event", "list", "mark", "unmark", "delete", "datetime-help"));

    /** The kinds of commands that the application supports. */
    public enum CommandType {
        BYE,
        LIST,
        DATETIME_HELP,
        ADD,
        MARK,
        UNMARK,
        DELETE
    }

    /** Immutable result of parsing one user command. */
    public static final class Command {
        private final CommandType type;
        private final String originalText;
        private final Integer taskNumber;

        private Command(CommandType type, String originalText, Integer taskNumber) {
            this.type = type;
            this.originalText = originalText;
            this.taskNumber = taskNumber;
        }

        /** Returns the category of this command. */
        public CommandType getType() {
            return type;
        }

        /** Returns the original text for an add command. */
        public String getOriginalText() {
            return originalText;
        }

        /** Returns the task number, or null when the command has no number. */
        public Integer getTaskNumber() {
            return taskNumber;
        }
    }

    /** Converts raw input into an immutable command. */
    public Command parse(String input) throws MegatronException {
        Objects.requireNonNull(input);
        if (input.equals("bye")) {
            return new Command(CommandType.BYE, null, null);
        }
        if (input.equals("list")) {
            return new Command(CommandType.LIST, null, null);
        }
        if (input.equals("datetime-help")) {
            return new Command(CommandType.DATETIME_HELP, null, null);
        }
        if (input.startsWith("mark ")) {
            return new Command(CommandType.MARK, null, parseTaskNumber(input));
        }
        if (input.startsWith("unmark ")) {
            return new Command(CommandType.UNMARK, null, parseTaskNumber(input));
        }
        if (input.equals("delete")) {
            return new Command(CommandType.DELETE, null, null);
        }
        if (input.startsWith("delete ")) {
            return new Command(CommandType.DELETE, null, parseTaskNumber(input));
        }
        if (input.isBlank()) {
            throw new EmptyCommandException();
        }
        return new Command(CommandType.ADD, input, null);
    }

    /** Creates the task described by an add command. */
    public Task createTask(Command command) throws MegatronException {
        Objects.requireNonNull(command);
        String text = command.getOriginalText();
        if (text.equals("todo")) {
            throw new EmptyDescriptionException();
        }
        if (text.startsWith("todo ")) {
            String description = text.substring(5).trim();
            if (description.isEmpty()) {
                throw new EmptyDescriptionException();
            }
            return new Todo(description);
        }
        if (text.startsWith("deadline ")) {
            String[] parts = text.substring(9).split(" /by ", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                throw new InvalidTaskFormatException("deadline <description> /by <date>.");
            }
            try {
                return new Deadline(parts[0].trim(), parts[1].trim());
            } catch (IllegalArgumentException exception) {
                throw new InvalidTaskFormatException("deadline <description> /by <valid date/time>. "
                        + "Use datetime-help to view supported date/time formats.");
            }
        }
        if (text.startsWith("event ")) {
            String[] parts = text.substring(6).split(" /from ", 2);
            if (parts.length != 2) {
                throw new InvalidTaskFormatException("event <description> /from <start> /to <end>.");
            }
            String[] times = parts[1].split(" /to ", 2);
            if (times.length != 2 || parts[0].trim().isEmpty() || times[0].trim().isEmpty()
                    || times[1].trim().isEmpty()) {
                throw new InvalidTaskFormatException("event <description> /from <start> /to <end>.");
            }
            try {
                return new Event(parts[0].trim(), times[0].trim(), times[1].trim());
            } catch (IllegalArgumentException exception) {
                throw new InvalidTaskFormatException("event <description> /from <valid start> /to <valid end>. "
                        + "Use datetime-help to view supported date/time formats.");
            }
        }
        throw new UnknownCommandException(AVAILABLE_COMMANDS);
    }

    /** Extracts a task number from a mark, unmark, or delete command. */
    private static int parseTaskNumber(String input) throws InvalidTaskNumberException {
        try {
            return Integer.parseInt(input.substring(input.indexOf(' ') + 1).trim());
        } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
            throw new InvalidTaskNumberException();
        }
    }
}
