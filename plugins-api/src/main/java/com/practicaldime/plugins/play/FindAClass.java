package com.practicaldime.plugins.play;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FindAClass extends ClassLoader {

    private final Set<String> cache = new HashSet<>();

    public FindAClass(ClassLoader parent) {
        super(parent);
        cache.add("com.practicaldime.plugins.play.PlayStuff");
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (cache.contains(name)) {
            try {
                byte[] b = readClassBytes(name);
                return defineClass(name, b, 0, b.length);
            } catch (IOException e) {
                throw new ClassNotFoundException("Could not find plugin class", e);
            }
        }
        return super.findClass(name);
    }

    public byte[] readClassBytes(String name) throws IOException {
        ClassReader cr = new ClassReader(name);
        ClassWriter cw = new ClassWriter(cr, 0);
        // cv forwards all events to cw
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM6) {
        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    public void registerPlugin(String name) {
        this.cache.add(name);
    }
}
