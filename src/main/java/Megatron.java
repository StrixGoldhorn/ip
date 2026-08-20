import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Starts the Megatron chatbot application.
 */
public class Megatron {
    private static final int MAX_TASKS = 100;
    private static final List<String> AVAILABLE_COMMANDS = new ArrayList<>(List.of(
            "todo", "deadline", "event", "list", "mark", "unmark", "delete", "datetime-help"));

    public static void main(String[] args) {
        String banner = "   __  ___              __              \n"
                + "  /  |/  /__ ___ ____ _/ /________  ___ \n"
                + " / /|_/ / -_) _ `/ _ `/ __/ __/ _ \\/ _ \\\n"
                + "/_/  /_/\\__/\\_, /\\_,_/\\__/_/  \\___/_//_/\n"
                + "           /___/                        ";
        String divider = "____________________________________________________________";

        System.out.println(divider);
        System.out.println(banner);
        System.out.println("     Rawr! I'm Megatron.");
        System.out.println("     What can I do for you?");
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        TaskStorage storage = new TaskStorage(args.length > 0 ? args[0] : "data/megatron.csv");
        List<Task> tasks = storage.load();
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            }

            try {
                if (command.equals("list")) {
                    printTasks(tasks);
                } else if (command.equals("datetime-help")) {
                    printDatetimeInformation();
                } else if (command.startsWith("mark ")) {
                    markTask(tasks, command, true, storage);
                } else if (command.startsWith("unmark ")) {
                    markTask(tasks, command, false, storage);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    deleteTask(tasks, command, storage);
                } else if (!command.isBlank()) {
                    if (tasks.size() == MAX_TASKS) {
                        throw new TaskListFullException();
                    }
                    Task newTask = createTask(command);
                    tasks.add(newTask);
                    storage.save(tasks);
                    System.out.println("     Got it. I've added this task:");
                    System.out.println("       " + newTask.displayText());
                    System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                } else {
                    throw new EmptyCommandException();
                }
            } catch (MegatronException exception) {
                System.out.println("     OOPS! " + exception.getMessage());
            }
            System.out.println(divider);
        }
        scanner.close();
    }

    /**
     * Prints all stored tasks in the order in which the user entered them.
     *
     * @param tasks the collection containing the stored tasks
     */
    private static void printTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i).displayText());
        }
    }

    /** Prints the supported date/time inputs and the rules used to interpret them. */
    private static void printDatetimeInformation() {
        System.out.println("     Supported date/time formats:");
        System.out.println("     Dates with a year: yyyy-MM-dd, d/M/yyyy");
        System.out.println("       MMM d yyyy, MMMM d yyyy");
        System.out.println("       d MMM yyyy, d MMMM yyyy");
        System.out.println("     Dates without a year: MMM d, MMMM d");
        System.out.println("       d MMM, d MMMM (current year is used)");
        System.out.println("     Times: HHmm, H:mm, h[am|pm], h:mm[am|pm]");
        System.out.println("       Examples: 2145, 21:45, 9pm, 9:45pm");
        System.out.println("     Weekdays: mon/tue/wed/thu/fri/sat/sun");
        System.out.println("       Full names are also accepted, for example monday 6pm.");
        System.out.println("     Missing times default to 0000 (midnight).");
        System.out.println("     A weekday resolves to its next available occurrence.");
        System.out.println("     A time-only event end uses the event start date.");
        System.out.println("       Example: event Exam /from 6 Jul 26 1200 /to 1400");
        System.out.println("       The above sets an event occuring from 6 Jul 26 1200hrs to 6 Jul 26 1400hrs");
        System.out.println("     Output format: dd MMM uu, HHmm'hrs' (example: 24 Aug 26, 2145hrs)");
    }

    /** Converts a user command into the matching task subtype. */
    private static Task createTask(String command) throws MegatronException {
        if (command.equals("todo")) {
            throw new EmptyDescriptionException();
        }
        if (command.startsWith("todo ")) {
            String description = command.substring(5).trim();
            if (description.isEmpty()) {
                throw new EmptyDescriptionException();
            }
            return new Todo(description);
        }
        if (command.startsWith("deadline ")) {
            String[] parts = command.substring(9).split(" /by ", 2);
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
        if (command.startsWith("event ")) {
            String[] parts = command.substring(6).split(" /from ", 2);
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

    /**
     * Changes the completion status of a task selected by its list number.
     *
     * @param tasks the collection containing the stored tasks
     * @param command the mark or unmark command
     * @param shouldMarkDone whether the task should be marked as done
     */
    private static void markTask(List<Task> tasks, String command, boolean shouldMarkDone, TaskStorage storage)
            throws MegatronException {
        try {
            int taskNumber = Integer.parseInt(command.substring(command.indexOf(' ') + 1).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TaskNotFoundException();
            }

            Task task = tasks.get(taskNumber - 1);
            if (shouldMarkDone) {
                task.markAsDone();
                System.out.println("     Nice! I've marked this task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("     OK, I've marked this task as not done yet:");
            }
            storage.save(tasks);
            System.out.println("       " + task.displayText());
        } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
            throw new InvalidTaskNumberException();
        }
    }

    /**
     * Removes a task selected by its list number.
     *
     * @param tasks the collection containing the stored tasks
     * @param command the delete command
     * @throws MegatronException if the command does not contain a valid task number
     */
    private static void deleteTask(List<Task> tasks, String command, TaskStorage storage) throws MegatronException {
        try {
            int taskNumber = Integer.parseInt(command.substring(command.indexOf(' ') + 1).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TaskNotFoundException();
            }

            Task removedTask = tasks.remove(taskNumber - 1);
            storage.save(tasks);
            System.out.println("     Noted. I've removed this task:");
            System.out.println("       " + removedTask.displayText());
            System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
        } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
            throw new InvalidTaskNumberException();
        }
    }

}
