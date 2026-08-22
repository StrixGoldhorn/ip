package megatron.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import megatron.exception.EmptyCommandException;
import megatron.exception.EmptyDescriptionException;
import megatron.exception.InvalidTaskFormatException;
import megatron.exception.InvalidTaskNumberException;
import megatron.exception.MegatronException;
import megatron.exception.UnknownCommandException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Todo;

/** Tests command parsing and task creation by {@link Parser}. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_exactCommands_returnsMatchingCommandTypes() throws MegatronException {
        assertAll(
                () -> assertInstanceOf(ExitCommand.class, parser.parse("bye")),
                () -> assertInstanceOf(ListCommand.class, parser.parse("list")),
                () -> assertInstanceOf(DatetimeHelpCommand.class, parser.parse("datetime-help")));
    }

    @Test
    void parse_numberedCommands_returnsMatchingCommandTypes() throws MegatronException {
        assertAll(
                () -> assertInstanceOf(MarkCommand.class, parser.parse("mark 2")),
                () -> assertInstanceOf(UnmarkCommand.class, parser.parse("unmark   2")),
                () -> assertInstanceOf(DeleteCommand.class, parser.parse("delete 2")));
    }

    @Test
    void parse_addTaskInput_returnsAddCommand() throws MegatronException {
        assertAll(
                () -> assertInstanceOf(AddCommand.class, parser.parse("todo buy milk")),
                () -> assertInstanceOf(AddCommand.class,
                        parser.parse("deadline submit report /by 2026-08-06")),
                () -> assertInstanceOf(AddCommand.class,
                        parser.parse("event meeting /from 1400 /to 1600")));
    }

    @Test
    void parse_blankInput_throwsEmptyCommandException() {
        assertAll(
                () -> assertThrows(EmptyCommandException.class,
                        () -> parser.parse("")),
                () -> assertThrows(EmptyCommandException.class,
                        () -> parser.parse("   ")));
    }

    @Test
    void parse_invalidTaskNumber_throwsInvalidTaskNumberException() {
        assertAll(
                () -> assertThrows(InvalidTaskNumberException.class,
                        () -> parser.parse("mark abc")),
                () -> assertThrows(InvalidTaskNumberException.class,
                        () -> parser.parse("mark ")),
                () -> assertThrows(InvalidTaskNumberException.class,
                        () -> parser.parse("unmark 1.5")),
                () -> assertThrows(InvalidTaskNumberException.class,
                        () -> parser.parse("delete")),
                () -> assertThrows(InvalidTaskNumberException.class,
                        () -> parser.parse("delete ")));
    }

    @Test
    void parse_exactCommandWithUnexpectedSuffix_returnsAddCommand() throws MegatronException {
        assertAll(
                () -> assertInstanceOf(AddCommand.class, parser.parse("bye now")),
                () -> assertInstanceOf(AddCommand.class, parser.parse("list now")),
                () -> assertInstanceOf(AddCommand.class, parser.parse("datetime-help now")));
    }

    @Test
    void parse_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    void createTask_validTodo_createsTodoWithTrimmedDescription() throws MegatronException {
        Todo todo = assertInstanceOf(Todo.class, parser.createTask("todo    buy milk   "));

        assertAll(
                () -> assertEquals("buy milk", todo.getDescription()),
                () -> assertEquals("T", todo.getTypeCode()),
                () -> assertEquals("", todo.getExtra()));
    }

    @Test
    void createTask_validDeadline_createsDeadlineWithParsedDateTime() throws MegatronException {
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.createTask("deadline submit report /by 2026-08-06 1400"));

        assertAll(
                () -> assertEquals("submit report", deadline.getDescription()),
                () -> assertEquals("D", deadline.getTypeCode()),
                () -> assertEquals("2026-08-06T14:00", deadline.getExtra()));
    }

    @Test
    void createTask_eventWithTimeOnlyEnd_usesStartDateForEnd() throws MegatronException {
        Event event = assertInstanceOf(Event.class,
                parser.createTask("event review /from 2026-08-06 1400 /to 4:30pm"));

        assertAll(
                () -> assertEquals("review", event.getDescription()),
                () -> assertEquals("E", event.getTypeCode()),
                () -> assertEquals("2026-08-06T14:00|2026-08-06T16:30", event.getExtra()));
    }

    @Test
    void createTask_emptyTodoDescription_throwsEmptyDescriptionException() {
        assertAll(
                () -> assertThrows(EmptyDescriptionException.class,
                        () -> parser.createTask("todo")),
                () -> assertThrows(EmptyDescriptionException.class,
                        () -> parser.createTask("todo   ")));
    }

    @Test
    void createTask_invalidDeadline_throwsInvalidTaskFormatException() {
        assertAll(
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("deadline report")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("deadline  /by 2026-08-06")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("deadline report /by ")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("deadline report /by 31/2/2026")));
    }

    @Test
    void createTask_invalidEvent_throwsInvalidTaskFormatException() {
        assertAll(
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("event review")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("event review /from 2026-08-06 1400")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("event  /from 2026-08-06 1400 /to 1600")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("event review /from /to 1600")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("event review /from 2026-08-06 1400 /to ")),
                () -> assertThrows(InvalidTaskFormatException.class,
                        () -> parser.createTask("event review /from invalid /to 1600")));
    }

    @Test
    void createTask_unknownTaskType_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> parser.createTask("reminder buy milk"));
    }

    @Test
    void createTask_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.createTask(null));
    }
}
