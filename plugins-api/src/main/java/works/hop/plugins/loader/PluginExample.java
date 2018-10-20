package works.hop.plugins.loader;

import java.net.URL;
import java.net.URLClassLoader;

import works.hop.plugins.api.Pluggable;
import works.hop.plugins.api.Plugin;
import works.hop.plugins.api.PlugLifecycle;
import works.hop.plugins.config.PlugConfig;

public class PluginExample implements Runnable {

	private final Pluggable plug;

	public PluginExample(Pluggable plug) {
		super();
		this.plug = plug;
	}

	@Override
	public void run() {
		String path = PlugConfig.getInstance().resolveUrl(plug);
		String plugin = plug.getPlugin();
		try {
			URL url = new URL(path);
			URLClassLoader loader = new URLClassLoader(new URL[] { url });
			Class<?> cl = Class.forName(plugin, true, loader);
			//create plugin instance
			Plugin<?> plug = (Plugin<?>) cl.newInstance();
			PlugLifecycle plc = plug.lifecycle();
			//load plugin
			plc.beforeLoad();
			try {
				plug.load(loader);
				plc.onLoadSuccess();
			} catch (Exception e) {
				plc.onLoadError();
			}
			//execute plugin
			plc.beforeExecute();
			plug.execute("printTasks", null);
			plc.onExecuteSuccess();
			try {
				plc.onExecuteSuccess();
			} catch (Exception e) {
				plc.onExecuteError();
			}
			System.out.println("Hit <Enter> to proceed");
			System.in.read();
			//unload plugin
			plc.beforeUnload();
			try {
				plug.unload();
				plc.onUnloadSuccess();
			} catch (Exception e) {
				plc.onUnloadError();
			}
			//close loader
			loader.close();

			// assuming plugin jar got updated, the changes should be reflect after this
			loader = new URLClassLoader(new URL[] { url });
			cl = Class.forName(plugin, true, loader);
			plug = (Plugin<?>) cl.newInstance();
			plc = plug.lifecycle();
			plc.beforeLoad();
			try {
				plug.load(loader);
				plc.onLoadSuccess();
			} catch (Exception e) {
				plc.onLoadError();
			}
			plc.beforeExecute();
			plug.execute("printTasks", null);
			plc.onExecuteSuccess();
			try {
				plc.onExecuteSuccess();
			} catch (Exception e) {
				plc.onExecuteError();
			}
			System.out.println("Plugin will now unload");
			plc.beforeUnload();
			try {
				plug.unload();
				plc.onUnloadSuccess();
			} catch (Exception e) {
				plc.onUnloadError();
			}
			loader.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Pluggable plug = PlugConfig.getInstance().loadConfig();
		Runnable task = new PluginExample(plug.getSources().get(0));
		Thread thread = new Thread(task);
		thread.start();
		thread.join();
	}
}
