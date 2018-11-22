package works.hop.plugins.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import works.hop.plugins.api.PlugException;
import works.hop.plugins.api.PlugLifecycle;
import works.hop.plugins.api.PlugProxy;
import works.hop.plugins.api.PlugResult;
import works.hop.plugins.api.Pluggable;
import works.hop.plugins.api.Plugin;
import works.hop.plugins.config.PlugConfig;

public class PluginCentral {

	private final Map<String, Plugin<?>> plugins = new HashMap<>();
	private final Map<String, URLClassLoader> loaders = new HashMap<>();
	private final List<Pluggable> sources;

	public PluginCentral(List<Pluggable> sources) {
		super();
		this.sources = sources;
		this.init();
	}

	private void init() {
		if (sources != null) {
			for (Iterator<Pluggable> iter = this.sources.iterator(); iter.hasNext();) {
				Pluggable plug = iter.next();
				String path = PlugConfig.resolveUrl(plug);
				String name = plug.getPlugin();
				// load plugin
				try {
					URL url = new URL(path);
					URLClassLoader loader = loaders.get(path);
					if (loader == null) {
						loader = new URLClassLoader(new URL[] { url });
						loaders.put(path, loader);
					}

					Class<?> cl = Class.forName(name, true, loader);
					String pluginKey = path + "@" + name;
					if (plugins.get(pluginKey) == null) {
						Plugin<?> plugin = (Plugin<?>) cl.newInstance();
						plugins.put(pluginKey, plugin);
					}
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

	public void loadPlugin(String plugin) {
		// identify plugin
		Pluggable pluggable = findPlugByPlugin(plugin);
		if (pluggable != null) {
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.get(pluginKey);
			if (plug != null) {
				PlugLifecycle plc = plug.lifecycle();
				plc.beforeLoad();
				try {
					// load plugin
					URLClassLoader loader = loaders.get(path);
					plug.load(loader);
					plc.onLoadSuccess();
				} catch (Exception e) {
					plc.onLoadError();
				}
			} else {
				throw new PlugException("Plugin with name '" + plugin + "' not defiend");
			}
		} else {
			throw new PlugException("Plugin with name '" + plugin + "' not defiend");
		}
	}

	public PlugResult<?> runPlugin(String plugin, String feature, String payload) {
		// identify plugin
		Pluggable pluggable = findPlugByPlugin(plugin);
		if (pluggable != null) {
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.get(pluginKey);
			if (plug != null) {
				PlugLifecycle plc = plug.lifecycle();
				plc.beforeExecute();
				try {
					// execute plugin
					PlugResult<?> result = plug.execute(feature, payload);
					plc.onExecuteSuccess();
					return result;
				} catch (Exception e) {
					plc.onExecuteError();
					return new PlugResult<>(e.getMessage());
				}
			} else {
				throw new PlugException("Plugin with name '" + plugin + "' not loaded");
			}
		} else {
			throw new PlugException("Plugin with name '" + plugin + "' not defiend");
		}
	}

	public Object invokePlugin(String plugin, String feature, Class<?>[] params, Object[] args) {
		// identify plugin
		Pluggable pluggable = findPlugByPlugin(plugin);
		if (pluggable != null) {
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.get(pluginKey);
			if (plug != null) {
				PlugLifecycle plc = plug.lifecycle();
				plc.beforeExecute();
				try {
					// invoke plugin
					Object result = plug.invoke(feature, params, args);
					plc.onExecuteSuccess();
					return result;
				} catch (Exception e) {
					plc.onExecuteError();
					return new PlugResult<>(e.getMessage());
				}
			} else {
				throw new PlugException("Plugin with name '" + plugin + "' not loaded");
			}
		} else {
			throw new PlugException("Plugin with name '" + plugin + "' not defiend");
		}
	}

	public void reloadPlugin(String plugin) {
		// identify plugin
		Pluggable pluggable = findPlugByPlugin(plugin);
		if (pluggable != null) {
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.remove(pluginKey);
			try {
				if (plug != null) {
					PlugLifecycle plc = plug.lifecycle();
					// unload plugin
					plc.beforeUnload();
					try {
						plug.unload();
						plc.onUnloadSuccess();
					} catch (Exception e) {
						plc.onUnloadError();
					}
					// close current loader
					URLClassLoader loader = loaders.remove(path);
					loader.close();
				}

				// assuming plugin jar got updated, the changes should be reflect after this
				URL url = new URL(path);
				URLClassLoader loader = new URLClassLoader(new URL[] { url });
				loaders.put(path, loader);
				// replace cached plugin instance
				Class<?> cl = Class.forName(name, true, loader);
				plug = (Plugin<?>) cl.newInstance();
				PlugLifecycle plc = plug.lifecycle();
				plugins.put(pluginKey, plug);
				// reload plugin
				plc.beforeLoad();
				try {
					plug.load(loader);
					plc.onLoadSuccess();
				} catch (Exception e) {
					plc.onLoadError();
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
				throw new PlugException(e);
			}
		} else {
			throw new PlugException("Plugin with name '" + plugin + "' not defiend");
		}
	}

	public void unloadPlugin(String plugin) {
		// identify plugin
		Pluggable pluggable = findPlugByPlugin(plugin);
		if (pluggable != null) {
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.remove(pluginKey);
			PlugLifecycle plc = plug.lifecycle();
			// unload plugin
			plc.beforeUnload();
			try {
				plug.unload();
				plc.onUnloadSuccess();
			} catch (Exception e) {
				plc.onUnloadError();
			}

			try {
				// retrieve and close loader
				URLClassLoader loader = loaders.remove(path);
				loader.close();
			} catch (Exception e) {
				e.printStackTrace(System.err);
				throw new PlugException(e);
			}
		} else {
			throw new PlugException("Plugin with name '" + plugin + "' not defiend");
		}
	}

	public byte[] loadPluginBytes(String plugin) {
		try {
			// identify plugin
			Pluggable pluggable = findPlugByPlugin(plugin);
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.get(pluginKey);
			if (plug != null) {
				// read bytes
				ClassReader cr = new ClassReader(pluggable.getPlugin());
				ClassWriter cw = new ClassWriter(cr, 0);
				// cv forwards all events to cw
				ClassVisitor cv = new ClassVisitor(Opcodes.ASM6) {
				};
				cr.accept(cv, 0);
				return cw.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new PlugException(e);
		}
		return null;
	}

	public Object getInstance(String plugin, String bean) {
		try {
			// identify plugin
			Pluggable pluggable = findPlugByPlugin(plugin);
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.get(pluginKey);

			// retrieve named bean
			return plug.getBean(bean);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new PlugException(e);
		}
	}

	public Object loadPluginProxy(String plugin) {
		try {
			// identify plugin
			Pluggable pluggable = findPlugByPlugin(plugin);
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			URLClassLoader loader = loaders.get(path);
			// get cached plugin
			Plugin<?> plug = plugins.get(pluginKey);

			// create proxy
			Class<?> pluginClass = Class.forName(plugin, true, loader);
			Object proxy = PlugProxy.instance(pluginClass, plug.target());
			return proxy;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace(System.err);
			throw new PlugException(e);
		}
	}

	public void discoverFeatures(String plugin) {
		try {
			// identify plugin
			Pluggable pluggable = findPlugByPlugin(plugin);
			String path = PlugConfig.resolveUrl(pluggable);
			String name = pluggable.getPlugin();
			// get cached plugin instance
			String pluginKey = path + "@" + name;
			Plugin<?> plug = plugins.get(pluginKey);
			if (plug != null) {
				// discover plugin features
				URLClassLoader loader = loaders.get(path);
				plug.features(loader);
				return;
			}
			throw new PlugException("Could not find class to discover features.");
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new PlugException(e);
		}
	}

	public static Pluggable findPlugByPlugin(String plugin) {
		Pluggable config = PlugConfig.getInstance().loadConfig();
		for (Pluggable source : config.getSources()) {
			if (source.getPlugin().equals(plugin)) {
				return source;
			}
		}
		return null;
	}

	public static Pluggable findPlugByJarname(String jarname) {
		Pluggable config = PlugConfig.getInstance().loadConfig();
		for (Pluggable source : config.getSources()) {
			if (source.getJarfile().equals(jarname)) {
				return source;
			}
		}
		return null;
	}

	public static void main(String[] args) throws ReflectiveOperationException {
		Pluggable config = PlugConfig.getInstance().loadConfig();
		PluginCentral reloader = new PluginCentral(config.getSources());

		String plugin = "com.jarredweb.plugins.users.StartupPlugin";
		String feature = "initialize";
		reloader.loadPlugin(plugin);
		reloader.discoverFeatures(plugin);
		reloader.runPlugin(plugin, feature, null);
		reloader.reloadPlugin(plugin);
		reloader.discoverFeatures(plugin);
		reloader.runPlugin(plugin, feature, null);
		reloader.unloadPlugin(plugin);
	}
}
