package megatron.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import megatron.exception.TaskNotFoundException;

/**
 * Owns the tasks in the order in which they were added.
 */
public final class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param initialTasks The tasks to copy.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(Objects.requireNonNull(initialTasks));
        assert this.tasks.stream().noneMatch(Objects::isNull)
                : "A task list must not contain null tasks.";
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        Task nonNullTask = Objects.requireNonNull(task);
        tasks.add(nonNullTask);
        assert tasks.get(tasks.size() - 1) == nonNullTask
                : "A newly added task must be stored at the end of the list.";
    }

    /**
     * Returns the task at a one-based list number.
     *
     * @param taskNumber The one-based task number.
     * @return The selected task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task getTask(int taskNumber) throws TaskNotFoundException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes and returns the task at a one-based list number.
     *
     * @param taskNumber The one-based task number.
     * @return The removed task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task removeTask(int taskNumber) throws TaskNotFoundException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Marks and returns the task at a one-based list number as done.
     *
     * @param taskNumber The one-based task number.
     * @return The marked task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task setDone(int taskNumber) throws TaskNotFoundException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks and returns the task at a one-based list number as not done.
     *
     * @param taskNumber The one-based task number.
     * @return The unmarked task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task setNotDone(int taskNumber) throws TaskNotFoundException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the keyword, in list order.
     */
    public TaskList find(String keyword) {
        Objects.requireNonNull(keyword);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Returns an iterator over the tasks in list order.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /**
     * Converts a one-based task number to an internal zero-based index.
     *
     * @param taskNumber The one-based task number.
     * @return The zero-based index.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    private int toIndex(int taskNumber) throws TaskNotFoundException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TaskNotFoundException();
        }
        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size()
                : "A valid one-based task number must convert to a valid zero-based index.";
        return index;
    }
}
