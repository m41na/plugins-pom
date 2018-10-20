package works.hop.plugins.api;

import works.hop.plugins.api.PlugException;
import works.hop.plugins.api.PlugResult;
import works.hop.plugins.api.Plugin;
import works.hop.plugins.api.PlugLifecycle;

public class AbstractPlugin<T> implements Plugin<T>, PlugLifecycle {

	protected T service;

	@Override
	public PlugLifecycle lifecycle() {
		return this;
	}

	@Override
	public T features() {
		return this.service;
	}

	@Override
	public void load(ClassLoader loader) {
		throw new PlugException("No implementation is provided yet.");
	}

	@Override
	public PlugResult<?> execute(String feature, String payload) {
		throw new PlugException("No implementation is provided yet.");
	}

	@Override
	public void unload() {
		throw new PlugException("No implementation is provided yet,");
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
