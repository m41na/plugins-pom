package works.graphql.app;

public class Result<T> {

	public final T data;	
	public final String error;
	
	public Result(T data) {
		this(data, null);
	}

	public Result(T data, String error) {
		super();
		this.data = data;
		this.error = error;
	}
	
	public static <R>Result<R> of(R data) {
		return new Result<R>(data);
	}
	
	public static Result<Integer> error(String error) {
		return new Result<Integer>(null, error);
	}
}
