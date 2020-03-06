package com.practicaldime.plugins.util;

import com.google.gson.Gson;
import com.practicaldime.plugins.api.Feature;
import com.practicaldime.plugins.api.Feature.ParamType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InspectFeatures extends ClassVisitor {

    public static final Pattern methods = Pattern.compile("^(.+\\b)(\\(L?.+;?\\){1})(.+)$");
    public static final Pattern arguments = Pattern.compile("(\\[*[ZCBSIFJD]{1})|(\\[*L?.+?;)");

    private final List<Feature> features = new ArrayList<>();

    public InspectFeatures() {
        super(Opcodes.ASM6);
    }

    public static void main(String args[]) throws IOException {
        InspectFeatures features = new InspectFeatures();
        String clazzName = "com.practicaldime.plugins.loader.PluginCentral";
        String resName = clazzName.replaceAll("\\.", "/") + ".class";
        InputStream is = features.getClass().getClassLoader().getResourceAsStream(resName);
        features.discover(is);
        Gson gson = new Gson();
        System.out.println(gson.toJson(features.getFeatures()));
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(name + " extends " + superName + " {");
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        String method = name + desc;
        System.out.println("inspecting method: " + method);
        //proceed if method is <public> and not <constructor>
        if (access == 1 && !method.matches("<init>.*")) {
            Feature feature = new Feature();
            Matcher m = methods.matcher(method);
            while (m.find()) {
                for (int i = 1; i <= m.groupCount(); i++) {
                    String group = m.group(i);
                    if (i == 1) {
                        System.out.println("found feature name '" + group + "' at group " + i);
                        feature.setName(group);
                    } else if (i == 2) {
                        String args = group.substring(1, group.length() - 1);
                        System.out.println("found args list '" + args + "' at group " + i);
                        Matcher am = arguments.matcher(args);
                        while (am.find()) {
                            for (int j = 1; j <= am.groupCount(); j++) {
                                String arg = am.group(j);
                                if (arg != null) {
                                    System.out.println("identified arg '" + arg + "' at group (" + i + "," + j + ")");
                                    ParamType param = new ParamType(arg);
                                    if (!arg.matches("[ZCBSIFJD]{1}") && !arg.startsWith("Ljava") && arg.startsWith("L")) {
                                        try {
                                            String fieldType = group.substring(1, group.length() - 1).replaceAll("/", ".");
                                            InspectParameter inspect = new InspectParameter(param);
                                            ClassReader cr = new ClassReader(fieldType);
                                            cr.accept(inspect, 0);
                                            //add parameter to feature
                                            feature.addParam(inspect.getParameter());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        //add parameter to feature
                                        feature.addParam(param);
                                    }
                                }
                            }
                        }
                    } else if (i == 3) {
                        System.out.println("found return type'" + group + "' at group " + i);
                        if (group.startsWith("[")) {
                            // handle array type
                            String arrayType = group.substring(1);
                            if (!arrayType.matches("[ZCBSIFJD]{1}") && !arrayType.startsWith("Ljava") && arrayType.startsWith("L")) {
                                try {
                                    String fieldType = group.substring(1, group.length() - 1).replaceAll("/", ".");
                                    ParamType param = new ParamType(fieldType);
                                    InspectParameter inspect = new InspectParameter(param);
                                    ClassReader cr = new ClassReader(fieldType);
                                    cr.accept(inspect, 0);
                                    //set return type
                                    feature.setReturns(inspect.getParameter());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                feature.setReturns(new ParamType(group));
                            }
                        } else if (!group.startsWith("Ljava") && group.startsWith("L")) {
                            try {
                                String returnType = group.substring(1, group.length() - 1).replaceAll("/", ".");
                                ParamType param = new ParamType(returnType);
                                InspectParameter inspect = new InspectParameter(param);
                                ClassReader cr = new ClassReader(returnType);
                                cr.accept(inspect, 0);
                                //set return type
                                feature.setReturns(inspect.getParameter());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            feature.setReturns(new ParamType(group));
                        }
                    }
                }
            }
            features.add(feature);
        }
        return null;
    }

    public void visitEnd() {
        System.out.println("}");
    }

    public void discover(InputStream stream) throws IOException {
        ClassReader cr = new ClassReader(stream);
        cr.accept(this, 0);
    }

    public List<Feature> getFeatures() {
        return features;
    }

    protected void regexTest() {
        String target = "Ljava/lang/String;Ljava/lang/String;";
        Matcher match = arguments.matcher(target);
        for (int i = 0; i <= match.groupCount(); i++) {
            String arg = match.group(i);
            System.out.println("found '" + arg + "' at group " + i + "(" + match.start(i) + "," + match.end(i) + ")");
        }
    }
}
