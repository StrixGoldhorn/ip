package megatron.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import megatron.exception.EmptyCommandException;
import megatron.task.TaskList;
import megatron.task.Todo;

/** Tests input handling and console output by {@link Ui}. */
class UiTest {
    @Test
    void constructor_nullScanner_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Ui(null, new PrintStream(new ByteArrayOutputStream())));
    }

    @Test
    void constructor_nullOutput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Ui(new Scanner(""), null));
    }

    @Test
    void input_commands_areReadInOrder() {
        Ui ui = new Ui(new Scanner("todo study\nbye\n"), new PrintStream(new ByteArrayOutputStream()));

        assertTrue(ui.hasNextCommand());
        assertEquals("todo study", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void close_scanner_preventsFurtherInput() {
        Ui ui = new Ui(new Scanner("bye\n"), new PrintStream(new ByteArrayOutputStream()));

        ui.close();

        assertThrows(IllegalStateException.class, ui::hasNextCommand);
    }

    @Test
    void showWelcome_printsBannerAndPrompt() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showWelcome();

        String welcome = output.toString(StandardCharsets.UTF_8);
        assertTrue(welcome.startsWith("____________________________________________________________"));
        assertTrue(welcome.contains("Rawr! I'm Megatron."));
        assertTrue(welcome.contains("What can I do for you?"));
        assertTrue(welcome.endsWith("____________________________________________________________"
                + System.lineSeparator()));
    }

    @Test
    void showDivider_printsDivider() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showDivider();

        assertEquals("____________________________________________________________"
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showGoodbye_printsExitMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showGoodbye();

        assertEquals("     Bye. Hope to see you again soon!" + System.lineSeparator()
                + "____________________________________________________________" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTasks_emptyList_printsNothing() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTasks(new TaskList());

        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTasks_populatedList_printsNumberedTasks() {
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        secondTask.markAsDone();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTasks(new TaskList(List.of(firstTask, secondTask)));

        assertEquals("     1.[T][ ] first" + System.lineSeparator()
                + "     2.[T][X] second" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showMatchingTasks_populatedList_printsHeadingAndNumberedTasks() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showMatchingTasks(new TaskList(List.of(new Todo("read book"))));

        assertEquals("     Here are the matching tasks in your list:" + System.lineSeparator()
                + "     1.[T][ ] read book" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showDatetimeInformation_printsSupportedFormats() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showDatetimeInformation();

        assertEquals(expectedDatetimeInformation(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskAdded_printsTaskAndCount() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskAdded(new Todo("study"), 3);

        assertEquals("     Got it. I've added this task:" + System.lineSeparator()
                + "       [T][ ] study" + System.lineSeparator()
                + "     Now you have 3 tasks in the list." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskMarked_doneTask_printsDoneMessage() {
        Todo task = new Todo("study");
        task.markAsDone();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskMarked(task, true);

        assertEquals("     Nice! I've marked this task as done:" + System.lineSeparator()
                + "       [T][X] study" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskMarked_notDoneTask_printsNotDoneMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskMarked(new Todo("study"), false);

        assertEquals("     OK, I've marked this task as not done yet:" + System.lineSeparator()
                + "       [T][ ] study" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskDeleted_printsTaskAndCount() {
        Todo task = new Todo("study");
        task.markAsDone();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskDeleted(task, 2);

        assertEquals("     Noted. I've removed this task:" + System.lineSeparator()
                + "       [T][X] study" + System.lineSeparator()
                + "     Now you have 2 tasks in the list." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showError_printsExceptionMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showError(new EmptyCommandException());

        assertEquals("     OOPS! Please enter a command." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedDatetimeInformation() {
        return String.join(System.lineSeparator(), List.of(
                "     Supported date/time formats:",
                "     Dates with a year: yyyy-MM-dd, d/M/yyyy",
                "       MMM d yyyy, MMMM d yyyy",
                "       d MMM yyyy, d MMMM yyyy",
                "     Dates without a year: MMM d, MMMM d",
                "       d MMM, d MMMM (current year is used)",
                "     Times: HHmm, H:mm, h[am|pm], h:mm[am|pm]",
                "       Examples: 2145, 21:45, 9pm, 9:45pm",
                "     Weekdays: mon/tue/wed/thu/fri/sat/sun",
                "       Full names are also accepted, for example monday 6pm.",
                "     Missing times default to 0000 (midnight).",
                "     A weekday resolves to its next available occurrence.",
                "     A time-only event end uses the event start date.",
                "       Example: event Exam /from 6 Jul 26 1200 /to 1400",
                "       The above sets an event occuring from 6 Jul 26 1200hrs to 6 Jul 26 1400hrs",
                "     Output format: dd MMM uu, HHmm'hrs' (example: 24 Aug 26, 2145hrs)"))
                + System.lineSeparator();
    }
}
