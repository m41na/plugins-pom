package com.practicaldime.plugins.todos;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TodoService {

    private final int count = 9;

    //TESTING ONLY
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(TodoConfig.class);
        TodoService app = context.getBean("app", TodoService.class);
        app.printTasks();
        ((AnnotationConfigApplicationContext) context).close();
    }

    public void printTasks() {
        try {
            for (int i = 0; i < count; i++) {
                Todo task = new Todo();
                System.out.println(task);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }
}
