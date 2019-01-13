package com.practicaldime.plugins.users;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.practicaldime.plugins.users.service.StartupService;

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
