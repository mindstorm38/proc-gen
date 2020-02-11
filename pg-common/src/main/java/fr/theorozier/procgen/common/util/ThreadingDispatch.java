package fr.theorozier.procgen.common.util;

import io.sutil.LazyLoadValue;
import io.sutil.math.MathHelper;

import java.util.HashSet;

import static io.msengine.common.util.GameLogger.LOGGER;

public class ThreadingDispatch {
	
	private static final LazyLoadValue<Integer> AVAILABLE_CORES = new LazyLoadValue<Integer>() {
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
		
		int coresCount = getAvailableCores();
		int affectedCores = 0;
		
		for (ThreadingDispatch d : dispatches) {
			
			d.effectiveCount = MathHelper.floorFloatInt(d.ratio / (float) totalRatio * coresCount);
			affectedCores += d.effectiveCount;
			
		}
		
		while (affectedCores < coresCount) {
			for (ThreadingDispatch d : dispatches) {
				d.effectiveCount++;
				affectedCores++;
			}
		}
		
	}
	
	public static void debug() {
		
		LOGGER.info("Available cores : " + ThreadingDispatch.getAvailableCores());
		LOGGER.info("Threads dispatching :");
		for (ThreadingDispatch dispatch : ThreadingDispatch.getDispatches()) {
			LOGGER.info("- " + dispatch.getIdentifier() + " : " + dispatch.getEffectiveCount() + " threads.");
		}
	
	}
	
	// WORLD_CHUNK_LOADERS (),
	// WORLD_RENDERING ();
	
	private String identifier;
	private int ratio;
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
	
	public int getEffectiveCount() {
		return this.effectiveCount;
	}

}
