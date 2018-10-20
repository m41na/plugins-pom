package works.hop.plugins.api;

public interface Plugin<T>{
	
	PlugLifecycle lifecycle();
	
	T features();
	
	void load(ClassLoader loader);
	
	PlugResult<?> execute(String feature, String payload);
	
	void unload();
}
