package fr.theorozier.procgen.client.renderer.world.chunk.redraw;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Deprecated
public class ChunkRedrawFuture<T> implements Future<T> {
	
	private final ChunkRedrawData data;
	private final Future<T> delegate;
	
	public ChunkRedrawFuture(ChunkRedrawData data, Future<T> delegate) {
		this.data = data;
		this.delegate = delegate;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return this.delegate.cancel(mayInterruptIfRunning);
	}
	
	@Override
	public boolean isCancelled() {
		return this.delegate.isCancelled();
	}
	
	@Override
	public boolean isDone() {
		return this.delegate.isDone();
	}
	
	@Override
	public T get() throws InterruptedException, ExecutionException {
		return this.delegate.get();
	}
	
	@Override
	public T get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.delegate.get(timeout, unit);
	}
	
	public ChunkRedrawData getData() {
		return this.data;
	}
	
}
