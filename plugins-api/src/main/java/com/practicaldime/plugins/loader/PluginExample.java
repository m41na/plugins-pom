package com.practicaldime.plugins.loader;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import com.practicaldime.plugins.api.PlugLifecycle;
import com.practicaldime.plugins.api.Pluggable;
import com.practicaldime.plugins.api.Plugin;
import com.practicaldime.plugins.config.PlugConfig;

public class PluginExample implements Runnable {

	private final Pluggable plug;

	public PluginExample(Pluggable plug) {
		super();
		this.plug = plug;
	}

	@Override
	public void run() {
		try (Scanner scan = new Scanner(System.in)) {
			String path = PlugConfig.resolveUrl(plug);
			String plugin = plug.getPlugin();
			try {
				URL url = new URL(path);
				Reference<URLClassLoader> loader = new WeakReference<>(new URLClassLoader(new URL[] { url }));
				Class<?> cl = Class.forName(plugin, true, loader.get());
				// create plugin instance
				Plugin<?> plug = (Plugin<?>) cl.newInstance();
				PlugLifecycle plc = plug.lifecycle();
				// load plugin
				plc.beforeLoad();
				try {
					plug.load(loader.get());
					plc.onLoadSuccess();
				} catch (Exception e) {
					plc.onLoadError();
				}
				// execute plugin
				plc.beforeExecute();
				plug.execute("printTasks", null);
				plc.onExecuteSuccess();
				try {
					plc.onExecuteSuccess();
				} catch (Exception e) {
					plc.onExecuteError();
				}
				// unload plugin
				plc.beforeUnload();
				try {
					plug.unload();
					plc.onUnloadSuccess();
				} catch (Exception e) {
					plc.onUnloadError();
				}
				// close loader
				loader.get().close();

				System.out.println("Now app will reload continuously");
				while (true) {
					Thread.sleep(3000);

					// assuming plugin jar got updated, the changes should be reflect after this
					loader = new WeakReference<>(new URLClassLoader(new URL[] { url }));
					cl = Class.forName(plugin, true, loader.get());
					plug = (Plugin<?>) cl.newInstance();
					plc = plug.lifecycle();
					// load plugin
					plc.beforeLoad();
					try {
						plug.load(loader.get());
						plc.onLoadSuccess();
					} catch (Exception e) {
						plc.onLoadError();
					}
					// execute plugin
					plc.beforeExecute();
					plug.execute("printTasks", null);
					plc.onExecuteSuccess();
					try {
						plc.onExecuteSuccess();
					} catch (Exception e) {
						plc.onExecuteError();
					}
					System.out.println("Plugin will now unload");
					// unload plugin
					plc.beforeUnload();
					try {
						plug.unload();
						plc.onUnloadSuccess();
					} catch (Exception e) {
						plc.onUnloadError();
					}
					// close loader
					loader.get().close();
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
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
