package com.practicaldime.plugins.users;

import com.practicaldime.plugins.users.service.StartupService;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StartupPluginTest {

    private StartupPlugin plugin;

    @Test
    @Ignore
    public void testWorkingWithPlugin() {
        plugin = new StartupPlugin();
        plugin.load(getClass().getClassLoader());
        assertTrue("Expeting same class", StartupService.class.isAssignableFrom(plugin.target().getClass()));
    }
}
