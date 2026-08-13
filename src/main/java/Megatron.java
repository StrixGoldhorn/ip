import java.util.Scanner;

/**
 * Starts the Megatron chatbot application.
 */
public class Megatron {
    private static final int MAX_TASKS = 100;

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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            }

            if (command.equals("list")) {
                printTasks(tasks, taskCount);
            } else if (command.startsWith("mark ")) {
                taskCount = markTask(tasks, taskCount, command, true);
            } else if (command.startsWith("unmark ")) {
                taskCount = markTask(tasks, taskCount, command, false);
            } else if (!command.isBlank() && taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("     added: " + command);
            }
            System.out.println(divider);
        }
        scanner.close();
    }

    /**
     * Prints all stored tasks in the order in which the user entered them.
     *
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of stored tasks
     */
    private static void printTasks(Task[] tasks, int taskCount) {
        for (int i = 0; i < taskCount; i++) {
            System.out.println("     " + (i + 1) + ". " + tasks[i]);
        }
    }

    /**
     * Changes the completion status of a task selected by its list number.
     *
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of stored tasks
     * @param command the mark or unmark command
     * @param shouldMarkDone whether the task should be marked as done
     * @return the unchanged number of tasks
     */
    private static int markTask(Task[] tasks, int taskCount, String command, boolean shouldMarkDone) {
        try {
            int taskNumber = Integer.parseInt(command.substring(command.indexOf(' ') + 1).trim());
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("     That task number does not exist.");
                return taskCount;
            }

            Task task = tasks[taskNumber - 1];
            if (shouldMarkDone) {
                task.markAsDone();
                System.out.println("     Nice! I've marked this task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("     OK, I've marked this task as not done yet:");
            }
            System.out.println("       " + task);
        } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
            System.out.println("     Please provide a valid task number.");
        }
        return taskCount;
    }

}
