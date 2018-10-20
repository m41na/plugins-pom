package works.hop.plugins.api;

public class Feature<T> {

	private String name;	
	private T accepts;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getAccepts() {
		return accepts;
	}

	public void setAccepts(T accepts) {
		this.accepts = accepts;
	}
}
