package com.practicaldime.plugins.api;

public class PlugResult<T> {

    private boolean status;
    private String error;
    private T entity;

    public PlugResult(T entity) {
        this(entity, true, null);
    }

    public PlugResult(Boolean error, String status) {
        this(null, error, status);
    }

    public PlugResult(T entity, Boolean error, String status) {
        this.entity = entity;
        this.status = error;
        this.error = status;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
