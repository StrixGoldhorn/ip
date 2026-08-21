package megatron.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import megatron.exception.TaskNotFoundException;

/** Owns the tasks in the order in which they were added. */
public final class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing a copy of the supplied tasks. */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(Objects.requireNonNull(initialTasks));
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return tasks.size();
    }

    /** Adds a task to the end of this list. */
    public void add(Task task) {
        tasks.add(Objects.requireNonNull(task));
    }

    /** Returns the task at a one-based list number. */
    public Task getTask(int taskNumber) throws TaskNotFoundException {
        return tasks.get(toIndex(taskNumber));
    }

    /** Removes and returns the task at a one-based list number. */
    public Task removeTask(int taskNumber) throws TaskNotFoundException {
        return tasks.remove(toIndex(taskNumber));
    }

    /** Marks and returns the task at a one-based list number as done. */
    public Task setDone(int taskNumber) throws TaskNotFoundException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /** Marks and returns the task at a one-based list number as not done. */
    public Task setNotDone(int taskNumber) throws TaskNotFoundException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /** Returns an iterator over the tasks in list order. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /** Converts a one-based task number to an internal zero-based index. */
    private int toIndex(int taskNumber) throws TaskNotFoundException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TaskNotFoundException();
        }
        return taskNumber - 1;
    }
}
