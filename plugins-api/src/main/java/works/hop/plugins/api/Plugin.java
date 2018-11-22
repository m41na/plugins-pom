package works.hop.plugins.api;

public interface Plugin<T>{
	
	PlugLifecycle lifecycle();
	
	void features(ClassLoader loader);
	
	T target();
	
	Object getBean(String name);
	
	void load(ClassLoader loader);
	
	Object invoke(String feature, Class<?>[] params, Object[] args) throws ReflectiveOperationException;
	
	PlugResult<?> execute(String feature, String payload);
	
	void unload();
}
