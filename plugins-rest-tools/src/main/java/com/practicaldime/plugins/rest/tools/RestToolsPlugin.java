package com.practicaldime.plugins.rest.tools;

import com.practicaldime.plugins.api.AbstractPlugin;
import com.practicaldime.plugins.api.PlugException;
import com.practicaldime.plugins.api.PlugLifecycle;
import com.practicaldime.plugins.api.PlugResult;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RestToolsPlugin extends AbstractPlugin<RestToolsService> {

    private AnnotationConfigApplicationContext context;

    public RestToolsPlugin() {
        super(RestToolsService.class);
    }

    @Override
    public PlugLifecycle lifecycle() {
        return this;
    }

    @Override
    public RestToolsService target() {
        return this.service;
    }

    @Override
    public void load(ClassLoader loader) {
        context = new AnnotationConfigApplicationContext();
        context.setClassLoader(loader);
        context.register(RestToolsConfig.class);
        context.refresh();
        System.out.println("plugin loaded");
        this.service = context.getBean("app", RestToolsService.class);
    }

    @Override
    public Object invoke(String feature, Class<?>[] params, Object[] args) throws ReflectiveOperationException {
        System.out.println("plugin executing");
        return RestToolsService.class.getMethod(feature, params).invoke(service, args);
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
