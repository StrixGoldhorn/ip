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
        String[] tasks = new String[MAX_TASKS];
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
            } else if (!command.isBlank() && taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
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
    private static void printTasks(String[] tasks, int taskCount) {
        for (int i = 0; i < taskCount; i++) {
            System.out.println("     " + (i + 1) + ". " + tasks[i]);
        }
    }

}
