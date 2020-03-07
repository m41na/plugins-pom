package com.practicaldime.plugins.loader;

import com.practicaldime.plugins.api.PlugDefinition;
import com.practicaldime.plugins.api.PlugResult;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PluginCentralTest {

    @Test
    public void findPlugByPluginName() {
        PlugDefinition definition = PluginCentral.findPlugByPluginName("com.practicaldime.plugins.users.StartupPlugin");
        assertNotNull(definition);
        assertEquals("com.practicaldime.plugins.users.StartupPlugin", definition.getPlugin());
    }

    @Test
    public void findPlugByJarName() {
        PlugDefinition definition = PluginCentral.findPlugByJarName("plugins-users-service-0.1-shaded.jar");
        assertNotNull(definition);
        assertEquals("plugins-users-service-0.1-shaded.jar", definition.getJarfile());
    }

    @Test
    public void loadPlugin() {
        PlugDefinition definition = PluginCentral.findPlugByJarName("plugins-users-service-0.1-shaded.jar");
        assertNotNull(definition);
        PluginCentral central = new PluginCentral(Arrays.asList(definition));
        central.loadPlugin("com.practicaldime.plugins.users.StartupPlugin");
    }

    @Test
    public void runPlugin() {
        PlugDefinition definition = PluginCentral.findPlugByJarName("plugins-users-service-0.1-shaded.jar");
        assertNotNull(definition);
        PluginCentral central = new PluginCentral(Arrays.asList(definition));
        //load service first before executing feature
        central.loadPlugin("com.practicaldime.plugins.users.StartupPlugin");
        PlugResult result = central.runPlugin("com.practicaldime.plugins.users.StartupPlugin", "initialize", "");
        assertNotNull(result);
    }

    @Test
    public void invokePlugin() {
        PlugDefinition definition = PluginCentral.findPlugByJarName("plugins-users-service-0.1-shaded.jar");
        assertNotNull(definition);
        PluginCentral central = new PluginCentral(Arrays.asList(definition));
        //load service first before executing feature
        central.loadPlugin("com.practicaldime.plugins.users.StartupPlugin");
        Object result = central.invokePlugin("com.practicaldime.plugins.users.StartupPlugin", "initialize", new Class[]{}, new Object[]{});
        assertNull(result);
    }

    @Test
    public void reloadPlugin() {
        PlugDefinition definition = PluginCentral.findPlugByJarName("plugins-users-service-0.1-shaded.jar");
        assertNotNull(definition);
        PluginCentral central = new PluginCentral(Arrays.asList(definition));
        central.loadPlugin("com.practicaldime.plugins.users.StartupPlugin");
        //reload plugin
        central.reloadPlugin("com.practicaldime.plugins.users.StartupPlugin");
    }

    @Test
    public void unloadPlugin() {
        PlugDefinition definition = PluginCentral.findPlugByJarName("plugins-users-service-0.1-shaded.jar");
        assertNotNull(definition);
        PluginCentral central = new PluginCentral(Arrays.asList(definition));
        central.loadPlugin("com.practicaldime.plugins.users.StartupPlugin");
        //reload plugin
        central.unloadPlugin("com.practicaldime.plugins.users.StartupPlugin");
    }

    @Test
    public void loadPluginBytes() {
        //TODO: add test if necessary or remove the function
    }

    @Test
    public void loadPluginProxy() {
        //TODO: add test if necessary or remove the function
    }

    @Test
    public void discoverFeatures() {
        //TODO: figure out what this function accomplishes
        PlugDefinition definition = PluginCentral.findPlugByJarName("plugins-users-service-0.1-shaded.jar");
        assertNotNull(definition);
        PluginCentral central = new PluginCentral(Arrays.asList(definition));
        central.loadPlugin("com.practicaldime.plugins.users.StartupPlugin");
        //reload plugin
        central.discoverFeatures("com.practicaldime.plugins.users.StartupPlugin");
    }
}