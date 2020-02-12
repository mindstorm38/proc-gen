package fr.theorozier.procgen.common.world.load;

import fr.theorozier.procgen.common.util.ThreadingDispatch;
import fr.theorozier.procgen.common.util.concurrent.PriorityThreadPoolExecutor;
import fr.theorozier.procgen.common.world.WorldDimensionManager;
import fr.theorozier.procgen.common.world.chunk.WorldServerSection;
import fr.theorozier.procgen.common.world.position.SectionPositioned;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * Basic handler for world dimensions saving and loading from files or from scratch.
 *
 * @author Theo Rozier
 *
 */
public class WorldLoadingManager {
	
	private static final ThreadingDispatch WORLD_CHUNK_LOADING_DISPATCH = ThreadingDispatch.register("WORLD_CHUNK_LOADING", 3);
	private static final long MAX_IDLE_TIME = 60000;

	private WorldDimensionManager dimensionManager = null;
	private PriorityThreadPoolExecutor loadingThreadPool = null;
	private long idleStart = 0;

	public WorldLoadingManager() { }

	/**
	 * Set active dimension manager to run, it will stop current tasks if an dimension manager is active in.<br>
	 * Should be called from main update loop.
	 * @param manager The dimension manager, or Null to only stop current dimension manager.
	 */
	public void setCurrentDimensionManager(WorldDimensionManager manager) {

		if (this.dimensionManager != null) {
			// Nothing to do for now, because thread pool shutdown is delayed.
		}

		this.dimensionManager = manager;

		if (manager != null) {

			if (this.loadingThreadPool == null) {

				LOGGER.info("Started world loading thread pool (" + WORLD_CHUNK_LOADING_DISPATCH.getEffectiveCount() + " threads) ...");
				this.loadingThreadPool = new PriorityThreadPoolExecutor(WORLD_CHUNK_LOADING_DISPATCH.getEffectiveCount(), PriorityThreadPoolExecutor.ASC_COMPARATOR);

			}


		} else if (this.loadingThreadPool != null) {
			this.idleStart = System.currentTimeMillis();
		}

	}

	/**
	 * Update world loading manager.
	 */
	public void update() {

		if (this.dimensionManager == null && this.loadingThreadPool != null && (System.currentTimeMillis() - this.idleStart) > MAX_IDLE_TIME) {

			LOGGER.info("Shutting down world loading thread pool (" + this.loadingThreadPool.getCorePoolSize() + " threads) ...");
			this.loadingThreadPool.shutdown(); // FIXME: Potential leaks if tasks never finished
			this.loadingThreadPool = null;

		}

	}

	public boolean isSectionSaved(SectionPositioned pos) {
		return false;
	}
	
	public boolean isSectionSaving(SectionPositioned pos) {
		return false;
	}
	
	public void loadSectionTo(WorldServerSection section) {
	
	}
	
	public boolean isSectionLoading(SectionPositioned pos) {
		return false;
	}

}
