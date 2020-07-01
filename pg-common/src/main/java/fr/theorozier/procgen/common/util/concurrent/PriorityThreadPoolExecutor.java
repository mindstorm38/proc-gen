package fr.theorozier.procgen.common.util.concurrent;

import java.util.Comparator;
import java.util.concurrent.*;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
	
	private static int getPrioritySafe(Object obj) {
		return (obj instanceof PrioritySupplier) ? ((PrioritySupplier) obj).getPriority() : 0;
	}
	
	public static final Comparator<Runnable> ASC_COMPARATOR = (o1, o2) -> {

		if (o1 == null) {

			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}

		} else if (o2 == null) {
			return 1;
		} else {
			return getPrioritySafe(o1) - getPrioritySafe(o2);
		}

	};
	
	public static final Comparator<Runnable> DESC_COMPARATOR = (o1, o2) -> {
		
		if (o1 == null) {
			
			if (o2 == null) {
				return 0;
			} else {
				return 1;
			}
			
		} else if (o2 == null) {
			return -1;
		} else {
			return getPrioritySafe(o2) - getPrioritySafe(o1);
		}
		
	};
	
	public PriorityThreadPoolExecutor(int poolSize, Comparator<Runnable> comp, ThreadFactory threadFactory) {
		super(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(8, comp), threadFactory);
	}
	
	public PriorityThreadPoolExecutor(int poolSize, Comparator<Runnable> comp) {
		super(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(8, comp));
	}
	
	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
		return new PriorityFuture<>(newTaskFor, getPrioritySafe(callable));
	}
	
	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		RunnableFuture<T> newTaskFor = super.newTaskFor(runnable, value);
		return new PriorityFuture<>(newTaskFor, getPrioritySafe(runnable));
	}
	
}
