package plugins.poc.todos;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.practicaldime.plugins.api.PlugException;
import com.practicaldime.plugins.api.PlugLifecycle;
import com.practicaldime.plugins.api.PlugResult;
import com.practicaldime.plugins.api.Plugin;

public class TodoPlugin implements Plugin<TodoService>, PlugLifecycle {

	private AnnotationConfigApplicationContext context;
	private TodoService app;

	@Override
	public PlugLifecycle lifecycle() {
		return this;
	}

	@Override
	public void features(ClassLoader loader) {
		System.out.printf("use this to discover %s plugin features%n", TodoPlugin.class.getName());
	}

	@Override
	public TodoService target() {
		return this.app;
	}

	@Override
	public void load(ClassLoader loader) {
		context = new AnnotationConfigApplicationContext();
		context.setClassLoader(loader);
		context.register(TodoConfig.class);
		context.refresh();
		System.out.println("plugin loaded");
		this.app = context.getBean("app", TodoService.class);
	}

	@Override
	public Object invoke(String feature, Class<?>[] params, Object[] args) throws ReflectiveOperationException {
		System.out.println("plugin executing");
		return TodoService.class.getMethod(feature, params).invoke(app, args);
	}

	@Override
	public Object getBean(String name) {
		return context.getBean(name);
	}

	@Override
	public PlugResult<?> execute(String feature, String payload) {
		if (app != null) {
			System.out.println("plugin executing");
			switch (feature) {
			case "printTasks":
				try {
					app.printTasks();
					return new PlugResult<>(0, true, "success");
				} catch (PlugException e) {
					return new PlugResult<>(e.getMessage());
				}
			default:
				return new PlugResult<>("Feature not found on plugin");
			}
		} else {
			return new PlugResult<>("Plugin is not yet loaded");
		}
	}

	@Override
	public void unload() {
		context.close();
		System.out.println("plugin unloaded");
		context = null;
	}

	@Override
	public void beforeLoad() {
		System.out.printf("executing %s%n", "beforeLoad");
	}

	@Override
	public void onLoadSuccess() {
		System.out.printf("executing %s%n", "onLoadSuccess");
	}

	@Override
	public void onLoadError() {
		System.out.printf("executing %s%n", "onLoadError");
	}

	@Override
	public void beforeExecute() {
		System.out.printf("executing %s%n", "beforeExecute");
	}

	@Override
	public void onExecuteSuccess() {
		System.out.printf("executing %s%n", "onExecuteSuccess");
	}

	@Override
	public void onExecuteError() {
		System.out.printf("executing %s%n", "onExecuteError");
	}

	@Override
	public void beforeUnload() {
		System.out.printf("executing %s%n", "beforeUnload");
	}

	@Override
	public void onUnloadSuccess() {
		System.out.printf("executing %s%n", "onUnloadSuccess");
	}

	@Override
	public void onUnloadError() {
		System.out.printf("executing %s%n", "onUnloadError");
	}
}
