package works.hop.plugins.api;

import java.lang.reflect.Method;
import java.util.Stack;

public class Poppin {

	private Stack<Object> stack = new Stack<>();

	public Poppin(Object target) {
		this.stack.push(target);
	}
	
	public static Poppin use(Object target) {
		return new Poppin(target);
	}

	public Poppin func(String method, Class<?>... params) {
		try {
			Method m = stack.peek().getClass().getMethod(method, params);
			stack.push(m);
			return this;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Could not find method", e);
		}
	}

	public Poppin call(Object... args) {
		try {
			Method method = (Method) stack.pop();
			Object result = method.invoke(stack.pop(), args);
			stack.push(result);
			return this;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Could not find method", e);
		}
	}
	
	public Object pop() {
		return stack.pop();
	}

	public <T>T pop(Class<T> type) {
		return type.cast(pop());
	}
}
