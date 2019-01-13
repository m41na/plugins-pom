package com.practicaldime.plugins.api;

public class PlugResult<T> {

	private boolean success;
    private String status;
    private T entity;
    
    public PlugResult(T entity) {
    	this(entity, true, null);
    }

    public PlugResult(Boolean error, String status) {
    	this(null, error, status);
    }
    
    public PlugResult(T entity, Boolean error, String status) {
    	this.entity = entity;
    	this.success = error;
    	this.status = status;
    }

    public T getEntity() {
        return entity;
    }

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
