package com.practicaldime.plugins.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Class template for loading plugins' json configurations
 *
 * @author mainas
 */
public class Pluggable {

    private String type;
    private String repository;
    private String coordinates;
    private String jarfile;
    private String plugin;
    private List<Feature> features = new ArrayList<>();
    private List<Pluggable> sources = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getJarfile() {
        return jarfile;
    }

    public void setJarfile(String jarfile) {
        this.jarfile = jarfile;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> operations) {
        this.features = operations;
    }

    public List<Pluggable> getSources() {
        return sources;
    }

    public void setSources(List<Pluggable> sources) {
        this.sources = sources;
    }
}
