package com.practicaldime.plugins.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PlugProxy implements InvocationHandler{

	private final Object target;
	
	public PlugProxy(Object target) {
		super();
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return method.invoke(target, args);
	}

	public static Object instance(Class<?> loaded, Object target) {
		return Proxy.newProxyInstance(PlugProxy.class.getClassLoader(), new Class<?>[] {loaded}, new PlugProxy(target));
	}
}
