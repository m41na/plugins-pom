package com.practicaldime.plugins.play;

public class DefineAClass extends ClassLoader{

	public Class<?> defineClass(String name, byte[] b){
		return defineClass(name, b, 0, b.length);
	}
}
