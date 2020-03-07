package com.practicaldime.plugins.config;

import com.google.gson.Gson;
import com.practicaldime.plugins.api.PlugDefinition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class PlugConfig {

    private static final String CONFIG_FILE_NAME = "plugs-config.json";
    private static final String PLUGINS_DIR_NAME = "plugs";
    private static final Path PLUGINS_DIR_PATH = Paths.get(System.getProperty("user.dir"), PLUGINS_DIR_NAME).resolve(CONFIG_FILE_NAME);
    private static final Path HOME_DIR = Paths.get(System.getProperty("user.home"));
    private static PlugConfig instance;
    private PlugDefinition plugDefinition;

    private PlugConfig() {
        //singleton - hide constructor
    }

    public static PlugConfig getInstance() {
        if (instance == null) {
            synchronized (PlugConfig.class) {
                instance = new PlugConfig();
            }
        }
        return instance;
    }

    public static void resolve(PlugDefinition parent) {
        if (parent.getDefinitions().size() > 0) {
            for (Iterator<PlugDefinition> iter = parent.getDefinitions().iterator(); iter.hasNext(); ) {
                PlugDefinition athis = iter.next();
                if (athis.getType() == null) {
                    athis.setType(parent.getType());
                }
                if (athis.getRepository() == null) {
                    athis.setRepository(parent.getRepository());
                }
                resolve(athis);
            }
        }
    }

    public static String resolveUrl(PlugDefinition plug) {
        //"file:/home/user/.m2/repository/works/hop/plugins-basic/0.1/plugins-basic-0.1.jar
        StringBuilder path = new StringBuilder();
        String type = plug.getType().concat(":/");
        String repo = plug.getRepository().equals(".") ? PLUGINS_DIR_NAME : plug.getRepository();
        if (repo.contains("~")) repo = repo.replace("~", HOME_DIR.toString());
        repo = repo.replace("\\", "/");
        path.append(type).append(repo).append(plug.getCoordinates()).append("/").append(plug.getJarfile());
        return path.toString();
    }

    public static void main(String[] args) {
        PlugDefinition config = PlugConfig.getInstance().loadConfig();
        System.out.println(new Gson().toJson(config, PlugDefinition.class));
    }

    public PlugDefinition loadConfig() {
        if (plugDefinition == null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(PLUGINS_DIR_PATH.toFile()))) {
                PlugDefinition config = new Gson().fromJson(reader, PlugDefinition.class);
                resolve(config);
                this.plugDefinition = config;
            } catch (IOException e) {
                e.printStackTrace(System.err);
                return null;
            }
        }
        return plugDefinition;
    }
}
