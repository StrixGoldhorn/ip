package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.MegatronException;
import megatron.exception.TaskNotFoundException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/**
 * Tests task removal, persistence, and display by {@link DeleteCommand}.
 */
class DeleteCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_middleTask_removesSelectedTaskSavesAndDisplaysIt() throws MegatronException {
        TaskList tasks = new TaskList(List.of(
                new Todo("first"), new Todo("second"), new Todo("third")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new DeleteCommand(2).execute(tasks, createUi(output), storage);

        TaskList savedTasks = storage.load();
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.getTask(1).getDescription());
        assertEquals("third", tasks.getTask(2).getDescription());
        assertEquals(2, savedTasks.size());
        assertEquals("first", savedTasks.getTask(1).getDescription());
        assertEquals("third", savedTasks.getTask(2).getDescription());
        assertEquals(expectedDeletedOutput("[T][ ] second", 2), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_onlyTask_removesTaskAndSavesEmptyList() throws MegatronException {
        Todo task = new Todo("only task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new DeleteCommand(1).execute(tasks, createUi(output), storage);

        assertEquals(0, tasks.size());
        assertEquals(0, storage.load().size());
        assertEquals(expectedDeletedOutput("[T][X] only task", 0),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_zeroTaskNumber_throwsWithoutSavingOrDisplaying() throws MegatronException {
        assertInvalidTaskNumber(0);
    }

    @Test
    void execute_negativeTaskNumber_throwsWithoutSavingOrDisplaying() throws MegatronException {
        assertInvalidTaskNumber(-1);
    }

    @Test
    void execute_taskNumberAboveSize_throwsWithoutSavingOrDisplaying() throws MegatronException {
        assertInvalidTaskNumber(2);
    }

    private void assertInvalidTaskNumber(int taskNumber) throws MegatronException {
        TaskList tasks = new TaskList(List.of(new Todo("task")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertThrows(TaskNotFoundException.class,
                () -> new DeleteCommand(taskNumber).execute(tasks, createUi(output), storage));

        assertEquals(1, tasks.size());
        assertEquals("task", tasks.getTask(1).getDescription());
        assertFalse(Files.exists(storageFile));
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedDeletedOutput(String taskText, int taskCount) {
        return "     Noted. I've removed this task:" + System.lineSeparator()
                + "       " + taskText + System.lineSeparator()
                + "     Now you have " + taskCount + " tasks in the list." + System.lineSeparator();
    }
}
