package megatron.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.TaskNotFoundException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.task.Todo;

/** Tests persistent task storage without using the production data file. */
class TaskStorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyTaskList() {
        TaskStorage storage = new TaskStorage(tempDirectory.resolve("missing.csv").toString());

        assertEquals(0, storage.load().size());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesDataAndEscapesCsv() throws IOException, TaskNotFoundException {
        Path file = tempDirectory.resolve("nested").resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(file.toString());
        Todo todo = new Todo("buy, \"fresh\" milk");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 6, 14, 0));
        Event event = new Event("review", LocalDateTime.of(2026, 8, 7, 15, 0),
                LocalDateTime.of(2026, 8, 7, 16, 30));

        storage.save(new TaskList(List.of(todo, deadline, event)));

        assertEquals(List.of(
                "type,done,description,extra",
                "\"T\",1,\"buy, \"\"fresh\"\" milk\",\"\"",
                "\"D\",0,\"submit report\",\"2026-08-06T14:00\"",
                "\"E\",0,\"review\",\"2026-08-07T15:00|2026-08-07T16:30\""),
                Files.readAllLines(file));

        TaskList loadedTasks = storage.load();
        Task loadedTodo = loadedTasks.getTask(1);
        Task loadedDeadline = loadedTasks.getTask(2);
        Task loadedEvent = loadedTasks.getTask(3);
        assertAll(
                () -> assertEquals(3, loadedTasks.size()),
                () -> assertInstanceOf(Todo.class, loadedTodo),
                () -> assertEquals("buy, \"fresh\" milk", loadedTodo.getDescription()),
                () -> assertTrue(loadedTodo.isDone()),
                () -> assertInstanceOf(Deadline.class, loadedDeadline),
                () -> assertEquals("submit report", loadedDeadline.getDescription()),
                () -> assertEquals("2026-08-06T14:00", loadedDeadline.getExtra()),
                () -> assertFalse(loadedDeadline.isDone()),
                () -> assertInstanceOf(Event.class, loadedEvent),
                () -> assertEquals("review", loadedEvent.getDescription()),
                () -> assertEquals("2026-08-07T15:00|2026-08-07T16:30", loadedEvent.getExtra()),
                () -> assertFalse(loadedEvent.isDone()));
    }

    @Test
    void load_malformedRows_skipsInvalidRowsAndLoadsValidRows() throws IOException {
        Path file = tempDirectory.resolve("tasks.csv");
        Files.write(file, List.of(
                "type,done,description,extra",
                "T,0,valid todo,",
                "D,1,valid deadline,2026-08-06T14:00",
                "E,0,valid event,2026-08-07T15:00|2026-08-07T16:30",
                "invalid,row",
                "X,0,unknown type,",
                "T,2,invalid status,",
                "T,0,,",
                "T,0,unexpected extra,value",
                "D,0,invalid deadline,not-a-date",
                "D,0,missing deadline,",
                "E,0,missing end,2026-08-07T15:00",
                "E,0,invalid start,invalid|2026-08-07T16:30"));
        TaskStorage storage = new TaskStorage(file.toString());

        TaskList loadedTasks = storage.load();

        assertAll(
                () -> assertEquals(3, loadedTasks.size()),
                () -> assertEquals("valid todo", loadedTasks.getTask(1).getDescription()),
                () -> assertEquals("valid deadline", loadedTasks.getTask(2).getDescription()),
                () -> assertTrue(loadedTasks.getTask(2).isDone()),
                () -> assertEquals("valid event", loadedTasks.getTask(3).getDescription()));
    }

    @Test
    void loadAndSave_directoryPath_doesNotPropagateIoFailure() throws IOException {
        Path directory = Files.createDirectory(tempDirectory.resolve("data"));
        TaskStorage storage = new TaskStorage(directory.toString());

        assertAll(
                () -> assertDoesNotThrow(storage::load),
                () -> assertDoesNotThrow(() -> storage.save(new TaskList())));
    }
}
