package com.practicaldime.plugins.rest.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RestToolsService {

    private static final Logger LOG = LoggerFactory.getLogger(RestToolsService.class);

    public RestToolsService() {
        LOG.debug("RestToolsService constructor executed");
    }

    //TESTING ONLY
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(RestToolsConfig.class);
        RestToolsService app = context.getBean("app", RestToolsService.class);
        app.printTasks();
        ((AnnotationConfigApplicationContext) context).close();
    }

    public void printTasks() {
        try {
            for (int i = 0; i < 3; i++) {

                System.out.println("rest tools (" + i + ")");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }
}
