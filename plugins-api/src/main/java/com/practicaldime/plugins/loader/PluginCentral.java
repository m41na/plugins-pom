package com.practicaldime.plugins.loader;

import com.practicaldime.plugins.api.*;
import com.practicaldime.plugins.config.PlugConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PluginCentral {

    private final Map<String, Plugin<?>> plugins = new HashMap<>();
    private final Map<String, URLClassLoader> loaders = new HashMap<>();
    private final List<PlugDefinition> definitions;

    public PluginCentral(List<PlugDefinition> definitions) {
        super();
        this.definitions = definitions;
        this.init();
    }

    public static PlugDefinition findPlugByPluginName(String plugin) {
        PlugDefinition config = PlugConfig.getInstance().loadConfig();
        for (PlugDefinition source : config.getDefinitions()) {
            if (source.getPlugin().equals(plugin)) {
                return source;
            }
        }
        return null;
    }

    public static PlugDefinition findPlugByJarName(String jarName) {
        PlugDefinition config = PlugConfig.getInstance().loadConfig();
        for (PlugDefinition source : config.getDefinitions()) {
            if (source.getJarfile().equals(jarName)) {
                return source;
            }
        }
        return null;
    }

    public static void main(String[] args) throws ReflectiveOperationException {
        PlugDefinition config = PlugConfig.getInstance().loadConfig();
        PluginCentral loader = new PluginCentral(config.getDefinitions());

        String plugin = "com.practicaldime.plugins.users.StartupPlugin";
        String feature = "initialize";
        loader.loadPlugin(plugin);
        loader.discoverFeatures(plugin);
        loader.runPlugin(plugin, feature, null);
        loader.reloadPlugin(plugin);
        loader.discoverFeatures(plugin);
        loader.runPlugin(plugin, feature, null);
        loader.unloadPlugin(plugin);
    }

    private void init() {
        if (definitions != null) {
            for (Iterator<PlugDefinition> iter = this.definitions.iterator(); iter.hasNext(); ) {
                PlugDefinition plug = iter.next();
                String path = PlugConfig.resolveUrl(plug);
                String name = plug.getPlugin();
                // load plugin
                try {
                    URL url = new URL(path);
                    URLClassLoader loader = loaders.get(path);
                    if (loader == null) {
                        loader = new URLClassLoader(new URL[]{url});
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
        PlugDefinition plugDefinition = findPlugByPluginName(plugin);
        if (plugDefinition != null) {
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
                    plc.onLoadError(e);
                } finally {
                    plc.onLoadComplete();
                }
            } else {
                throw new PlugException("Plugin with name '" + plugin + "' not defined");
            }
        } else {
            throw new PlugException("Plugin with name '" + plugin + "' not defined");
        }
    }

    public PlugResult<?> runPlugin(String plugin, String feature, String payload) {
        // identify plugin
        PlugDefinition plugDefinition = findPlugByPluginName(plugin);
        if (plugDefinition != null) {
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
                    plc.onExecuteError(e);
                    return new PlugResult<>(e.getMessage());
                }
                finally{
                    plc.onExecuteComplete();
                }
            } else {
                throw new PlugException("Plugin with name '" + plugin + "' not loaded");
            }
        } else {
            throw new PlugException("Plugin with name '" + plugin + "' not defined");
        }
    }

    public Object invokePlugin(String plugin, String feature, Class<?>[] params, Object[] args) {
        // identify plugin
        PlugDefinition plugDefinition = findPlugByPluginName(plugin);
        if (plugDefinition != null) {
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
                    plc.onExecuteError(e);
                    return new PlugResult<>(e.getMessage());
                }
            } else {
                throw new PlugException("Plugin with name '" + plugin + "' not loaded");
            }
        } else {
            throw new PlugException("Plugin with name '" + plugin + "' not defined");
        }
    }

    public void reloadPlugin(String plugin) {
        // identify plugin
        PlugDefinition plugDefinition = findPlugByPluginName(plugin);
        if (plugDefinition != null) {
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
                        plc.onUnloadError(e);
                    }
                    // close current loader
                    URLClassLoader loader = loaders.remove(path);
                    loader.close();
                }

                // assuming plugin jar got updated, the changes should be reflect after this
                URL url = new URL(path);
                URLClassLoader loader = new URLClassLoader(new URL[]{url});
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
                    plc.onLoadError(e);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new PlugException(e);
            }
        } else {
            throw new PlugException("Plugin with name '" + plugin + "' not defined");
        }
    }

    public void unloadPlugin(String plugin) {
        // identify plugin
        PlugDefinition plugDefinition = findPlugByPluginName(plugin);
        if (plugDefinition != null) {
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
                plc.onUnloadError(e);
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
            throw new PlugException("Plugin with name '" + plugin + "' not defined");
        }
    }

    public byte[] loadPluginBytes(String plugin) {
        try {
            // identify plugin
            PlugDefinition plugDefinition = findPlugByPluginName(plugin);
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
            // get cached plugin instance
            String pluginKey = path + "@" + name;
            Plugin<?> plug = plugins.get(pluginKey);
            if (plug != null) {
                // read bytes
                ClassReader cr = new ClassReader(plugDefinition.getPlugin());
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
            PlugDefinition plugDefinition = findPlugByPluginName(plugin);
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
            PlugDefinition plugDefinition = findPlugByPluginName(plugin);
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
            PlugDefinition plugDefinition = findPlugByPluginName(plugin);
            String path = PlugConfig.resolveUrl(plugDefinition);
            String name = plugDefinition.getPlugin();
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
}
