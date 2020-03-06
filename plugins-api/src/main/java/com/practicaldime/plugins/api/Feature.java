package com.practicaldime.plugins.api;

import java.util.ArrayList;
import java.util.List;

public class Feature {

    private final List<ParamType> accepts = new ArrayList<>();
    private String name;
    private ParamType returns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParamType getReturns() {
        return returns;
    }

    public void setReturns(ParamType returns) {
        this.returns = returns;
    }

    public List<ParamType> getAccepts() {
        return accepts;
    }

    public void addParam(ParamType arg) {
        this.accepts.add(arg);
    }

    public static class ParamType {

        public final String name;
        public List<ParamType> attributes = new ArrayList<>();

        public ParamType(String name) {
            super();
            this.name = name;
        }
    }
}
