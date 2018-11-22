package plugins.poc.web.service;

import java.util.List;

public interface PluginService{
	
	String PLUGIN_ACTION_HEADER = "xh-plugin-action";
	
	String PLUGIN_NAME_HEADER = "xh-plugin-name";
	
	String PLUGIN_FEATURE_HEADER = "xh-plugin-feature";
	
	String IS_AVAILABLE = "isAvailable";
	
	String PLUGS_AVAILABLE = "plugsAvailable";
	
	String LOAD_PLUGIN = "loadPlugin";
	
	String RELOAD_PLUGIN = "reloadPlugin";
	
	String UNLOAD_PLUGIN = "unloadPlugin";
	
	String EXECUTE_PLUGIN = "executePlugin";
	
	boolean isAvailable(String name);

	List<String> plugsAvailable();

	boolean loadPlugin(String name);

	boolean reloadPlugin(String name);

	boolean unloadPlugin(String name);
	
	String executePlugin(String name, String feature, String payload);

	Object invokePlugin(String name, String feature, Object[] args);
	
	byte[] loadPluginBytes(String name);
	
	Object createPluginProxy(String name);
}