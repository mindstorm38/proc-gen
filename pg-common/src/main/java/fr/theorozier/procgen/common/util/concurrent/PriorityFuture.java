package fr.theorozier.procgen.common.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PriorityFuture<V> implements RunnableFuture<V>, PrioritySupplier {
	
	private final RunnableFuture<V> delegate;
	private final int priority;
	
	PriorityFuture(RunnableFuture<V> delegate, int priority) {
		
		this.delegate = delegate;
		this.priority = priority;
		
	}
	
	@Override
	public int getPriority() {
		return this.priority;
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
	public V get() throws InterruptedException, ExecutionException {
		return this.delegate.get();
	}
	
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.delegate.get(timeout, unit);
	}
	
	@Override
	public void run() {
		this.delegate.run();
	}
	
}
