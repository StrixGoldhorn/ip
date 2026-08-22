package megatron.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import megatron.exception.EmptyDescriptionException;
import megatron.exception.InvalidTaskFormatException;
import megatron.exception.MegatronException;
import megatron.exception.UnknownCommandException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Todo;

/** Tests task creation from user input by {@link Parser}. */
class ParserTest {
    private final Parser parser = new Parser();

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
