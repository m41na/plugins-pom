package com.practicaldime.plugins.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import com.google.gson.Gson;

import com.practicaldime.plugins.api.Pluggable;

public class PlugConfig {
	
	private static PlugConfig instance;
	private static final String CONFIG_FILE_NAME = "plugs-config.json";
	private static final String PLUGINS_DIR_NAME = "plugs";
	private static final Path PLUGINS_DIR_PATH = Paths.get(System.getProperty("user.dir"), PLUGINS_DIR_NAME).resolve(CONFIG_FILE_NAME);
	private Pluggable pluggable;
	
	private PlugConfig() {
		//singleton - hide constructor
	}

	public static PlugConfig getInstance() {
		if(instance == null) {
			synchronized (PlugConfig.class) {
				instance = new PlugConfig();
			}
		}
		return instance;
	}

	public Pluggable loadConfig() {
		if(pluggable == null) {
			try(BufferedReader reader = new BufferedReader(new FileReader(PLUGINS_DIR_PATH.toFile()))){
				Pluggable config = new Gson().fromJson(reader, Pluggable.class);
				resolve(config);
				this.pluggable = config;
			}
			catch(IOException e) {
				e.printStackTrace(System.err);
				return null;
			}
		}
		return pluggable;
	}

	public static void resolve(Pluggable parent) {
		if(parent.getSources().size() > 0) {
			for(Iterator<Pluggable> iter = parent.getSources().iterator(); iter.hasNext();) {
				Pluggable athis = iter.next();
				if(athis.getType() == null) {
					athis.setType(parent.getType());
				}
				if(athis.getRepository() == null) {
					athis.setRepository(parent.getRepository());
				}
				resolve(athis);
			}
		}
	}
	
	public static String resolveUrl(Pluggable plug) {
		//"file:/home/user/.m2/repository/works/hop/plugins-basic/0.1/plugins-basic-0.1.jar
		StringBuilder path = new StringBuilder();
		String type = plug.getType().concat(":");
		String repo = plug.getRepository().equals(".") ? PLUGINS_DIR_NAME.toString() : plug.getRepository();
		path.append(type).append(repo).append(plug.getCoordinates()).append("/").append(plug.getJarfile());
		return path.toString();
	}
	
	public static void main(String[] args){
		Pluggable config = PlugConfig.getInstance().loadConfig();
		System.out.println(new Gson().toJson(config, Pluggable.class));
	}
}
