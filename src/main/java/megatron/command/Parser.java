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

/**
 * Interprets user commands and creates tasks from add commands.
 */
public final class Parser {
    private static final List<String> AVAILABLE_COMMANDS = new ArrayList<>(List.of(
            "todo", "deadline", "event", "list", "find", "mark", "unmark", "delete", "datetime-help"));

    /**
     * Creates a parser for Megatron commands.
     */
    public Parser() { }

    /**
     * Converts raw input into an executable command.
     *
     * @param input The raw user input.
     * @return The executable command.
     * @throws MegatronException If the input is empty or has an invalid task number.
     */
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
            case "find":
                if (input.startsWith("find ")) {
                    String keyword = input.substring(5).trim();
                    if (!keyword.isEmpty()) {
                        return new FindCommand(keyword);
                    }
                }
                throw new InvalidTaskFormatException("find <keyword>.");
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

    /**
     * Creates the task described by an add command.
     *
     * @param text The add command text.
     * @return The created task.
     * @throws MegatronException If the task description or format is invalid.
     */
    public Task createTask(String text) throws MegatronException {
        Objects.requireNonNull(text);

        if (text.equals("todo") || text.startsWith("todo ")) {
            return createTodo(text);
        }

        if (text.startsWith("deadline ")) {
            return createDeadline(text);
        }

        if (text.startsWith("event ")) {
            return createEvent(text);
        }

        throw new UnknownCommandException(AVAILABLE_COMMANDS);
    }

    /**
     * Creates a todo task from an add command.
     *
     * @param text The complete todo command.
     * @return The created todo task.
     * @throws EmptyDescriptionException If the todo description is empty.
     */
    private static Todo createTodo(String text) throws EmptyDescriptionException {
        String description = text.substring(4).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException();
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline task from an add command.
     *
     * @param text The complete deadline command.
     * @return The created deadline task.
     * @throws InvalidTaskFormatException If the deadline command or date/time is invalid.
     */
    private static Deadline createDeadline(String text) throws InvalidTaskFormatException {
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

    /**
     * Creates an event task from an add command.
     *
     * @param text The complete event command.
     * @return The created event task.
     * @throws InvalidTaskFormatException If the event command or date/time is invalid.
     */
    private static Event createEvent(String text) throws InvalidTaskFormatException {
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
            throw new InvalidTaskFormatException(
                    "event <description> /from <valid start> /to <valid end>. "
                            + "Use datetime-help to view supported date/time formats.");
        }
    }

    /**
     * Extracts a task number from a mark, unmark, or delete command.
     *
     * @param input The complete command text.
     * @return The parsed one-based task number.
     * @throws InvalidTaskNumberException If the command does not contain a valid number.
     */
    private static int parseTaskNumber(String input) throws InvalidTaskNumberException {
        try {
            return Integer.parseInt(input.substring(input.indexOf(' ') + 1).trim());
        } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
            throw new InvalidTaskNumberException();
        }
    }
}
