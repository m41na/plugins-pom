package com.practicaldime.plugins.api;

import java.lang.reflect.Method;
import java.util.Stack;

public class PlugStack {

    private Stack<Object> stack = new Stack<>();

    private PlugStack(Object target) {
        this.stack.push(target);
    }

    public static PlugStack use(Object target) {
        return new PlugStack(target);
    }

    public PlugStack push(Object obj){
        this.stack.push(obj);
        return this;
    }

    public PlugStack push(String method, Class<?>... params) {
        try {
            Method m = params != null?
                    stack.peek().getClass().getMethod(method, params) :
                    stack.peek().getClass().getMethod(method);
            stack.push(m);
            return this;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not find method", e);
        }
    }

    public PlugStack call(Object... args) {
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

    public <T> T pop(Class<T> type) {
        return type.cast(pop());
    }
}
