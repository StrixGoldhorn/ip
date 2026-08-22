package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import megatron.exception.TaskNotFoundException;

/** Tests the one-based task lookup provided by {@link TaskList}. */
class TaskListTest {
    private final Task firstTask = new Todo("first task");
    private final Task middleTask = new Todo("middle task");
    private final Task lastTask = new Todo("last task");
    private final TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

    @Test
    void getTask_validFirstTaskNumber_returnsFirstTask() throws TaskNotFoundException {
        assertSame(firstTask, taskList.getTask(1));
    }

    @Test
    void getTask_validMiddleTaskNumber_returnsMiddleTask() throws TaskNotFoundException {
        assertSame(middleTask, taskList.getTask(2));
    }

    @Test
    void getTask_validLastTaskNumber_returnsLastTask() throws TaskNotFoundException {
        assertSame(lastTask, taskList.getTask(3));
    }

    @Test
    void getTask_zeroTaskNumber_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskList.getTask(0));
    }

    @Test
    void getTask_negativeTaskNumber_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskList.getTask(-1));
    }

    @Test
    void getTask_taskNumberAboveListSize_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskList.getTask(4));
    }

    @Test
    void getTask_emptyList_throwsTaskNotFoundException() {
        TaskList emptyTaskList = new TaskList();

        assertThrows(TaskNotFoundException.class, () -> emptyTaskList.getTask(1));
    }

    @Test
    void find_matchingKeyword_returnsMatchesInListOrder() throws TaskNotFoundException {
        TaskList matches = taskList.find("task");

        assertEquals(3, matches.size());
        assertSame(firstTask, matches.getTask(1));
        assertSame(middleTask, matches.getTask(2));
        assertSame(lastTask, matches.getTask(3));
    }

    @Test
    void find_partialKeyword_returnsOnlyMatchingTasks() throws TaskNotFoundException {
        TaskList matches = taskList.find("st");

        assertEquals(2, matches.size());
        assertSame(firstTask, matches.getTask(1));
        assertSame(lastTask, matches.getTask(2));
    }

    @Test
    void find_unknownKeyword_returnsEmptyList() {
        assertEquals(0, taskList.find("book").size());
    }

    @Test
    void find_nullKeyword_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> taskList.find(null));
    }
}
