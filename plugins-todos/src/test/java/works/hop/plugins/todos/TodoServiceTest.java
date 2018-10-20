package works.hop.plugins.todos;

import org.junit.Ignore;
import org.junit.Test;

import works.hop.plugins.todos.TodoService;

public class TodoServiceTest {

	@Test
	@Ignore
	public void testPrintTasks() {
		TodoService app = new TodoService();
		app.printTasks();
	}

}
