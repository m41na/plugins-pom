package com.practicaldime.plugins.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.practicaldime.plugins.util.InspectFeatures;

public abstract class AbstractPlugin<T> implements Plugin<T>, PlugLifecycle {

	protected final Class<T> type;
	protected T service;
	protected Map<String, Feature> features;

	public AbstractPlugin(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public PlugLifecycle lifecycle() {
		return this;
	}

	@Override
	public T target() {
		return this.service;
	}

	@Override
	public Object getBean(String name) {
		throw new PlugException("There is no beans context configuration available.");
	}

	@Override
	public void features(ClassLoader loader) {
		try {
			InspectFeatures inspect = new InspectFeatures();
			String clazzName = type.getName();
			String resName = clazzName.replaceAll("\\.", "/") + ".class";
			try(InputStream stream = loader.getResourceAsStream(resName)){
				inspect.discover(stream);
			}
			List<Feature> features = inspect.getFeatures();
			this.features = features.stream().collect(Collectors.toMap(Feature::getName, feature -> feature));
		} catch (IOException e) {
			throw new PlugException("Could not find class to discover features.");
		}
	}

	@Override
	public void load(ClassLoader loader) {
		throw new PlugException("No implementation is provided yet.");
	}

	@Override
	public void unload() {
		throw new PlugException("No implementation is provided yet.");
	}

	@Override
	public Object invoke(String feature, Class<?>[] params, Object[] args) throws ReflectiveOperationException {
		System.out.println("plugin executing");
		return type.getMethod(feature, params).invoke(service, args);
	}

	@Override
	public void beforeLoad() {
		System.out.printf("executing %s%n", "beforeLoad");
	}

	@Override
	public void onLoadSuccess() {
		System.out.printf("executing %s%n", "onLoadSuccess");
	}

	@Override
	public void onLoadError() {
		System.out.printf("executing %s%n", "onLoadError");
	}

	@Override
	public void beforeExecute() {
		System.out.printf("executing %s%n", "beforeExecute");
	}

	@Override
	public void onExecuteSuccess() {
		System.out.printf("executing %s%n", "onExecuteSuccess");
	}

	@Override
	public void onExecuteError() {
		System.out.printf("executing %s%n", "onExecuteError");
	}

	@Override
	public void beforeUnload() {
		System.out.printf("executing %s%n", "beforeUnload");
	}

	@Override
	public void onUnloadSuccess() {
		System.out.printf("executing %s%n", "onUnloadSuccess");
	}

	@Override
	public void onUnloadError() {
		System.out.printf("executing %s%n", "onUnloadError");
	}
}
