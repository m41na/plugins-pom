package com.practicaldime.plugins.todos;

import com.practicaldime.plugins.api.AbstractPlugin;
import com.practicaldime.plugins.api.PlugException;
import com.practicaldime.plugins.api.PlugLifecycle;
import com.practicaldime.plugins.api.PlugResult;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TodoPlugin extends AbstractPlugin<TodoService> {

    private AnnotationConfigApplicationContext context;

    public TodoPlugin() {
        super(TodoService.class);
    }

    @Override
    public PlugLifecycle lifecycle() {
        return this;
    }

    @Override
    public void features(ClassLoader loader) {
        System.out.printf("use this to discover %s plugin features%n", TodoPlugin.class.getName());
    }

    @Override
    public void load(ClassLoader loader) {
        context = new AnnotationConfigApplicationContext();
        context.setClassLoader(loader);
        context.register(TodoConfig.class);
        context.refresh();
        System.out.println("plugin loaded");
        this.service = context.getBean("app", TodoService.class);
    }

    @Override
    public Object invoke(String feature, Class<?>[] params, Object[] args) throws ReflectiveOperationException {
        System.out.println("plugin executing");
        return TodoService.class.getMethod(feature, params).invoke(service, args);
    }

    @Override
    public Object getBean(String name) {
        return context.getBean(name);
    }

    @Override
    public PlugResult<?> execute(String feature, String payload) {
        if (service != null) {
            System.out.println("plugin executing");
            switch (feature) {
                case "printTasks":
                    try {
                        service.printTasks();
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
}
