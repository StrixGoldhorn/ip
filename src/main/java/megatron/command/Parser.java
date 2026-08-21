package megatron.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import megatron.exception.EmptyCommandException;
import megatron.exception.EmptyDescriptionException;
import megatron.exception.InvalidTaskFormatException;
import megatron.exception.InvalidTaskNumberException;
import megatron.exception.MegatronException;
import megatron.exception.UnknownCommandException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.Todo;

/** Interprets user commands and creates tasks from add commands. */
public final class Parser {
    private static final List<String> AVAILABLE_COMMANDS = new ArrayList<>(List.of(
            "todo", "deadline", "event", "list", "mark", "unmark", "delete", "datetime-help"));

    /** Converts raw input into an executable command. */
    public Command parse(String input) throws MegatronException {
        Objects.requireNonNull(input);
        int firstSpace = input.indexOf(' ');
        String commandWord = firstSpace == -1 ? input : input.substring(0, firstSpace);

        switch (commandWord) {
        case "bye":
            if (input.equals("bye")) {
                return new ExitCommand();
            }
            break;
        case "list":
            if (input.equals("list")) {
                return new ListCommand();
            }
            break;
        case "datetime-help":
            if (input.equals("datetime-help")) {
                return new DatetimeHelpCommand();
            }
            break;
        case "mark":
            if (input.startsWith("mark ")) {
                return new MarkCommand(parseTaskNumber(input));
            }
            break;
        case "unmark":
            if (input.startsWith("unmark ")) {
                return new UnmarkCommand(parseTaskNumber(input));
            }
            break;
        case "delete":
            return new DeleteCommand(parseTaskNumber(input));
        default:
            break;
        }

        if (input.isBlank()) {
            throw new EmptyCommandException();
        }
        return new AddCommand(input, this);
    }

    /** Creates the task described by an add command. */
    public Task createTask(String text) throws MegatronException {
        Objects.requireNonNull(text);
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
