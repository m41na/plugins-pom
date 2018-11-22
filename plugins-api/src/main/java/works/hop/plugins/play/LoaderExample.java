package works.hop.plugins.play;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import works.hop.plugins.api.Pluggable;
import works.hop.plugins.config.PlugConfig;
import works.hop.plugins.loader.PluginCentral;

public class LoaderExample extends ClassLoader{

	public LoaderExample(ClassLoader parent) {
		super(parent);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Pluggable found = PluginCentral.findPlugByPlugin(name);
		if (found != null) {
			try {
				String jarUrl = PlugConfig.resolveUrl(found);
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

	public static String toFilePath(String name) {
		return name.replaceAll("\\.", "/") + ".class";
	}

	public static String getMainClass(JarURLConnection uc) throws IOException {
		Attributes attr = uc.getAttributes();
		return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	}

	public static List<String> listClasses(String jarName, String packageName) {
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
}
