package works.graphql.model;

import java.util.Date;

public class Link {

	private final Long id;
	private final String url;
    private final String description;

    public Link(String url, String description) {
    	this.id = new Date().getTime();
        this.url = url;
        this.description = description;
    }
    
    public Link(Long id, String url, String description) {
        this.id = id;
        this.url = url;
        this.description = description;
    }

    public Long getId() {
		return id;
	}

	public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }
}
