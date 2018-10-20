package works.hop.plugins.api;

public class PlugResult<T> {

	private boolean success;
    private String status;
    private T entity;
    
    public PlugResult(T entity, String status) {
    	this(entity, true, status);
    }

    public PlugResult(String status) {
    	this(null, false, status);
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
