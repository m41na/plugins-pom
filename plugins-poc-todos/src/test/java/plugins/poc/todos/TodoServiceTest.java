package plugins.poc.todos;

import org.junit.Ignore;
import org.junit.Test;

import plugins.poc.todos.TodoService;

public class TodoServiceTest {

	@Test
	@Ignore
	public void testPrintTasks() {
		TodoService app = new TodoService();
		app.printTasks();
	}

}
