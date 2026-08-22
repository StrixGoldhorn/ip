package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.storage.TaskStorage;
import megatron.task.Deadline;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/** Tests task search and display behavior by {@link FindCommand}. */
class FindCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_matchingKeyword_displaysMatchesInListOrderWithoutSaving() {
        Todo firstMatch = new Todo("read book");
        firstMatch.markAsDone();
        TaskList tasks = new TaskList(List.of(
                firstMatch,
                new Todo("buy groceries"),
                new Deadline("return book", "2026-06-06")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FindCommand("book").execute(tasks, createUi(output), new TaskStorage(storageFile.toString()));

        assertEquals("     Here are the matching tasks in your list:" + System.lineSeparator()
                + "     1.[T][X] read book" + System.lineSeparator()
                + "     2.[D][ ] return book (by: 06 Jun 26, 0000hrs)" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
        assertEquals(3, tasks.size());
        assertFalse(Files.exists(storageFile));
    }

    @Test
    void execute_noMatchingKeyword_displaysNoMatchMessage() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FindCommand("movie").execute(tasks, createUi(output),
                new TaskStorage(tempDirectory.resolve("tasks.csv").toString()));

        assertEquals("     No tasks found matching that description." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
