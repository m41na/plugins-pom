package com.practicaldime.plugins.loader;

import com.practicaldime.plugins.api.PlugLifecycle;
import com.practicaldime.plugins.api.PlugDefinition;
import com.practicaldime.plugins.api.Plugin;
import com.practicaldime.plugins.config.PlugConfig;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginExample implements Runnable {

    private final PlugDefinition plug;

    public PluginExample(PlugDefinition plug) {
        super();
        this.plug = plug;
    }

    public static void main(String[] args) throws InterruptedException {
        PlugDefinition plug = PlugConfig.getInstance().loadConfig();
        Runnable task = new PluginExample(plug.getDefinitions().get(0));
        Thread thread = new Thread(task);
        thread.start();
        thread.join();
    }

    @Override
    public void run() {
        String path = PlugConfig.resolveUrl(plug);
        String plugin = plug.getPlugin();
        try {
            URL url = new URL(path);
            Reference<URLClassLoader> loader = new WeakReference<>(new URLClassLoader(new URL[]{url}));
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
                plc.onLoadError(e);
            }
            // execute plugin
            plc.beforeExecute();
            plug.execute("printTasks", null);
            plc.onExecuteSuccess();
            try {
                plc.onExecuteSuccess();
            } catch (Exception e) {
                plc.onExecuteError(e);
            }
            // unload plugin
            plc.beforeUnload();
            try {
                plug.unload();
                plc.onUnloadSuccess();
            } catch (Exception e) {
                plc.onUnloadError(e);
            }
            // close loader
            loader.get().close();

            System.out.println("Now app will reload continuously");
            while (true) {
                Thread.sleep(3000);

                // assuming plugin jar got updated, the changes should be reflect after this
                loader = new WeakReference<>(new URLClassLoader(new URL[]{url}));
                cl = Class.forName(plugin, true, loader.get());
                plug = (Plugin<?>) cl.newInstance();
                plc = plug.lifecycle();
                // load plugin
                plc.beforeLoad();
                try {
                    plug.load(loader.get());
                    plc.onLoadSuccess();
                } catch (Exception e) {
                    plc.onLoadError(e);
                }
                // execute plugin
                plc.beforeExecute();
                plug.execute("printTasks", null);
                plc.onExecuteSuccess();
                try {
                    plc.onExecuteSuccess();
                } catch (Exception e) {
                    plc.onExecuteError(e);
                }
                System.out.println("Plugin will now unload");
                // unload plugin
                plc.beforeUnload();
                try {
                    plug.unload();
                    plc.onUnloadSuccess();
                } catch (Exception e) {
                    plc.onUnloadError(e);
                }
                // close loader
                loader.get().close();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
