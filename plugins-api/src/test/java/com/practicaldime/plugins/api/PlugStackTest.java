package com.practicaldime.plugins.api;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PlugStackTest {

    @Test
    public void use(){
        PlugStack stack = PlugStack.use(new Object());
        assertNotNull(stack);
    }

    @Test
    public void callAdd() {
        List<Integer> list = new LinkedList<>();
        PlugStack stack = PlugStack.use(list);
        stack = stack.push("add", new Class[]{Object.class});
        stack = stack.call(Integer.valueOf(1));
        Object result = stack.pop();
        System.out.println(result.toString());
    }

    @Test
    public void callAddThenGet() {
        List<Integer> list = new LinkedList<>();
        PlugStack stack = PlugStack.use(list);
        stack = stack.push("add", new Class[]{Object.class});
        stack = stack.call(Integer.valueOf(2));
        Object result = stack.pop();
        System.out.println(result.toString());
        stack = stack.push(list);
        stack = stack.push("size", null);
        stack.call();
        result = stack.pop();
        System.out.println(result.toString());
        stack = stack.push(list);
        stack = stack.push("get", new Class[]{int.class});
        stack = stack.call(0);
        result = stack.pop();
        System.out.println(result.toString());
    }
}