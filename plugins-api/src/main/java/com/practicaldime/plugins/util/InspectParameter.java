package com.practicaldime.plugins.util;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import com.practicaldime.plugins.api.Feature.ParamType;

public class InspectParameter extends ClassVisitor {

	private final ParamType parameter;

	public InspectParameter(ParamType parameter) {
		super(Opcodes.ASM6);
		this.parameter = parameter;
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		System.out.println(name + " extends " + superName + " {");
	}

	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		String field = desc + " " + name;
		System.out.println("found field '" + field + "'");
		if (field.startsWith("[")) {
			// handle array type
			String arrayType = field.substring(1);
			if (!arrayType.matches("[ZCBSIFJD]{1}") && !arrayType.startsWith("Ljava") && arrayType.startsWith("L")) {
				try {
					String fieldType = field.substring(1, field.length() - 1).replaceAll("/", ".");
					ParamType param = new ParamType(fieldType);
					InspectParameter inspect = new InspectParameter(param);
					ClassReader cr = new ClassReader(fieldType);
					cr.accept(inspect, 0);
					//add type
					parameter.attributes.add(inspect.getParameter());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else if (!field.startsWith("Ljava") && field.startsWith("L")) {
			try {
				String returnType = field.substring(1, field.length() - 1).replaceAll("/", ".");
				ParamType param = new ParamType(returnType);
				InspectParameter inspect = new InspectParameter(param);
				ClassReader cr = new ClassReader(returnType);
				cr.accept(this, 0);
				//add type
				parameter.attributes.add(inspect.getParameter());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			parameter.attributes.add(new ParamType(field));
		}
		return null;
	}

	public void visitEnd() {
		System.out.println("}");
	}

	public void discover(String plugin) throws IOException {
		ClassReader cr = new ClassReader(plugin);
		cr.accept(this, 0);
	}

	public ParamType getParameter() {
		return parameter;
	}

	public static void main(String args[]) throws IOException {
		InspectParameter ret = new InspectParameter(new ParamType("testing"));
		ret.discover("com.practicaldime.plugins.loader.PluginCentral");
	}
}
