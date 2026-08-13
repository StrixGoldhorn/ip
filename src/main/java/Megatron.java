import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Starts the Megatron chatbot application.
 */
public class Megatron {
    private static final String[] SHORT_MESSAGE_VERBS = {"shouts", "yells", "barks", "proclaims"};

    public static void main(String[] args) {
        String banner = "   __  ___              __              \n"
                + "  /  |/  /__ ___ ____ _/ /________  ___ \n"
                + " / /|_/ / -_) _ `/ _ `/ __/ __/ _ \\/ _ \\\n"
                + "/_/  /_/\\__/\\_, /\\_,_/\\__/_/  \\___/_//_/\n"
                + "           /___/                        ";
        String divider = "____________________________________________________________";

        System.out.println(divider);
        System.out.println(banner);
        say("Rawr! I'm Megatron.");
        say("What can I do for you?");
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                say("Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            }

            say(command);
            System.out.println(divider);
        }
        scanner.close();
    }

    /**
     * Prints a message spoken by Megatron.
     *
     * @param message the message to print
     */
    private static void say(String message) {
        String verb = message.length() > 100
                ? "rambles"
                : SHORT_MESSAGE_VERBS[ThreadLocalRandom.current().nextInt(SHORT_MESSAGE_VERBS.length)];
        System.out.println("     Megatron " + verb + ": " + message);
    }
}
