package com.practicaldime.plugins.util;

import java.util.HashMap;
import java.util.Map;

public class TypeValues {

    public static final Map<Class<?>, Character> PRIMITIVES = new HashMap<>();
    public static final Map<Class<?>, String> PRIMITIVE_ARRAYS = new HashMap<>();

    static {
        PRIMITIVES.put(boolean.class, 'Z');
        PRIMITIVES.put(char.class, 'C');
        PRIMITIVES.put(byte.class, 'B');
        PRIMITIVES.put(short.class, 'S');
        PRIMITIVES.put(int.class, 'I');
        PRIMITIVES.put(float.class, 'F');
        PRIMITIVES.put(long.class, 'J');
        PRIMITIVES.put(double.class, 'D');
    }

    static {
        PRIMITIVE_ARRAYS.put(boolean.class, "[Z");
        PRIMITIVE_ARRAYS.put(char.class, "[C");
        PRIMITIVE_ARRAYS.put(byte.class, "[B");
        PRIMITIVE_ARRAYS.put(short.class, "[S");
        PRIMITIVE_ARRAYS.put(int.class, "[I");
        PRIMITIVE_ARRAYS.put(float.class, "[F");
        PRIMITIVE_ARRAYS.put(long.class, "[J");
        PRIMITIVE_ARRAYS.put(double.class, "[D");
    }

    public static Map<String, ?> inspect(Class<?> clazz, Map<String, ?> members) {
        return null;
    }
}
