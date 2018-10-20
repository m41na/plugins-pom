package works.hop.plugins.api;

public interface PlugLifecycle {

	void beforeLoad();
	
	void onLoadSuccess();
	
	void onLoadError();
	
	void beforeExecute();
	
	void onExecuteSuccess();
	
	void onExecuteError();
	
	void beforeUnload();
	
	void onUnloadSuccess();
	
	void onUnloadError();
}
