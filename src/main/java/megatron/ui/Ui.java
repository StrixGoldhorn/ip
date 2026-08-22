package megatron.ui;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

import megatron.exception.MegatronException;
import megatron.task.Task;
import megatron.task.TaskList;

/**
 * Handles all console input and output used by Megatron.
 *
 * <p>This class keeps user-facing text in one place. The application logic
 * can therefore work with commands and tasks without knowing how they are
 * displayed.</p>
 */
public final class Ui {
    private static final String BANNER = "   __  ___              __              \n"
            + "  /  |/  /__ ___ ____ _/ /________  ___ \n"
            + " / /|_/ / -_) _ `/ _ `/ __/ __/ _ \\/ _ \\\n"
            + "/_/  /_/\\__/\\_, /\\_,_/\\__/_/  \\___/_//_/\n"
            + "           /___/                        ";
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI connected to the standard input and output streams. */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /** Creates a UI using the supplied input and output streams.
     *
     * @param scanner The input scanner.
     * @param output The output stream.
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = Objects.requireNonNull(scanner);
        this.output = Objects.requireNonNull(output);
    }

    /** Returns whether another complete command is available.
     *
     * @return True if another command line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and returns the next complete command line.
     *
     * @return The next command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the welcome banner shown when the application starts. */
    public void showWelcome() {
        showDivider();
        output.println(BANNER);
        output.println("     Rawr! I'm Megatron.");
        output.println("     What can I do for you?");
        showDivider();
    }

    /** Displays the divider used between console responses. */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /** Displays the message shown when the user exits. */
    public void showGoodbye() {
        output.println("     Bye. Hope to see you again soon!");
        showDivider();
    }

    /** Displays every stored task in the order in which it was added.
     *
     * @param tasks The tasks to display.
     */
    public void showTasks(TaskList tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            output.println("     " + taskNumber + "." + task.displayText());
            taskNumber++;
        }
    }

    /** Displays tasks that match a find command. */
    public void showMatchingTasks(TaskList matchingTasks) {
        if (matchingTasks.size() == 0) {
            output.println("     No tasks found matching that description.");
            return;
        }

        output.println("     Here are the matching tasks in your list:");
        showTasks(matchingTasks);
    }

    /** Displays the supported date/time inputs and interpretation rules. */
    public void showDatetimeInformation() {
        output.println("     Supported date/time formats:");
        output.println("     Dates with a year: yyyy-MM-dd, d/M/yyyy");
        output.println("       MMM d yyyy, MMMM d yyyy");
        output.println("       d MMM yyyy, d MMMM yyyy");
        output.println("     Dates without a year: MMM d, MMMM d");
        output.println("       d MMM, d MMMM (current year is used)");
        output.println("     Times: HHmm, H:mm, h[am|pm], h:mm[am|pm]");
        output.println("       Examples: 2145, 21:45, 9pm, 9:45pm");
        output.println("     Weekdays: mon/tue/wed/thu/fri/sat/sun");
        output.println("       Full names are also accepted, for example monday 6pm.");
        output.println("     Missing times default to 0000 (midnight).");
        output.println("     A weekday resolves to its next available occurrence.");
        output.println("     A time-only event end uses the event start date.");
        output.println("       Example: event Exam /from 6 Jul 26 1200 /to 1400");
        output.println("       The above sets an event occuring from 6 Jul 26 1200hrs to 6 Jul 26 1400hrs");
        output.println("     Output format: dd MMM uu, HHmm'hrs' (example: 24 Aug 26, 2145hrs)");
    }

    /** Displays confirmation after a task is added.
     *
     * @param task The added task.
     * @param taskCount The current number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("     Got it. I've added this task:");
        output.println("       " + task.displayText());
        output.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays confirmation after a task's completion status changes.
     *
     * @param task The task whose status changed.
     * @param markedDone Whether the task is now done.
     */
    public void showTaskMarked(Task task, boolean markedDone) {
        if (markedDone) {
            output.println("     Nice! I've marked this task as done:");
        } else {
            output.println("     OK, I've marked this task as not done yet:");
        }
        output.println("       " + task.displayText());
    }

    /** Displays confirmation after a task is deleted.
     *
     * @param task The deleted task.
     * @param taskCount The current number of tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("     Noted. I've removed this task:");
        output.println("       " + task.displayText());
        output.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays an error message for a rejected command.
     *
     * @param exception The command error to display.
     */
    public void showError(MegatronException exception) {
        output.println("     OOPS! " + exception.getMessage());
    }

    /** Closes the input stream owned by this UI. */
    public void close() {
        scanner.close();
    }
}
