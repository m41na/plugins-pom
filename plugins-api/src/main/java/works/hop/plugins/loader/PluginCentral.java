package works.hop.plugins.loader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import works.hop.plugins.api.PlugException;
import works.hop.plugins.api.PlugLifecycle;
import works.hop.plugins.api.PlugResult;
import works.hop.plugins.api.Pluggable;
import works.hop.plugins.api.Plugin;
import works.hop.plugins.config.PlugConfig;

public class PluginCentral extends ClassLoader {

	private final Map<String, Plugin<?>> plugins = new HashMap<>();
	private final Map<String, URLClassLoader> loaders = new HashMap<>();
	private final List<Pluggable> sources;

	public PluginCentral(ClassLoader parent, List<Pluggable> sources) {
		super(parent);
		this.sources = sources;
		this.init();
	}

	private void init() {
		if (sources != null) {
			for (Iterator<Pluggable> iter = this.sources.iterator(); iter.hasNext();) {
				Pluggable plug = iter.next();
				String path = PlugConfig.getInstance().resolveUrl(plug);
				String plugin = plug.getPlugin();
				// reloading
				URL url;
				URLClassLoader loader;
				try {
					url = new URL(path);
					loader = new URLClassLoader(new URL[] { url });
					loaders.put(path, loader);
					Class<?> cl = Class.forName(plugin, true, loader);
					plugins.put(path, (Plugin<?>) cl.newInstance());
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Pluggable found = findPlugByPlugin(name);
		if (found != null) {
			try {
				String jarUrl = PlugConfig.getInstance().resolveUrl(found);
				String classUrl = toFilePath(name);
				URL url = new URL("jar", "", jarUrl + "!/" + classUrl);
				JarURLConnection connection = (JarURLConnection) url.openConnection();
				try (InputStream input = connection.getInputStream()) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int data = input.read();
					while (data != -1) {
						baos.write(data);
						data = input.read();
					}
					input.close();

					byte[] classData = baos.toByteArray();
					return defineClass(name, classData, 0, classData.length);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		return super.loadClass(name);
	}

	public Class<?> loadClass(byte[] classData, String name) {
		Class<?> clazz = defineClass(name, classData, 0, classData.length);
		if (clazz != null) {
			if (clazz.getPackage() == null) {
				definePackage(name.replace("\\.\\w+$", ""), null, null, null, null, null, null, null);
			}
			resolveClass(clazz);
		}
		return clazz;
	}

	public String toFilePath(String name) {
		return name.replaceAll("\\.", "/") + ".class";
	}

	public List<String> listClasses(String jarName, String packageName) {
		System.out.printf("Looking for classes in package %s inside %s jar%n", packageName, jarName);
		List<String> classes = new ArrayList<>();
		try (JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName))) {
			JarEntry entry;
			while (true) {
				entry = jarFile.getNextJarEntry();
				if (entry == null) {
					break;
				}
				if (entry.getName().startsWith(packageName) && entry.getName().endsWith(".class")) {
					String clazz = entry.getName().replaceAll("/", "\\.");
					System.out.printf("found %s%n", clazz);
					classes.add(clazz);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return classes;
	}

	public String getMainClass(JarURLConnection uc) throws IOException {
		Attributes attr = uc.getAttributes();
		return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	}

	public void invokeMainClass(String name, String[] args) throws Exception {
		Class<?> c = loadClass(name);
		Method m = c.getMethod("main", new Class[] { args.getClass() });
		m.setAccessible(true);
		int mods = m.getModifiers();
		if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
			throw new NoSuchMethodException("main method not found");
		}
		try {
			m.invoke(null, new Object[] { args });
		} catch (ReflectiveOperationException e) {
			// this should not happen because access checks have been disabled
		}
	}

	public Pluggable findPlugByPlugin(String plugin) {
		Pluggable config = PlugConfig.getInstance().loadConfig();
		for (Pluggable source : config.getSources()) {
			if (source.getPlugin().equals(plugin)) {
				return source;
			}
		}
		return null;
	}

	public Pluggable findPlugByJarname(String jarname) {
		Pluggable config = PlugConfig.getInstance().loadConfig();
		for (Pluggable source : config.getSources()) {
			if (source.getJarfile().equals(jarname)) {
				return source;
			}
		}
		return null;
	}

	public void loadPlugin(String name) {
		try {
			// identify plugin
			Pluggable pluggable = findPlugByPlugin(name);
			String path = PlugConfig.getInstance().resolveUrl(pluggable);
			String plugin = pluggable.getPlugin();

			// String classUrl = toFilePath(plugin);
			// URL url = new URL("jar", "", path + "!/" + classUrl);
			// create new loader
			try {
				URL url = new URL(path);
				URLClassLoader loader = new URLClassLoader(new URL[] { url }, this.getClass().getClassLoader());
				loaders.put(path, loader);
				// create plugin instance
				Class<?> cl = Class.forName(plugin, true, loader);
				Plugin<?> plug = (Plugin<?>) cl.newInstance();
				PlugLifecycle plc = plug.lifecycle();
				plugins.put(path, plug);
				// load plugin
				plc.beforeLoad();
				try {
					plug.load(loader);
					plc.onLoadSuccess();
				} catch (Exception e) {
					plc.onLoadError();
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public PlugResult<?> runPlugin(String name, String feature, String payload) {
		// identify plugin
		Pluggable pluggable = findPlugByPlugin(name);
		if (pluggable != null) {
			String path = PlugConfig.getInstance().resolveUrl(pluggable);
			// get cached plugin
			Plugin<?> plug = plugins.get(path);
			if (plug != null) {
				PlugLifecycle plc = plug.lifecycle();
				// execute plugin
				plc.beforeExecute();
				PlugResult<?> result = plug.execute(feature, payload);
				plc.onExecuteSuccess();
				try {
					plc.onExecuteSuccess();
					return result;
				} catch (Exception e) {
					plc.onExecuteError();
					return new PlugResult<>(e.getMessage());
				}
			} else {
				throw new PlugException("Plugin with name '" + name + "' not loaded");
			}
		} else {
			throw new PlugException("Plugin with name '" + name + "' not defiend");
		}
	}

	public void reloadPlugin(String name) {
		try {
			// identify plugin
			Pluggable pluggable = findPlugByPlugin(name);
			String path = PlugConfig.getInstance().resolveUrl(pluggable);
			// get cached plugin
			Plugin<?> plug = plugins.remove(path);
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
			Class<?> cl = Class.forName(pluggable.getPlugin(), true, loader);
			plug = (Plugin<?>) cl.newInstance();
			PlugLifecycle plc = plug.lifecycle();
			plugins.put(path, plug);
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
		}
	}

	public void unloadPlugin(String name) {
		try {
			// identify plugin
			Pluggable pluggable = findPlugByPlugin(name);
			String path = PlugConfig.getInstance().resolveUrl(pluggable);
			// get cached plugin
			Plugin<?> plug = plugins.remove(path);
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
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static void main(String[] args) throws ReflectiveOperationException {
		ClassLoader parentCl = PluginCentral.class.getClassLoader();
		PluginCentral reloader = new PluginCentral(parentCl, null);

		String plugin = "works.hop.plugins.todos.TodoPlugin";
		String feature = "printTasks";
		reloader.loadPlugin(plugin);
		reloader.runPlugin(plugin, feature, null);
		reloader.reloadPlugin(plugin);
		reloader.runPlugin(plugin, feature, null);
		reloader.unloadPlugin(plugin);
	}
}
