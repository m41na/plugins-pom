package works.hop.plugins.web;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import works.hop.plugins.api.PlugException;

public class PluginOnError implements WriteListener{

	private final AsyncContext async;
	private final ServletOutputStream out;
	private final ByteBuffer content;
	
	public PluginOnError(AsyncContext async, ServletOutputStream out, String message) {
		super();
		this.async = async;
		this.out = out;
		this.content = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void onWritePossible() throws IOException {
		while (out.isReady()) {
			if (!content.hasRemaining()) {
				async.complete();
				return;
			}
			// write to response
			try {
				out.write(content.get());
			} catch (IOException e) {
				e.printStackTrace(System.err);
				throw new PlugException(e);
			}
		}
	}

	@Override
	public void onError(Throwable t) {
		t.printStackTrace(System.err);
		async.complete();
		throw new PlugException(t);
	}

}
