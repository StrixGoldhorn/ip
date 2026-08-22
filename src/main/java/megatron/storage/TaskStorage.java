package megatron.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.task.Todo;

/** Stores and restores Megatron tasks in a CSV file. */
public final class TaskStorage {
    private final Path file;

    /** Creates storage that reads and writes the given relative or absolute path. */
    public TaskStorage(String fileName) {
        file = Paths.get(fileName);
    }

    /** Loads all valid tasks. A missing file is treated as an empty task list. */
    public TaskList load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return new TaskList(tasks);
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> fields = parseCsvLine(line);
                if (fields.size() < 4 || fields.get(0).equals("type")) {
                    continue;
                }
                if (!isValid(fields)) {
                    continue;
                }
                Task task = createTask(fields);
                if (task != null) {
                    if ("1".equals(fields.get(1))) {
                        task.markAsDone();
                    }
                    tasks.add(task);
                }
            }
        } catch (IOException | RuntimeException exception) {
            // Keep the application usable when the data file is unreadable or malformed.
        }
        return new TaskList(tasks);
    }

    /** Saves all tasks and creates the data folder when needed. */
    public void save(TaskList tasks) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                writer.write("type,done,description,extra");
                writer.newLine();
                for (Task task : tasks) {
                    writer.write(csv(task.getTypeCode()) + "," + (task.isDone() ? "1" : "0") + ","
                            + csv(task.getDescription()) + "," + csv(task.getExtra()));
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            // A save failure must not terminate the chatbot.
        }
    }

    private static Task createTask(List<String> fields) {
        if (fields.get(0).equals("T")) {
            return new Todo(fields.get(2));
        } else if (fields.get(0).equals("D")) {
            try {
                return new Deadline(fields.get(2), LocalDateTime.parse(fields.get(3)));
            } catch (IllegalArgumentException exception) {
                return null;
            }
        } else if (fields.get(0).equals("E")) {
            String[] times = fields.get(3).split("\\|", 2);
            try {
                return times.length == 2 ? new Event(fields.get(2), LocalDateTime.parse(times[0]),
                        LocalDateTime.parse(times[1])) : null;
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }
        return null;
    }

    /** Checks a row before converting it into a task. Invalid rows are ignored. */
    private static boolean isValid(List<String> fields) {
        if (fields.size() != 4 || !(fields.get(1).equals("0") || fields.get(1).equals("1"))
                || fields.get(2).isBlank()) {
            return false;
        }
        if (fields.get(0).equals("T")) {
            return fields.get(3).isEmpty();
        } else if (fields.get(0).equals("D")) {
            return !fields.get(3).isBlank();
        } else if (fields.get(0).equals("E")) {
            String[] times = fields.get(3).split("\\|", 2);
            return times.length == 2 && !times[0].isBlank() && !times[1].isBlank();
        }
        return false;
    }

    private static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString());
        return fields;
    }
}
