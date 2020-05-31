package fr.theorozier.procgen.common.util;

import io.sutil.LazyLoadValue;
import io.sutil.math.MathHelper;

import java.util.HashSet;
import java.util.Iterator;

import static io.msengine.common.util.GameLogger.LOGGER;

public class ThreadingDispatch {
	
	private static final LazyLoadValue<Integer> AVAILABLE_CORES = new LazyLoadValue<Integer>() {
		
		@Override
		public Integer create() {
			return Runtime.getRuntime().availableProcessors();
		}
		
	};
	
	public static int getAvailableCores() {
		return AVAILABLE_CORES.get();
	}
	
	private static final HashSet<ThreadingDispatch> dispatches = new HashSet<>();
	
	public static HashSet<ThreadingDispatch> getDispatches() {
		return dispatches;
	}
	
	public static ThreadingDispatch register(ThreadingDispatch dispatch) {
		dispatches.add(dispatch);
		return dispatch;
	}
	
	public static ThreadingDispatch register(String identifier, int ratio) {
		return register(new ThreadingDispatch(identifier, ratio));
	}
	
	public static void dispatch() {
		
		int totalRatio = dispatches.stream().mapToInt(ThreadingDispatch::getRatio).sum();
		
		if (totalRatio == 0)
			return;
		
		// Keep on core for main thread.
		int availableCores = getAvailableCores() - 1;
		int affectedCores = 0;
		
		for (ThreadingDispatch d : dispatches) {
			
			d.effectiveCount = MathHelper.floorFloatInt(d.ratio / (float) totalRatio * availableCores);
			affectedCores += d.effectiveCount;
			
		}
		
		Iterator<ThreadingDispatch> it;
		
		while (affectedCores < availableCores) {
			
			it = dispatches.iterator();
			
			while (it.hasNext() && affectedCores < availableCores) {
				it.next().effectiveCount++;
				affectedCores++;
			}
			
		}
		
	}
	
	public static void debug() {
		
		LOGGER.info("Available cores : " + ThreadingDispatch.getAvailableCores());
		LOGGER.info("Threads dispatching :");
		LOGGER.info("- MAIN_THREAD : 1 thread (native).");
		for (ThreadingDispatch dispatch : ThreadingDispatch.getDispatches()) {
			LOGGER.info("- " + dispatch.getIdentifier() + " : " + dispatch.getEffectiveCount() + " threads.");
		}
	
	}
	
	// WORLD_CHUNK_LOADERS (),
	// WORLD_RENDERING ();
	
	private final String identifier;
	private final int ratio;
	private int effectiveCount = 0;
	
	public ThreadingDispatch(String identifier, int ratio) {
		
		this.identifier = identifier;
		this.ratio = ratio;
		
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public int getRatio() {
		return this.ratio;
	}
	
	public int getEffectiveCount(int minimum) {
		return Math.max(this.effectiveCount, minimum);
	}
	
	public int getEffectiveCount() {
		return this.getEffectiveCount(1);
	}

}
